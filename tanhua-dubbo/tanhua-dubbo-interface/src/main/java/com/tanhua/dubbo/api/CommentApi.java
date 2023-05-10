package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Comment;

public interface CommentApi {
    //发布评论并获取评论数量
    Integer save(Comment comment);
}
