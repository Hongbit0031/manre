package io.linfeng.modules.app.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.vo.app.ApplyInfoResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.TopicEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.admin.service.TopicService;
import io.linfeng.modules.app.entity.UserTopicEntity;
import io.linfeng.modules.app.param.ApplyForm;
import io.linfeng.modules.app.param.ApplyListForm;
import io.linfeng.modules.app.service.TopicAdminService;
import io.linfeng.modules.app.service.UserTopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.app.dao.TopicApplyDao;
import io.linfeng.modules.app.entity.TopicApplyEntity;
import io.linfeng.modules.app.service.TopicApplyService;
import org.springframework.transaction.annotation.Transactional;


@Service("topicApplyService")
public class TopicApplyServiceImpl extends ServiceImpl<TopicApplyDao, TopicApplyEntity> implements TopicApplyService {


    @Autowired
    private TopicAdminService topicAdminService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private UserTopicService userTopicService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private MessageService messageService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TopicApplyEntity> page = this.page(
                new Query<TopicApplyEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean getApplyInfoByUserId(Integer uid, Integer id) {
        List<TopicApplyEntity> list = this.lambdaQuery()
                .eq(TopicApplyEntity::getUid,uid)
                .eq(TopicApplyEntity::getTopicId, id)
                .eq(TopicApplyEntity::getStatus, 0)
                .list();
        return list.size()>0;
    }

    @Override
    public AppPageUtils<ApplyInfoResponse> applyInfoList(ApplyListForm request, AppUserEntity user) {
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), request.getTopicId());
        if(!isAdmin){
            throw new LinfengException("你不是圈子管理员！");
        }
        LambdaQueryWrapper<TopicApplyEntity> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(TopicApplyEntity::getStatus,request.getType());
        wrapper.eq(TopicApplyEntity::getTopicId,request.getTopicId());
        wrapper.orderByDesc(TopicApplyEntity::getId);
        Page<TopicApplyEntity> page = new Page<>(request.getPage(),request.getLimit());
        Page<TopicApplyEntity> pages = this.page(page, wrapper);
        List<TopicApplyEntity> data = pages.getRecords();
        List<ApplyInfoResponse> responseList=new ArrayList<>();
        data.forEach(item->{
            ApplyInfoResponse response=new ApplyInfoResponse();
            AppUserShortInfoResponse res=new AppUserShortInfoResponse();
            BeanUtils.copyProperties(item,response);
            AppUserEntity appUser = appUserService.getById(response.getUid());
            BeanUtils.copyProperties(appUser,res);
            response.setUserInfo(res);
            responseList.add(response);
        });
        return new AppPageUtils<>(pages, responseList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreeApply(ApplyForm param, AppUserEntity user) {
        TopicApplyEntity topicApplyEntity = this.getById(param.getId());
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), topicApplyEntity.getTopicId());
        if(!isAdmin){
            throw new LinfengException("你不是圈子管理员！");
        }
        topicApplyEntity.setStatus(1);
        topicApplyEntity.setOperateId(user.getUid());
        topicApplyEntity.setUpdateTime(DateUtil.nowDateTime());
        boolean b = this.saveOrUpdate(topicApplyEntity);
        if(!b){
            throw new LinfengException("更新失败");
        }
        //进圈操作
        UserTopicEntity userTopicEntity=new UserTopicEntity();
        userTopicEntity.setCreateTime(DateUtil.nowDateTime());
        userTopicEntity.setTopicId(topicApplyEntity.getTopicId());
        userTopicEntity.setUid(topicApplyEntity.getUid());
        userTopicService.save(userTopicEntity);
        //圈子人数加一
        TopicEntity topicEntity = topicService.getById(topicApplyEntity.getTopicId());
        topicEntity.setUserNum(topicEntity.getUserNum()+1);
        topicService.updateById(topicEntity);
        //消息通知
        String content = StrUtil.format(Constant.TOPIC_APPLY_SUCCESS,topicEntity.getTopicName());
        messageService.sendMessageNotAsync(0,topicApplyEntity.getUid(),0,Constant.SYSMSG,content,Constant.TITLE_TOPIC);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectApply(ApplyForm param, AppUserEntity user) {
        TopicApplyEntity topicApplyEntity = this.getById(param.getId());
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), topicApplyEntity.getTopicId());
        if(!isAdmin){
            throw new LinfengException("你不是圈子管理员！");
        }
        topicApplyEntity.setStatus(2);
        topicApplyEntity.setOperateId(user.getUid());
        topicApplyEntity.setUpdateTime(DateUtil.nowDateTime());
        boolean b = this.saveOrUpdate(topicApplyEntity);
        if(!b){
            throw new LinfengException("更新失败");
        }
        //消息通知
        TopicEntity topicEntity = topicService.getById(topicApplyEntity.getTopicId());
        String content = StrUtil.format(Constant.TOPIC_APPLY_FAIL,topicEntity.getTopicName());
        messageService.sendMessageNotAsync(0,topicApplyEntity.getUid(),0,Constant.SYSMSG,content,Constant.TITLE_TOPIC);

    }

    @Override
    public Integer checkTopicApplyByAdmin(Integer topicId, AppUserEntity user) {
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), topicId);
        if(!isAdmin){
            return 0;
        }
        List<TopicApplyEntity> list = this.lambdaQuery()
                .eq(TopicApplyEntity::getTopicId, topicId)
                .eq(TopicApplyEntity::getStatus, 0)
                .list();
        return list.size();
    }

}