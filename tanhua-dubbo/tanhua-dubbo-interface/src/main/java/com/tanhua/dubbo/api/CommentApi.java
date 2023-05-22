package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;


public interface CommentApi {
    //发布评论并获取评论数量
    Integer save(Comment comment1);

    //分页查询评论列表
    List<Comment> findComments(String movementId, CommentType commentType, Integer page, Integer pagesize);

    //动态点赞
    Boolean hasComment(String movementId, Long userId, CommentType commentType);
}
