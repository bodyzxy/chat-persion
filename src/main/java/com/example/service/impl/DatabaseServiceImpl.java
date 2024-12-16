package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.model.Database;
import com.example.model.User;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRepository;
import com.example.service.DatabaseService;
import com.example.thread.UserHolder;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/30 21:02
 */
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final DatabaseRepository databaseRepository;
    private final UserRepository userRepository;

    @Override
    public Long createDatabase(Long userId, String name) {
        Date date = new Date();
//        String name = date.toString();
        User user = userRepository.findById(userId).orElse(null);
        Database database = new Database(user, name, date);
        Database database1 = databaseRepository.save(database);
        //同步数据到user
        assert user != null;
        user.addDatabaseAndMinioFile(database);
        return database1.getId();
    }

    @Override
    public BaseResponse shareDatabase(Long id) {
        Database database = databaseRepository.findById(id).get();
        if(database.getIsDeleted()){
            return ResultUtils.error(ErrorCode.DATABASE_ERROR);
        }
        database.setIsPublic(true);
        return ResultUtils.success("公开成功");
    }

    @Override
    public BaseResponse delete(Long id) {
        Database database = databaseRepository.findById(id).get();
        database.setIsDeleted(false);
        return ResultUtils.success("删除成功");
    }

    @Override
    public BaseResponse getShareDatabase() {
        List<Database> databases = databaseRepository.findByIsPublicTrue();
        if (databases.isEmpty()) {
            return ResultUtils.error(ErrorCode.DATABASE_NULL);
        }
        return ResultUtils.success(databases);
    }

    @Override
    public BaseResponse getUserDataBase(Long userId) {
        User user = UserHolder.getUser();
        if (Objects.equals(user.getId(), userId)){
            List<Database> databases = databaseRepository.findAllByUserId(userId);
            return ResultUtils.success(databases);
        }
        List<Database> databases = databaseRepository.findAllByUserId(userId);
        //条件移除
        databases.removeIf(database -> !database.getIsPublic());
        return ResultUtils.success(databases);
    }

    @Override
    public BaseResponse getHotDatabase() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("number")));
        List<Database> databases = databaseRepository.findAll(pageable).getContent();
        return ResultUtils.success(databases);
    }
}
