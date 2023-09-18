<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">系统主页</div>
    </div>
    <!-- 主页面的四个区域 -->
    <div class="upPart">
      <!-- 左上角区 活跃用户-->
      <div class="main-one">
        <div class="main-center">
          <div class="header">活跃用户</div>
          <div class="image"><img class="imageOne" src="/user.png" /></div>
        </div>
        <div class="bottom" />
        <div class="pieChart">
          <div id="onlineUserChart" style="width: 580px; height: 350px" />
        </div>
      </div>
      <!-- 右上角区 用户分类-->
      <div class="main-two">
        <div class="main-center">
          <div class="headerTwo">用户分类</div>
          <div class="image"><img class="imageOne" src="/user.png" /></div>
        </div>
        <div class="bottomtwo" />
        <div class="pieChart">
          <div id="userTypeChart" style="width: 600px; height: 350px" />
        </div>
      </div>
    </div>
    <div class="downPart">
      <!-- 左下角区 服务请求-->
      <div class="main-three">
        <div class="main-center">
          <div class="header3">服务请求</div>
          <div class="image"><img class="imageOne" src="/dayOrder.png" /></div>
        </div>
        <div class="bottom3" />
        <div class="pieChart">
          <div id="requestChart" style="width: 600px; height: 350px" />
        </div>
      </div>
      <!-- 右下角区 数据操作-->
      <div class="main-four">
        <div class="main-center">
          <div class="header4">数据操作</div>
          <div class="image"><img class="imageOne" src="/dayOrder.png" /></div>
        </div>
        <div class="bottom4" />
        <div class="pieChart">
          <div id="operateChart" style="width: 600px; height: 350px" />
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import * as echarts from "echarts";
import { getMainPageData } from "~/services/mainServ";
import { type ChartItem, type UserOnlineItem } from "~/models/general";
var echart = echarts;
var onlineUserChart: any;
var userTypeChart: any;
var requestChart: any;
var operateChart: any;

