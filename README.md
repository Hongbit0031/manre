# Manre Deployment

这个仓库已经整理成适合单仓库管理的三端部署结构：

- `linfeng-community-backend`
- `linfeng-community-vue`
- `linfeng-community-uniapp`
- `ops/deploy`

## 目标

这套部署方案优先解决两件事：

- 三端可上线运行
- 后续更新只需要拉代码并重新执行一次部署脚本

## 现有能力

### 后端

- 使用 `Spring Boot + Maven`
- 生产配置已经改为支持环境变量
- 部署脚本会自动构建 jar、生成 `systemd` 服务文件、写入后端环境变量文件

### 管理端

- 使用 `Vue CLI`
- 支持运行时配置文件 `runtime-config.js`
- 线上改 API 地址时不需要重新改源码

### 用户端

- 当前仓库中的 `uni-app` 更适合作为 H5 用户端部署到服务器
- 同样支持运行时配置文件 `static/runtime-config.js`
- 如果需要原生 App 或小程序包，建议走 HBuilderX/云打包流程，和服务器部署解耦

## 首次部署

1. 在服务器上克隆仓库到固定目录，例如 `/srv/manre`
2. 复制 [ops/deploy/.deploy.env.example](/Users/chenhonghan/Desktop/manre/ops/deploy/.deploy.env.example) 为 `ops/deploy/.deploy.env`
3. 填写数据库、Redis、邮件、域名、站点目录等配置
4. 如果是全新数据库，执行：

```bash
cd /srv/manre
CONFIRM_DB_INIT=YES ./ops/deploy/init-db.sh
```

5. 执行部署：

```bash
cd /srv/manre
chmod +x ops/deploy/*.sh
./ops/deploy/deploy.sh
```

## 后续更新

部署一次后，后续更新只需要：

```bash
cd /srv/manre
./ops/deploy/update.sh
```

它会自动：

- `git pull`
- 重新构建后端
- 重新构建管理端
- 重新发布用户端 H5
- 重启 `systemd` 服务
- 重载 `nginx`

## 依赖要求

- `JDK 17`
- `Maven 3.9+`
- `Node.js 18+`
- `npm`
- `MySQL`
- `Redis`
- `nginx`
- `systemd`

## Uni-app H5 说明

`uni-app` 项目是否能在服务器上直接构建，取决于你使用的构建命令。

- 如果你已经在服务器上安装了 HBuilderX CLI 或可用的 H5 构建命令，就把命令填到 `UNI_H5_BUILD_CMD`
- 如果你是在本地先构建 H5，再把产物传到服务器，就把 `UNI_H5_OUTPUT_DIR` 指向产物目录

默认脚本会从：

- `/Users/chenhonghan/Desktop/manre/linfeng-community-uniapp/unpackage/dist/build/h5`

读取 H5 产物

## 关键文件

- [application.yml](/Users/chenhonghan/Desktop/manre/linfeng-community-backend/src/main/resources/application.yml)
- [application-prod.yml](/Users/chenhonghan/Desktop/manre/linfeng-community-backend/src/main/resources/application-prod.yml)
- [config/index.js](/Users/chenhonghan/Desktop/manre/linfeng-community-vue/config/index.js)
- [utils/config.js](/Users/chenhonghan/Desktop/manre/linfeng-community-uniapp/utils/config.js)
- [deploy.sh](/Users/chenhonghan/Desktop/manre/ops/deploy/deploy.sh)
- [update.sh](/Users/chenhonghan/Desktop/manre/ops/deploy/update.sh)
- [init-db.sh](/Users/chenhonghan/Desktop/manre/ops/deploy/init-db.sh)
