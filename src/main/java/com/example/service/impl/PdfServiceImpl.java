package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.CustomPgVectorStore;
import com.example.component.ErrorCode;
import com.example.model.MinioFile;
import com.example.model.Request.QueryFileRequest;
import com.example.model.User;
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

    @Override
    public BaseResponse updatePdf(MultipartFile file) {
        try{
            User user = UserHolder.getUser();
            String username = user.getUsername();
            Long id = userRepository.findIdByUsername(username);
            String fileName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Files.createTempFile("temp-",fileName);
            Files.write(path,bytes);

            FileSystemResource resource = new FileSystemResource(path.toFile());

            VectorStore vectorStore = randomVectorStore(id);

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
            minioFileRepository.save(MinioFile.builder()
                            .fileName(fileName)
                            .vectorId(applyList.stream().map(Document::getId).collect(Collectors.toList()))
                            .url(url)
                            .userId(id)
                            .createTime(new Date(time))
                            .updateTime(new Date(time))
                    .build());
            return ResultUtils.success("添加成功");
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtils.error(ErrorCode.UPDATE_ERROR);
    }

    private VectorStore randomVectorStore(Long id){
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl, defaultApiKey);
        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//        OpenAiEmbeddingModel openAiEmbeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return new CustomPgVectorStore(jdbcTemplate,embeddingClient,id);
    }

    private VectorStore randomVectorStore(){
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl, defaultApiKey);
        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
//        OpenAiEmbeddingModel openAiEmbeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return new PgVectorStore(jdbcTemplate,embeddingClient);
    }

    @Override
    public BaseResponse contents(QueryFileRequest request) {
        Page<MinioFile> filePage = minioFileRepository.findByUserIdContaining(request.userId(), PageRequest.of(request.page(), request.pageSize()));
        return ResultUtils.success(filePage);
    }

    @Override
    public BaseResponse deleteFile(Long id) {

        Optional<MinioFile> file = minioFileRepository.findById(id);
        MinioFile processedFile = file.orElseGet(() -> {
            return null;
        });

        if (processedFile != null) {
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
}
