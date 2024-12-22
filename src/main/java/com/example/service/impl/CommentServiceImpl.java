package com.example.service.impl;

import com.example.component.BaseResponse;
import com.example.model.Comment;
import com.example.model.Request.UserComment;
import com.example.repository.CommentRepository;
import com.example.service.CommentService;
import com.example.utils.ResultUtils;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 21:17
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public BaseResponse getDatabaseComment(Long id) {
        List<Comment> comments = commentRepository.findAllById(Collections.singleton(id));
        //最终结果
        List<Comment> result = new ArrayList<>();

        Map<Long, Comment> map = new HashMap<>();
        for (Comment comment : comments) {
            map.put(comment.getId(), comment);
        }
        //嵌套数据
        for (Comment comment : comments) {
            if(comment.getPid() == 0){
                result.add(comment);
            } else {
                Comment comment1 = map.get(comment.getPid());
                comment1.getReplies().add(comment);
            }
        }
        result.sort(Comparator.comparing(Comment::getCreateTime));
        return ResultUtils.success(result);
    }

    @Override
    public BaseResponse addComment(UserComment userComment) {
        if (userComment.toUserName() == null){
            Comment comment = Comment.builder()
                    .pid(0L)
                    .content(userComment.content())
                    .createTime(new Date())
                    .databaseId(userComment.databaseId())
                    .userName(userComment.userName())
                    .build();
            commentRepository.save(comment);
            return ResultUtils.success("首次评论成功");
        }
        Comment comment = Comment.builder()
                .pid(2L)
                .content(userComment.content())
                .createTime(new Date())
                .databaseId(userComment.databaseId())
                .userName(userComment.userName())
                .toUserName(userComment.toUserName())
                .build();
        commentRepository.save(comment);
        return ResultUtils.success("嵌套评论成功");
    }
}
