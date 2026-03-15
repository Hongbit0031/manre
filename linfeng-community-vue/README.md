# manre

## linfeng-community-vue3标准商业版启动方式

林风社交论坛商业版基于Vue3+Elemen-plus打造！
官网：https://www.linfengtech.cn
备用官网：https://net.linfeng.tech     CopyRight @宁波山觉网络科技有限公司

#### 1.初次启动项目

**node.js版本要求：大于16**才行。

推荐**nvm**管理工具管理node版本。

**参考版本**node.js版本 v18.15.0   npm 版本v9.5.0

初次启动项目请执行`npm install`安装依赖

#### 2.本地启动项目

在项目的**config/index.js**中配置后端接口即可，本地接口和线上环境接口都在这个文件中配置哦。

执行`npm run dev`即可在本地启动项目

#### 3.线上打包

执行`npm run build`即可在打包出dist文件夹。
