package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.CustomPgVectorStore;
import com.example.component.ErrorCode;
import com.example.model.Request.PdfRequest;
import com.example.repository.UserRepository;
import com.example.service.PdfService;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

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
    public BaseResponse updatePdf(PdfRequest pdfRequest) {
        try{
            MultipartFile file = pdfRequest.getFile();
            String username = pdfRequest.getUsername();
            Long id = userRepository.findIdByUsername(username);
            String fileName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Files.createTempFile("temp-",fileName);
            Files.write(path,bytes);

            FileSystemResource resource = new FileSystemResource(path.toFile());

            //阅读pdf
            PdfDocumentReaderConfig readerConfig = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(
                            new ExtractedTextFormatter.Builder()
                                    .withNumberOfTopPagesToSkipBeforeDelete(1)
                                    .withNumberOfBottomTextLinesToDelete(3)
                                    .build()
                    )
                    .withPagesPerDocument(1)
                    .build();

            ParagraphPdfDocumentReader reader = new ParagraphPdfDocumentReader(resource,readerConfig);

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
        OpenAiApi openAiApi = new OpenAiApi(defaultBaseUrl, defaultApiKey);
        EmbeddingClient embeddingClient = new OpenAiEmbeddingClient(openAiApi);
        return new CustomPgVectorStore(jdbcTemplate,embeddingClient,id);
    }
}
