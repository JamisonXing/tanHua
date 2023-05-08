package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MovementService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 发布动态
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1、判断发布动态的内容是否存在
        if(StringUtils.isEmpty(movement.getTextContent())) {
            throw  new BusinessException(ErrorResult.contentError());
        }
        //2、获取当前登录的用户id
        Long userId = UserHolder.getUserId();
        //3、将文件内容上传到阿里云OSS，获取请求地址
        List<String> medias = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            medias.add(upload);
        }
        //4、将数据封装到Movement对象
        movement.setUserId(userId);
        movement.setMedias(medias);
        //5、调用API完成发布动态
        movementApi.publish(movement);
    }

    //查询个人动态
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //1、根据用户id，调用API查询个人动态内容（PageResult  -- Movement）
        PageResult pr = movementApi.findByUserId(userId,page,pagesize);
        //2、获取PageResult中的item列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        //3、非空判断
        if(items == null) {
            return pr;
        }
        //4、循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement item : items) {
            //5、一个Movement构建一个Vo对象
            MovementsVo vo = MovementsVo.init(userInfo, item);
            vos.add(vo);
        }
        //6、构建返回值
        pr.setItems(vos);
        return pr;
    }

    //查询好友动态
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        //1. 获取当前用户id
        Long userId = UserHolder.getUserId();
        //2. 调用API查询当前好友发布的动态列表
        List<Movement> list = movementApi.findFriendMovements(page, pagesize, userId);
        //3. 判断列表是否为空
        if(CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //4. 提取动态发布人的id列表
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //5. 根据用户的ID列表获取用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //6. 一个movement构造一个vo对象
        List<MovementsVo> vos = new ArrayList<>();
        for(Movement movement : list) {
            UserInfo userInfo = map.get(movement.getUserId());
            if(userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //7. 构造PageResult并返回
        return new PageResult(page, pagesize, 0L, vos);
    }
}
