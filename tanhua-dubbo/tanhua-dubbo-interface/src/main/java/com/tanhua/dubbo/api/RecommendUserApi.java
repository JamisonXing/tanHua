package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserId);

    //分页查询
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);

    //根据操作人id和查看的用户id，查询两者的推荐数据
    RecommendUser queryByUserId(Long userId, Long userId1);
}
