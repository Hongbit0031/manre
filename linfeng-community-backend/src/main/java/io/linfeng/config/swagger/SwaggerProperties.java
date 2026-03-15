package io.linfeng.config.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Swagger相关配置
 */
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    private String title;

    private String version;

    private final Basic basic = new Basic();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Basic getBasic() {
        return basic;
    }

    public static class Basic {
        private boolean enable;
        private String username;
        private String password;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

