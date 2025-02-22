package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.model.Request.VideoRequest;
import com.example.model.Title;
import com.example.model.User;
import com.example.model.response.CustomMenuOption;
import com.example.repository.TitleRepository;
import com.example.service.TitleService;
import com.example.thread.UserHolder;
import com.example.utils.MinioUtil;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/13 19:18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TitleServiceImpl implements TitleService {

    private final TitleRepository titleRepository;
    private final MinioUtil minioUtil;

    @Override
    public BaseResponse getTitle() {
        List<Title> titles = titleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Title::getGrade))
                .toList()
                ;
        List<CustomMenuOption> customMenu = titles.stream().map(title -> new CustomMenuOption(title.getName(), title.getKey(), title.getUrl()))
                .toList();
        return ResultUtils.success(customMenu);
    }

    @Override
    public BaseResponse uploadVideo(MultipartFile file, Long id) {
        try{
            Optional<Title> optionalTitle = titleRepository.findById(id);
            if (optionalTitle.isEmpty()) {
                return ResultUtils.error(ErrorCode.NOT_ERROR);
            }

            Title title = optionalTitle.get();

            String fileName = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            String url = minioUtil.uploadFile(file);
            log.info(url);

            // 更新 Title 的 videoUrl 字段
            title.setUrl(url);
            titleRepository.save(title);

            return ResultUtils.success(title);
        }catch (Exception e){
            return ResultUtils.error(ErrorCode.ERROR);
        }
    }
}
