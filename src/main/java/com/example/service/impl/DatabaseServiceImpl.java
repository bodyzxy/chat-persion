package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.model.Database;
import com.example.model.Request.DatabasePageReq;
import com.example.model.User;
import com.example.model.response.DataBaseInfo;
import com.example.repository.DatabaseRepository;
import com.example.repository.UserRepository;
import com.example.service.DatabaseService;
import com.example.thread.UserHolder;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        databaseRepository.save(database);
        return ResultUtils.success("公开成功");
    }

    @Override
    public BaseResponse delete(Long id) {
        Database database = databaseRepository.findById(id).get();
        database.setIsDeleted(true);
        databaseRepository.save(database);
        return ResultUtils.success("删除成功");
    }

    @Override
    public BaseResponse getShareDatabase(DatabasePageReq databasePageReq) {
        Pageable pageable = PageRequest.of(databasePageReq.page(), databasePageReq.pageSize());

        Page<Database> databases = databaseRepository.findByIsPublicTrueAndIsDeletedFalse(pageable);
        if (databases.isEmpty()) {
            return ResultUtils.error(ErrorCode.DATABASE_NULL);
        }
        // 使用 Stream API 将 Database 转换为 DataBaseInfo
        Page<DataBaseInfo> databaseInfoPage = databases.map(database -> {
            DataBaseInfo info = new DataBaseInfo();
            info.setId(database.getId());
            info.setName(database.getUser().getUsername());
            info.setTitle(database.getName());
            info.setTime(database.getDate()); // 假设 `Database` 有 `getCreateTime()`
            return info;
        });
        return ResultUtils.success(databaseInfoPage);
    }

    @Override
    public BaseResponse getUserDataBase(Long userId) {
        User user = UserHolder.getUser();
        List<Database> databases = databaseRepository.findAllByUserId(userId);
        databases.removeIf(database -> database.getIsDeleted());
        // 如果不是当前用户，移除非公开的数据库
        if (!Objects.equals(user.getId(), userId)) {
            databases.removeIf(database -> !database.getIsPublic());
        }

        // 转换数据库列表为 DataBaseInfo 列表
        List<DataBaseInfo> dataBaseInfoList = convertToDataBaseInfoList(databases, userId);

        return ResultUtils.success(dataBaseInfoList);
    }

    @Override
    public BaseResponse getHotDatabase() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("number")));
        List<Database> databases = databaseRepository.findAll(pageable).getContent();
        return ResultUtils.success(databases);
    }

    private List<DataBaseInfo> convertToDataBaseInfoList(List<Database> databases, Long userId) {
        return databases.stream().map(db -> {
            DataBaseInfo dataBaseInfo = new DataBaseInfo();
            dataBaseInfo.setId(db.getId());
            dataBaseInfo.setName(db.getUser().getUsername());
            dataBaseInfo.setTitle(db.getName());
            dataBaseInfo.setTime(db.getDate());
            return dataBaseInfo;
        }).collect(Collectors.toList());
    }
}


