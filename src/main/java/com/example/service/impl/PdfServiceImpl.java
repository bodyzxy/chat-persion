package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.CustomPgVectorStore;
import com.example.component.ErrorCode;
import com.example.model.Database;
import com.example.model.MinioFile;
import com.example.model.Request.FileUpdate;
import com.example.model.Request.QueryFileRequest;
import com.example.model.User;
import com.example.repository.DatabaseRepository;
import com.example.repository.MinioFileRepository;
import com.example.repository.UserRepository;
import com.example.service.PdfService;
import com.example.thread.UserHolder;
import com.example.utils.MinioUtil;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.document.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 20:59
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String defaultApiKey;
    @Value("${spring.ai.openai.base-url}")
    private String defaultBaseUrl;
    private final UserRepository userRepository;
    private final MinioUtil minioUtil;
    private final MinioFileRepository minioFileRepository;
    private final DatabaseRepository databaseRepository;

    /**
     * 文件上传
     * @param fileUpdate
     * @return
     */
    @Override
    public BaseResponse updatePdf(FileUpdate fileUpdate) {
        try{
            User user = UserHolder.getUser();
            MultipartFile file = fileUpdate.file();
            String username = user.getUsername();
            Long id = userRepository.findIdByUsername(username);
            String fileName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Files.createTempFile("temp-",fileName);
            Files.write(path,bytes);

            FileSystemResource resource = new FileSystemResource(path.toFile());

            VectorStore vectorStore = randomVectorStore(id,fileUpdate.databaseId());

            // 使用 TikaDocumentReader 替代 ParagraphPdfDocumentReader
            TikaDocumentReader reader = new TikaDocumentReader(resource);

            List<Document> documents = reader.get();
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> applyList = splitter.apply(documents);

            vectorStore.accept(applyList);

            //此处将文件存入minio可以获取文件名
            String url = minioUtil.uploadFile(file);
            log.info(url);
            long time = System.currentTimeMillis();
            MinioFile savedMinioFile = minioFileRepository.save(MinioFile.builder()
                            .fileName(fileName)
                            .vectorId(applyList.stream().map(Document::getId).collect(Collectors.toList()))
                            .url(url)
//                            .userId(user)
                            .createTime(new Date(time))
                            .updateTime(new Date(time))
                    .build());

            Database database = databaseRepository.findById(id).orElse(null);
            if(database == null){
                return ResultUtils.error(ErrorCode.UPDATE_ERROR);
            }
            //建立数据库和文件的关联
            database.setFileId(savedMinioFile.getId());

            return ResultUtils.success("添加成功");
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtils.error(ErrorCode.UPDATE_ERROR);
    }

    /**
     * 文件分页
     * @param id  用户id
     * @param databaseId  数据库id
     * @return
     */
    private VectorStore randomVectorStore(Long id,Long databaseId){
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl, defaultApiKey);
        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//        OpenAiEmbeddingModel openAiEmbeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return new CustomPgVectorStore(jdbcTemplate,embeddingClient,id,databaseId);
    }

    @Override
    public VectorStore randomVectorStore(){
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl, defaultApiKey);
        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//        OpenAiEmbeddingModel openAiEmbeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return new PgVectorStore(jdbcTemplate,embeddingClient);
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @Override
    public BaseResponse contents(QueryFileRequest request) {
        Page<MinioFile> filePage = minioFileRepository.findByUserIdContaining(request.userId(), PageRequest.of(request.page(), request.pageSize()));
        return ResultUtils.success(filePage);
    }

    /**
     * 删除文件
     * @param id
     * @return
     */
    @Override
    public BaseResponse deleteFile(Long id) {

        Optional<MinioFile> file = minioFileRepository.findById(id);
        MinioFile processedFile = file.orElseGet(() -> {
            return null;
        });

        if (processedFile != null) {
            //先删除对应用户中的信息
//            User user = processedFile.getUserId();
//            if (user != null) {
//                user.getMinioFiles().remove(processedFile);
//            }
            minioFileRepository.delete(processedFile);
            VectorStore vectorStore = randomVectorStore();
            String minioFilename = MinioUtil.getMinioFileName(processedFile.getUrl());
            minioUtil.deleteFile(minioFilename);
            vectorStore.delete(processedFile.getVectorId());
            return ResultUtils.success("删除成功");
        } else {
            return ResultUtils.error(ErrorCode.PAGE_ERROR);
        }
    }


//    @Override
//    public BaseResponse contentsAll(Long id) {
//        Page<MinioFile> allContens = minioFileRepository.find
//        return null;
//    }
}
