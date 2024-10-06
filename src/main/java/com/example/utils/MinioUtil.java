package com.example.utils;

import cn.hutool.core.date.DateUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/5 18:40
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Value("${minio.access-key}")
    private String ACCESS_KEY;
    @Value("${minio.secret-key}")
    private String SECRET_KEY;
    @Value("${minio.bucket-name}")
    private String BUCKET_NAME;
    private MinioClient minioClient;

    /**
     * 初始化minio
     */
    @PostConstruct
    public void init() {
        try{
            log.info("Minio Initialize--------------");
            minioClient = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY,SECRET_KEY).build();
            createBucket(BUCKET_NAME);
            log.info("Minio Initialized------------------successful");
        }catch (Exception e){
            e.printStackTrace();
            log.error("Minio Initialize------------failed");
        }
    }

    /**
     * 创建箱子
     * @param bucketName
     */
    @SneakyThrows(Exception.class)
    private void createBucket(String bucketName) {
        if (!bucketExists(bucketName)){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 判断箱子是否存在
     * @param bucketName
     * @return
     */
    @SneakyThrows(Exception.class)
    private boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取全部bucket
     */
    @SneakyThrows(Exception.class)
    public List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 上传文件,可直接访问url
     */
    public String uploadFile(MultipartFile file) {
        try{
            if (!bucketExists(BUCKET_NAME)){
                createBucket(BUCKET_NAME);
            }

            //防止重名
            String fileName = file.getOriginalFilename();
            String originalFilename = fileName + "-" + DateUtil.format(new Date(),"yyyy-MM-dd- HH:mm:ss");
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(originalFilename)
                            .stream(file.getInputStream(),file.getSize(),-1)
                            .contentType(file.getContentType())
                    .build());
            return getPreviewFileUrl(originalFilename);
        } catch (Exception e){
            e.printStackTrace();
            log.error("上传文件异常: 【{}】", e.fillInStackTrace());
            return "你不行啊！(^-^)(o^^o)";
        }
    }

    /**
     * 获取minio文件的下载或者预览地址
     * 取决于调用本方法的方法中的PutObjectOptions对象有没有设置contentType
     * @param originalFilename
     * @return
     */
    @SneakyThrows(Exception.class)
    private String getPreviewFileUrl(String originalFilename) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(BUCKET_NAME)
                        .object(originalFilename)
                        .expiry(24, TimeUnit.HOURS)
                        .build()
        );
    }

    /**
     * 删除文件
     */
    @SneakyThrows(Exception.class)
    public void deleteFile(String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(BUCKET_NAME).object(fileName).build());
    }

    /**
     * 截取url地址
     * @param url
     * @return
     */
    public static String getMinioFileName(String url) {
        int endIndex = url.contains("?") ? url.indexOf("?") : url.length();
        return url.substring(url.lastIndexOf("/") + 1,endIndex);
    }


}
