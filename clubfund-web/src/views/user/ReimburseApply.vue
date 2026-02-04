<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card title="提交报销单">
          <el-form :model="form" label-position="top">
            <el-form-item label="选择已批准的活动">
              <el-select
                v-model="form.activityId"
                placeholder="请选择..."
                style="width: 100%"
              >
                <el-option
                  v-for="item in approvedActivities"
                  :key="item.id"
                  :label="item.title + ' (预算:' + item.budgetAmount + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="实际报销金额">
              <el-input-number
                v-model="form.amount"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="报销说明">
              <el-input
                v-model="form.description"
                type="textarea"
                placeholder="例如：购买矿泉水、租用音响设备费用"
              />
            </el-form-item>

            <el-form-item label="财务凭证 (发票/收据)">
              <el-upload
                class="upload-demo"
                action="http://localhost:8080/common/upload"
                :on-success="handleUploadSuccess"
                :limit="1"
                list-type="picture"
              >
                <el-button type="primary">点击上传凭证图片</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传 jpg/png 文件，且不超过 5MB
                  </div>
                </template>
              </el-upload>
              <el-input
                v-model="form.voucherUrl"
                placeholder="或直接输入图片URL"
                style="margin-top: 5px"
              />
            </el-form-item>

            <el-button
              type="success"
              style="width: 100%"
              @click="submitReimburse"
              >提交报销</el-button
            >
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card title="我的报销进度">
          <el-table :data="reimburseList" stripe>
            <el-table-column prop="createTime" label="提交日期" width="110" />
            <el-table-column prop="amount" label="金额" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{
                  getStatusText(row.status)
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="auditComment" label="财务备注" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const form = reactive({
  activityId: null,
  amount: 0,
  description: "",
  voucherUrl: "",
});
const approvedActivities = ref([]);
const reimburseList = ref([]);

// 1. 获取我可以报销的活动（必须是已批准 status=1）
const loadApprovedActivities = async () => {
  // 复用 activity 查询接口，查自己的 + 状态为1的
  const res = await request.post("/activity/my/list", {
    status: 1,
    pageSize: 100,
  });
  approvedActivities.value = res.data.records;
};

// 2. 获取我的报销记录
const loadReimburseList = async () => {
  const res = await request.get("/reimburse/my/list");
  reimburseList.value = res.data.records;
};

// 上传成功回调
const handleUploadSuccess = (res) => {
  // 假设后端返回 { code: 200, data: 'url...' }
  form.voucherUrl = res.data;
  ElMessage.success("凭证上传成功");
};

// 提交
const submitReimburse = async () => {
  if (!form.activityId || !form.amount || !form.voucherUrl) {
    return ElMessage.warning("请填写完整信息并上传凭证");
  }
  await request.post("/reimburse/submit", form);
  ElMessage.success("提交成功，等待财务核销");
  form.amount = 0;
  form.description = "";
  form.voucherUrl = "";
  loadReimburseList();
};

const getStatusType = (s) =>
  s === 0 ? "warning" : s === 1 ? "success" : "danger";
const getStatusText = (s) =>
  s === 0 ? "审核中" : s === 1 ? "已打款" : "已驳回";

onMounted(() => {
  loadApprovedActivities();
  loadReimburseList();
});
</script>
