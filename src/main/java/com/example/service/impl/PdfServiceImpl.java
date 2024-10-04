package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.CustomPgVectorStore;
import com.example.component.ErrorCode;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.PdfService;
import com.example.thread.UserHolder;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

            // 使用 TikaDocumentReader 替代 ParagraphPdfDocumentReader
            TikaDocumentReader reader = new TikaDocumentReader(resource);

            VectorStore vectorStore = randomVectorStore(id);
            TokenTextSplitter splitter = new TokenTextSplitter();

            vectorStore.accept(splitter.apply(reader.get()));
            return ResultUtils.success("添加成功");
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtils.error(ErrorCode.UPDATE_ERROR);
    }

    private VectorStore randomVectorStore(Long id){
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl+"/files", defaultApiKey);
//        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
        OpenAiEmbeddingModel openAiEmbeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return new CustomPgVectorStore(jdbcTemplate,openAiEmbeddingModel,id);
    }
}
