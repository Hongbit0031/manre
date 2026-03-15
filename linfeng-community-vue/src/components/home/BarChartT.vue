<template>
    <div
        id="linfengChart"
        :class="className"
        :style="{ height: height, width: width }"
    />
</template>

<script>
import * as echarts from "echarts";
import { markRaw } from "vue";
import "echarts/theme/macarons.js";
import { debounce } from "@/utils";
import http from "@/utils/httpRequest";
const animationDuration = 6000;

export default {
    props: {
        className: {
            type: String,
            default: "chart",
        },
        width: {
            type: String,
            default: "100%",
        },
        height: {
            type: String,
            default: "300px",
        },
    },
    data() {
        return {
            chart: null,
            day: [],
            num: [],
        };
    },
    mounted() {
        this.initChart();
        this.__resizeHandler = debounce(() => {
            if (this.chart) {
                this.chart.resize();
            }
        }, 100);
        window.addEventListener("resize", this.__resizeHandler);
    },
    beforeUnmount() {
        if (!this.chart) {
            return;
        }
        window.removeEventListener("resize", this.__resizeHandler);
        this.chart.dispose();
        this.chart = null;
    },
    methods: {
        initChart() {
            this.chart = markRaw(echarts.init(
                document.getElementById("linfengChart"),
                "macarons"
            ));

            http({
                url: http.adornUrl("/admin/statistics/chart"),
                method: "get",
            }).then(({ data }) => {
                if (data && data.code === 0) {
                    var _info = data.result.chart;
                    var day = [];
                    var num = [];
                    _info.forEach(function (item) {
                        day.push(item.time);
                        num.push(item.num);
                    });
                    this.chart.setOption({
                        tooltip: {
                            trigger: "axis",
                            axisPointer: {
                                // 坐标轴指示器，坐标轴触发有效
                                type: "shadow", // 默认为直线，可选为：'line' | 'shadow'
                            },
                        },
                        grid: {
                            top: 10,
                            left: "2%",
                            right: "2%",
                            bottom: "3%",
                            containLabel: true,
                        },
                        xAxis: [
                            {
                                type: "category",
                                data: day,
                                axisTick: {
                                    alignWithLabel: true,
                                },
                            },
                        ],
                        yAxis: [
                            {
                                type: "value",
                                axisTick: {
                                    show: false,
                                },
                            },
                        ],
                        series: [
                            {
                                name: "新增用户",
                                type: "line",
                                // itemStyle: {
                                //     color: "#9966cc",
                                //     // lineStyle: {
                                //     //     width: 2,
                                //     //     type: "solid", //'dotted'点型虚线 'solid'实线 'dashed'线性虚线
                                //     //     color: "#9966cc",
                                //     // },
                                // },
                                color: "#9966cc",
                                stack: "vistors",
                                data: num,
                                animationDuration,
                            },
                        ],
                    }, true);
                }
            });
        },
    },
};
</script>
