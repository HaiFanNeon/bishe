<template>
  <div class="page-container">
    <el-tabs type="border-card">
      <el-tab-pane label="发起活动申请">
        <el-form
          :model="applyForm"
          label-width="120px"
          style="max-width: 600px"
        >
          <el-alert
            title="注意：活动申请需提前至少3天提交，预算请精确到元。"
            type="info"
            show-icon
            style="margin-bottom: 20px"
          />

          <el-form-item label="活动标题" required>
            <el-input
              v-model="applyForm.title"
              placeholder="例如：2026年校园吉他音乐节"
            />
          </el-form-item>
          <el-form-item label="预算金额 (元)" required>
            <el-input-number
              v-model="applyForm.budgetAmount"
              :min="0"
              :precision="2"
            />
          </el-form-item>
          <el-form-item label="活动策划/内容" required>
            <el-input
              v-model="applyForm.content"
              type="textarea"
              :rows="6"
              placeholder="请详细描述活动流程、物资需求及预算明细..."
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitApply">提交申请</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="我的申请记录">
        <el-table :data="myList" border stripe>
          <el-table-column prop="createTime" label="提交时间" width="160" />
          <el-table-column prop="title" label="活动标题" />
          <el-table-column prop="budgetAmount" label="预算" width="120" />
          <el-table-column label="审核状态" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0">审核中</el-tag>
              <el-tag v-else-if="row.status === 1" type="success"
                >已批准</el-tag
              >
              <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
              <el-tag v-else type="warning">需补充</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="currentAuditComment" label="审核意见" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const applyForm = reactive({ title: "", budgetAmount: 0, content: "" });
const myList = ref([]);

const submitApply = async () => {
  if (!applyForm.title || !applyForm.content)
    return ElMessage.warning("请完善申请信息");

  await request.post("/activity/create", applyForm);
  ElMessage.success("提交成功");
  // 重置表单
  applyForm.title = "";
  applyForm.content = "";
  applyForm.budgetAmount = 0;
  loadMyList(); // 刷新列表
};

const loadMyList = async () => {
  const res = await request.post("/activity/my/list", {
    pageNum: 1,
    pageSize: 50,
  });
  myList.value = res.data.records;
};

onMounted(() => loadMyList());
</script>
