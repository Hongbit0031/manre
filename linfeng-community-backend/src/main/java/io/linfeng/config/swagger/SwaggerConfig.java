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
package io.linfeng.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public OpenAPI createRestApi(){
        return new OpenAPI()
                .info(apiInfo())
                .security(security());
    }

    private Info apiInfo(){
        Contact contact = new Contact();
        contact.setName("慢热");
        contact.setUrl("https://m.scs.baby");
        contact.setEmail("support@scs.baby");
        
        return new Info()
                .title(swaggerProperties.getTitle())
                .description("慢热小程序/H5/App 版本，基于 SpringBoot + MybatisPlus + Shiro + JWT + Redis + Vue + Uniapp 的前后端分离社区项目")
                .contact(contact)
                .version(swaggerProperties.getVersion());
    }

    private List<SecurityRequirement> security(){
        SecurityRequirement key = new SecurityRequirement();
        key.addList("token", "token");
        
        List<SecurityRequirement> list = new ArrayList<>();
        list.add(key);
        return list;
    }

}
