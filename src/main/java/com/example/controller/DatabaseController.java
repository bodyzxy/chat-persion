package com.example.controller;

import com.example.component.BaseResponse;
import com.example.model.Request.DatabasePageReq;
import com.example.service.DatabaseService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 21:47
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)//跨域共享，maxAge是最大缓存时间
@RequestMapping("/database")
@Tag(name = "DatabaseController",description = "数据库接口")
@ApiSupport(author = "bodyzxy")
public class DatabaseController {

    private final DatabaseService databaseService;

    /**
     * 创建数据库
     * @param id 用户ID
     * @param name 数据库名
     * @return
     */
    @GetMapping("/create/{id}/{name}")
    @Operation(summary = "create",description = "创建数据库")
    public Long createDatabase(@PathVariable Long id, @PathVariable String name){
        return databaseService.createDatabase(id, name);
    }

    /**
     * 公开数据库
     * @param id
     * @return
     */
    @GetMapping("/shareDatabase/{id}")
    @Operation(summary = "shareDatabase",description = "公开数据库")
    public BaseResponse shareDatabase(@PathVariable Long id){
        return databaseService.shareDatabase(id);
    }

    /**
     * 获取公开的数据库
     * @return
     */
    @PostMapping("/getShareDatabase")
    @Operation(summary = "getShareDatabase", description = "获取公开的数据库分页查询")
    public BaseResponse getShareDatabase(@RequestBody DatabasePageReq databasePageReq){
        return databaseService.getShareDatabase(databasePageReq);
    }

    /**
     * 获取用户的数据库
     * @param userId
     * @return
     */
    @GetMapping("/getUserData/{userId}")
    @Operation(summary = "getUserDataBase", description = "获取某位用户的数据库")
    public BaseResponse getUserDataBase(@PathVariable Long userId){
        return databaseService.getUserDataBase(userId);
    }

    @GetMapping("/getHotDatabase")
    @Operation(summary = "getHotDatabase", description = "获取热门数据库")
    public BaseResponse getHotDatabase(){
        return databaseService.getHotDatabase();
    }

    /**
     * 删除数据库
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @Operation(summary = "delete", description = "删除数据库")
    public BaseResponse deleteDatabase(@PathVariable Long id){
        return databaseService.delete(id);
    }
}
