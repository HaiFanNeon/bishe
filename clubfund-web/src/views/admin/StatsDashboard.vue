<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>批准总预算</template>
          <h2 style="color: #409eff">
            ¥ {{ dashboardData.totalBudgetApproved }}
          </h2>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>实际已核销</template>
          <h2 style="color: #67c23a">
            ¥ {{ dashboardData.totalExpenseVerified }}
          </h2>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>总结余</template>
          <h2 style="color: #e6a23c">¥ {{ dashboardData.totalBalance }}</h2>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>举办活动数</template>
          <h2>{{ dashboardData.totalActivities }} 场</h2>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14">
        <el-card title="社团花费排行 Top 10">
          <div ref="chartRef" style="height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card title="发布经费公示/系统公告">
          <el-form label-position="top">
            <el-form-item label="公告标题">
              <el-input
                v-model="noticeForm.title"
                placeholder="例如：2026年春季经费使用公示"
              />
            </el-form-item>
            <el-form-item label="公告内容">
              <el-input
                v-model="noticeForm.content"
                type="textarea"
                :rows="8"
              />
            </el-form-item>
            <el-button type="primary" style="width: 100%" @click="publishNotice"
              >立即发布</el-button
            >
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";

const dashboardData = ref({
  totalBudgetApproved: 0,
  totalExpenseVerified: 0,
  totalBalance: 0,
  totalActivities: 0,
});
const chartRef = ref(null);
const noticeForm = reactive({ title: "", content: "" });

// 加载看板数据
const loadDashboard = async () => {
  const res = await request.get("/stats/dashboard");
  dashboardData.value = res.data;
};

// 加载图表
const initChart = async () => {
  const res = await request.get("/stats/ranking");
  const data = res.data;

  const myChart = echarts.init(chartRef.value);
  myChart.setOption({
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: data.map((i) => i.clubName) },
    yAxis: { type: "value" },
    series: [
      {
        data: data.map((i) => i.totalSpent),
        type: "bar",
        itemStyle: { color: "#409EFF" },
      },
    ],
  });
};

// 发布公告
const publishNotice = async () => {
  if (!noticeForm.title) return ElMessage.warning("标题不能为空");
  await request.post("/stats/publish", noticeForm);
  ElMessage.success("发布成功");
  noticeForm.title = "";
  noticeForm.content = "";
};

onMounted(() => {
  loadDashboard();
  initChart();
});
</script>
