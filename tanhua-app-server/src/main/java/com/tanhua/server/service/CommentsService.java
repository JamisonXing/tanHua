package com.tanhua.server.service;

import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentsService {
    @DubboReference
    private CommentApi commentApi;

    //发布评论
    public void saveComments(String movementId, String comment) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();
        //2. 构造Comment
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setCreated(System.currentTimeMillis());
        //3. 调用Api保存评论
        Integer commentCount = commentApi.save(comment1);
        log.info("commentCount = " + commentCount);
    }
}
