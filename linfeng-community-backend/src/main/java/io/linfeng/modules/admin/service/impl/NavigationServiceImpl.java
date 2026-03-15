package io.linfeng.modules.admin.service.impl;

import io.linfeng.common.utils.Constant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.admin.dao.NavigationDao;
import io.linfeng.modules.admin.entity.NavigationEntity;
import io.linfeng.modules.admin.service.NavigationService;


@Service("navigationService")
public class NavigationServiceImpl extends ServiceImpl<NavigationDao, NavigationEntity> implements NavigationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<NavigationEntity> page = this.page(
                new Query<NavigationEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<NavigationEntity> getNav() {
        List<NavigationEntity> list = this.lambdaQuery()
                .eq(NavigationEntity::getStatus, Constant.NORMAL)
                .list();
        return list;
    }

    @Override
    public void downBatch(List<Integer> list) {
        list.forEach(id->{
            NavigationEntity navigation = this.getById(id);
            if(navigation.getStatus()==0){
                navigation.setStatus(1);
                this.updateById(navigation);
            }
        });
    }

    @Override
    public void upBatch(List<Integer> list) {
        list.forEach(id->{
            NavigationEntity navigation = this.getById(id);
            if(navigation.getStatus()==1){
                navigation.setStatus(0);
                this.updateById(navigation);
            }
        });
    }

}