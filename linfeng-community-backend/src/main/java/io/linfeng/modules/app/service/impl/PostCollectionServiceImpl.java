/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.app.PostCountResponse;
import io.linfeng.common.vo.app.PostIsCollectionResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.MessageEntity;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.app.param.AddCollectionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.app.dao.PostCollectionDao;
import io.linfeng.modules.app.entity.PostCollectionEntity;
import io.linfeng.modules.app.service.PostCollectionService;


@Service("postCollectionService")
public class PostCollectionServiceImpl extends ServiceImpl<PostCollectionDao, PostCollectionEntity> implements PostCollectionService {

    @Autowired
    private MessageService messageService;
    @Autowired
    private PostCollectionDao postCollectionDao;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PostCollectionEntity> page = this.page(
                new Query<PostCollectionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer collectCount(Integer postId) {
        Object hashValue = redisUtils.getHashValue(RedisKeys.getPostKey(postId), ConfigConstant.POST_STAR_NUM);
        if(hashValue==null){
            Integer num = baseMapper.selectCount(
                    new LambdaQueryWrapper<PostCollectionEntity>()
                            .eq(PostCollectionEntity::getPostId, postId)).intValue();
            redisUtils.hashAdd(RedisKeys.getPostKey(postId), ConfigConstant.POST_STAR_NUM,num.toString());
            return num;
        }else{
            return Integer.valueOf(hashValue.toString());
        }
    }

    @Override
    public Boolean isCollection(Integer uid, Integer postId) {
        PostCollectionEntity entity = baseMapper.selectOne(
                new LambdaQueryWrapper<PostCollectionEntity>()
                .eq(PostCollectionEntity::getPostId, postId)
                .eq(PostCollectionEntity::getUid, uid));
        return entity != null;
    }

    @Override
    public void cancelCollection(AddCollectionForm request, AppUserEntity user) {
        baseMapper.delete(new LambdaQueryWrapper<PostCollectionEntity>()
                .eq(PostCollectionEntity::getPostId,request.getId())
                .eq(PostCollectionEntity::getUid,user.getUid()));
        //redis缓存同步
        redisUtils.hashChange(RedisKeys.getPostKey(request.getId()),ConfigConstant.POST_STAR_NUM,-1);
        //清理点赞消息通知
        LambdaQueryWrapper<MessageEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageEntity::getPostId,request.getId());
        queryWrapper.eq(MessageEntity::getType,Constant.STAR);
        queryWrapper.eq(MessageEntity::getFromUid,request.getUid());
        messageService.remove(queryWrapper);
    }

    @Override
    public List<Integer> getPostListByUid(Integer uid) {
        LambdaQueryWrapper<PostCollectionEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(PostCollectionEntity::getPostId);
        lambdaQueryWrapper.in(PostCollectionEntity::getUid, uid);
        List<PostCollectionEntity> postCollectionEntities = baseMapper.selectList(lambdaQueryWrapper);
        return postCollectionEntities.stream().map(PostCollectionEntity::getPostId).collect(Collectors.toList());
    }

    @Override
    public List<PostCountResponse> findBatchCollectCount(List<Integer> list) {
        List<Integer> resultList=new ArrayList<>();
        List<PostCountResponse> cacheCollectList =new ArrayList<>();
        list.forEach(postId->{
            Object hashValue = redisUtils.getHashValue(RedisKeys.getPostKey(postId), ConfigConstant.POST_STAR_NUM);
            if(hashValue==null){
                resultList.add(postId);
            }else{
                PostCountResponse response=new PostCountResponse();
                response.setNumber(Integer.valueOf(hashValue.toString()));
                response.setPostId(postId);
                cacheCollectList.add(response);
            }
        });
        //如果redis缓存中没有 去数据库批量查询
        if(!resultList.isEmpty()){
            List<PostCountResponse> batchCollectCount = postCollectionDao.findBatchCollectCount(resultList);
            //数据库查询后也存入redis缓存
            batchCollectCount.forEach(item->{
                redisUtils.hashAdd(RedisKeys.getPostKey(item.getPostId()), ConfigConstant.POST_STAR_NUM,item.getNumber().toString());
            });
            cacheCollectList.addAll(batchCollectCount);
        }
        return cacheCollectList;
    }

    @Override
    public List<PostIsCollectionResponse> findIsCollectBatch(List<Integer> list, Integer uid) {
        return postCollectionDao.findIsCollectBatch(list,uid);
    }

}