export default defineComponent({
  //数据
  data: () => ({
    onlineUser: {} as UserOnlineItem,
    userTypeList: [],
    requestData: {} as ChartItem,
    operateData: {} as ChartItem,
  }),
  async created() {
    await this.doQuery();
  },
  methods: {
    //请求后台页面显示的所有数据
    async doQuery() {
      const res = await getMainPageData();
      this.onlineUser = res.data.onlineUser;
      this.userTypeList = res.data.userTypeList;
      this.requestData = res.data.requestData;
      this.operateData = res.data.operateData;

      this.drawEcharts();
    },
    //绘制echarts
    drawEcharts() {
      //绘制用户活跃图表
      if (
        onlineUserChart != null &&
        onlineUserChart != "" &&
        onlineUserChart != undefined
      ) {
        onlineUserChart.dispose(); //解决echarts dom已经加载的报错
      }
      if (
        userTypeChart != null &&
        userTypeChart != "" &&
        userTypeChart != undefined
      ) {
        userTypeChart.dispose(); //解决echarts dom已经加载的报错
      }
      if (
        requestChart != null &&
        requestChart != "" &&
        requestChart != undefined
      ) {
        requestChart.dispose(); //解决echarts dom已经加载的报错
      }
      if (
        operateChart != null &&
        operateChart != "" &&
        operateChart != undefined
      ) {
        operateChart.dispose(); //解决echarts dom已经加载的报错
      }
      onlineUserChart = echart.init(
        document.getElementById("onlineUserChart") as any
      );
      onlineUserChart.setOption({
        title: { text: "用户活跃情况", left: "center" },
        xAxis: {
          type: "value",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        yAxis: {
          type: "category",
          data: ["总用户", "本月活动用户", "当日活动用户"],
          axisTick: {
            alignWithLabel: true,
          },
        },
        series: [
          {
            itemStyle: {
              label: {
                show: true, //开启显示
                position: "right", //在上方显示
                textStyle: {
                  //数值样式
                  color: "black",
                  fontSize: 14,
                },
              },
            },
            barWidth: "50%",
            data: [
              {
                value: this.onlineUser.total,
                itemStyle: {
                  color: "#1E90FF",
                },
              },
              {
                value: this.onlineUser.monthCount,
                itemStyle: {
                  color: "#FF6347",
                },
              },
              {
                value: this.onlineUser.dayCount,
                itemStyle: {
                  color: "#FFD700",
                },
              },
            ],
            type: "bar",
          },
        ],
      });
      //      绘制用户分类图表
      userTypeChart = echart.init(
        document.getElementById("userTypeChart") as any
      );
      userTypeChart.setOption({
        title: { text: "用户总数:" + this.onlineUser.total, left: "center" },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b}:  ({d}%)",
        },
        legend: {
          top: "bottom",
        },

        series: [
          {
            itemStyle: {
              borderRadius: this.userTypeList.length,
              label: {
                show: true,
                formatter: "{b} : {c} ",
              },
              labelLine: { show: true },
            },

            name: "当天",
            type: "pie",
            radius: [25, 120],
            center: ["50%", "50%"],
            roseType: "area",

            data: this.userTypeList,
          },
        ],
      });
      //绘制请求图表
      requestChart = echart.init(
        document.getElementById("requestChart") as any
      );
      requestChart.setOption({
        xAxis: {
          type: "category",
          axisTick: {
            alignWithLabel: true,
          },
          data: this.requestData.value,
        },
        legend: {},
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "登录",
            type: "bar",
            stack: "total",
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: "#1296db",
              label: {
                show: true, //开启显示
                textStyle: {
                  //数值样式
                  color: "black",
                  fontSize: 7,
                },
              },
            },
            data: this.requestData.label1,
          },
          {
            name: "请求",
            type: "bar",
            stack: "total",
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: "#1296db",
              label: {
                show: true, //开启显示
                textStyle: {
                  //数值样式
                  color: "black",
                  fontSize: 7,
                },
              },
            },
            data: this.requestData.label2,
          },
        ],
      });
      // 绘制修改数据图表
      operateChart = echart.init(
        document.getElementById("operateChart") as any
      );
      operateChart.setOption({
        xAxis: {
          type: "category",
          axisTick: {
            alignWithLabel: true,
          },
          data: this.operateData.value,
        },
        legend: {},
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "新建",
            type: "bar",
            stack: "total",
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: "#1296db",
              label: {
                show: true, //开启显示
                textStyle: {
                  //数值样式
                  color: "black",
                  fontSize: 7,
                },
              },
            },
            data: this.operateData.label1,
          },
          {
            name: "修改",
            type: "bar",
            stack: "total",
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: "#1296db",
              label: {
                show: true, //开启显示
                textStyle: {
                  //数值样式
                  color: "black",
                  fontSize: 7,
                },
              },
            },
            data: this.operateData.label2,
          },
        ],
      });
    },
  },
});
</script>
<style scoped>
.pieChart {
  width: 580px;
  height: 280px;
  margin: 0px auto;
}
.upPart {
  display: flex;
  flex-direction: row;
  margin-top: 10px;
}
.main-one {
  background: white;
  width: 45%;
  height: 350px;
  margin-left: 50px;
  margin-right: 50px;
}
.main-two {
  background: white;
  width: 45%;
  height: 350px;
}
.downPart {
  display: flex;
  flex-direction: row;
  margin-top: 10px;
}
.main-three {
  background: white;
  width: 45%;
  height: 350px;
  margin-left: 50px;
  margin-right: 50px;
}
.main-four {
  background: white;
  width: 45%;
  height: 350px;
}
.header {
  margin-top: 20px;
  margin-left: 40px;
  font-size: 20px;
  color: #3cb371;
  font-weight: bold;
}
.header3 {
  margin-top: 20px;
  margin-left: 40px;
  font-size: 20px;
  color: #d4237a;
  font-weight: bold;
}
.header4 {
  margin-top: 20px;
  margin-left: 40px;
  font-size: 20px;
  color: #1296db;
  font-weight: bold;
}
.headerTwo {
  margin-top: 20px;
  margin-left: 40px;
  font-size: 20px;
  color: #ffa500;
  font-weight: bold;
}
.bottom {
  background: #3cb371;
  height: 2px;
  width: 100%;
  margin-top: 10px;
}
.bottom3 {
  background: #d4237a;
  height: 2px;
  width: 100%;
  margin-top: 10px;
}
.bottom4 {
  background: #1296db;
  height: 2px;
  width: 100%;
  margin-top: 10px;
}
.bottomtwo {
  background: #ffa500;
  height: 2px;
  width: 100%;
  margin-top: 10px;
}
.imageOne {
  width: 40px;
  height: 40px;
}
.main-center {
  display: flex;
  flex-direction: row;
}
.image {
  width: 40px;
  height: 40px;
  margin-left: auto;
  margin-top: 10px;
  margin-right: 20px;
}
</style>
