package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.model.Database;
import com.example.model.User;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRepository;
import com.example.service.DatabaseService;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public Long createDatabase(Long userId) {
        Date date = new Date();
        String name = date.toString();
        Database database = new Database(userId, name, date);
        Database database1 = databaseRepository.save(database);
        User user = userRepository.findById(userId).get();
        //同步数据到user
        user.addDatabaseAndMinioFile(database);
        return database1.getId();
    }

    @Override
    public BaseResponse shareDatabase(Long id) {
        Database database = databaseRepository.findById(id).get();
        if(database.getIsDeleted() == true){
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
}
