package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

public interface MovementApi {

    //发布动态
    void publish(Movement movement);

    //根据用户id，查询此用户发布的动态数据列表
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}
