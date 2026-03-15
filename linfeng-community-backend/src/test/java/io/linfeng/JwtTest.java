package io.linfeng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.DateUtil;
import io.linfeng.common.utils.SnowFlakeUtil;
import io.linfeng.config.RestTemplateUtil;
import io.linfeng.modules.admin.entity.LuckdrawEntity;
import io.linfeng.modules.admin.service.LuckdrawService;
import io.linfeng.modules.app.entity.ChatMessageEntity;
import io.linfeng.modules.app.service.ChatMessageService;
import io.linfeng.modules.app.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class JwtTest {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private LuckdrawService luckdrawService;
    @Autowired
    private ChatMessageService chatMessageService;

    @Test
    public void test() {
        String token = jwtUtils.generateToken(1);

        log.info(token);
    }

    @Test
    public void testPassword() {
        String password = DigestUtils.sha256Hex("123456");

        System.out.println(password);
    }

    @Test
    public void testUrl() {
//        JSONObject data = restTemplateUtil.getData(Constant.ROBOT_CONTENT);
//        if (ObjectUtil.isNull(data)) {
//            System.out.println(11111);
//        }
//        JSONObject data1 = data.getJSONObject("data");
//        System.out.println("-->"+data1.toJSONString());
//        String content = data1.getString("content");
//        System.out.println("content:"+content);
//        JSONObject res = restTemplateUtil.getData(Constant.ROBOT_PIC);
        String link = restTemplateUtil.getLink(Constant.ROBOT_PIC);

        System.out.println("-->"+link);
        JSONObject jsonObject = JSON.parseObject(link);
        System.out.println("==>"+jsonObject.getString("imgurl"));
//        System.out.println("-->"+res.toJSONString());
//        String imgurl = res.getString("imgurl");
//        System.out.println("==>"+imgurl);
    }

    @Test
    public void test5(){

            List<LuckdrawEntity> list = luckdrawService.lambdaQuery()
                    .eq(LuckdrawEntity::getStatus, 1)
                    .orderByDesc(LuckdrawEntity::getSort)
                    .last("limit 8")
                    .list();
            int a[]=new int[9];
            for (int i = 0; i < 50; i++) {
                int index = getPrize(list);
//                System.out.println(list.get(index).getId());
                a[list.get(index).getId()]++;
            }
        for (int i = 0; i <a.length ; i++) {
            System.out.println(a[i]);
        }
    }
    public static int getPrize(List<LuckdrawEntity > list){
        double sum = list.stream().mapToDouble(LuckdrawEntity::getProbability).sum();
        double random = Math.random();
        double currentMax = 0;
        double max = 0;
        double min = 0;
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            currentMax =  list.get(i).getProbability()/sum;
            max  = currentMax + max;

            if(random >= min && random <= max){
                index = i;
                break;
            }
            min = currentMax + min;
        }

        return index;
    }

    @Test
    public void test6(){
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .id(SnowFlakeUtil.getSnowFlakeId().toString())
                .sessionId("1872817281721")
                .senderId("40")
                .receiverId("32")
                .sendTime("2022-11-18 11:00:59")
                .content("你好")
                .messageType("text")
                .isWithdrawn(0)
                .updateTime(DateUtil.nowDateTime())
                .build();
        boolean save = chatMessageService.save(chatMessage);
        if(save){
            log.info("ok");
        }else{
            log.info("fail");
        }

    }
}
