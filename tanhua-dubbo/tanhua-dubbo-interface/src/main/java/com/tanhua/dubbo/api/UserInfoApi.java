package com.tanhua.dubbo.api;

import com.tanhua.model.domain.UserInfo;

public interface UserInfoApi {

    public void save(UserInfo userInfo);

    public void update(UserInfo userInfo);

    //根据id查询
    UserInfo findById(Long id);
}
