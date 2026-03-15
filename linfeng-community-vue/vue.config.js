const webpack = require("webpack");
const TerserPlugin = require("terser-webpack-plugin");
const CompressionWebpackPlugin = require("compression-webpack-plugin");
let scssVariables = require("./src/styles/variables.scss.js");

module.exports = {
    lintOnSave: false,
    publicPath: "",
    productionSourceMap: false,

    devServer: {
        port: 8001,
    },

    chainWebpack: (config) => {
        config.plugin("provide").use(webpack.ProvidePlugin, [
            {
                XE: "xe-utils",
            },
        ]);

        config.plugins.delete("prefetch");

    },

    configureWebpack: () => {
        let baseConfig = {};
        let envConfig = {};
        if (process.env.NODE_ENV === "production") {
            // 为生产环境修改配置...
            envConfig = {
                optimization: {
                    splitChunks: {
                        chunks: "all",
                        // enforceSizeThreshold: 20000,
                        cacheGroups: {
                            echarts: {
                                name: "chunk-echarts",
                                priority: 20,
                                test: /[\\/]node_modules[\\/]_?echarts(.*)/,
                            },
                            elementPlus: {
                                name: "chunk-elementPlus",
                                priority: 20,
                                test: /[\\/]node_modules[\\/]_?element-plus(.*)/,
                            },
                            elementPlusIcon: {
                                name: "chunk-elementPlusIcon",
                                priority: 20,
                                test: /[\\/]node_modules[\\/]_?@element-plus[\\/]icons(.*)/,
                            },
                        },
                    },
                },
                externals: {

                },
                plugins: [
                    new TerserPlugin({
                        terserOptions: {
                            compress: {
                                // drop_console: true,
                                drop_debugger: true,
                            },
                        },
                    }),
                    new CompressionWebpackPlugin({
                        filename: "[path][base].gz",
                        algorithm: "gzip",
                        // test: /\.js$|\.html$|\.json$|\.css/,
                        test: /\.js$|\.json$|\.css/,
                        threshold: 10240, // 只有大小大于该值的资源会被处理
                        minRatio: 0.8, // 只有压缩率小于这个值的资源才会被处理
                        // deleteOriginalAssets: true // 删除原文件
                    }),
                ],
            };
        }
        return Object.assign(baseConfig, envConfig);
    },

    css: {
        loaderOptions: {
            scss: {
                // 注意：在 sass-loader v8 中，这个选项名是 "prependData"
                // additionalData: `@import "~@/styles/imports.scss";`
                additionalData: Object.keys(scssVariables)
                    .map((k) => `$${k.replace("_", "-")}: ${scssVariables[k]};`)
                    .join("\n"),
            },
        },
    },
};
