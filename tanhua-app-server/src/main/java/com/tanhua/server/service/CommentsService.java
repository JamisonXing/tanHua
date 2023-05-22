package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommentsService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    //发布评论
    public void saveComments(String movementId, String comment) {
        //1. 获取用户id
        Long userId = UserHolder.getUserId();
        //2. 构造Comment
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setUserId(userId);
        comment1.setCreated(System.currentTimeMillis());
        //3. 调用Api保存评论
        Integer commentCount = commentApi.save(comment1);
        log.info("commentCount = " + commentCount);
    }

    //分页查询评论列表
    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        //1. 调用api查询评论列表
        List<Comment> list = commentApi.findComments(movementId, CommentType.COMMENT, page, pagesize);
        //2. 判断list集合是否存在
        if(CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //3. 提取所有的用户id，调用userInfoAPI查询用户详情
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //4. 构造vo对象
        List<CommentVo> vos = new ArrayList<>();
        for(Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if(userInfo != null) {
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        //5. 构造返回值
        return new PageResult(page, pagesize, 0L, vos);
    }
}
