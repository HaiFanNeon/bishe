<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="待审核" name="0"></el-tab-pane>
      <el-tab-pane label="已处理" name="all"></el-tab-pane>
    </el-tabs>

    <el-table :data="tableData" border>
      <el-table-column prop="title" label="活动标题" />
      <el-table-column prop="clubName" label="申请社团" width="150" />
      <el-table-column prop="realName" label="申请人" width="120" />
      <el-table-column prop="budgetAmount" label="预算(元)" width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已批准</el-tag>
          <el-tag v-else-if="row.status === 2" type="danger">已驳回</el-tag>
          <el-tag v-else type="warning">需补充</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)"
            >详情/历史</el-button
          >
          <el-button
            v-if="row.status === 0"
            size="small"
            type="primary"
            @click="openAudit(row)"
            >审核</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="auditVisible" title="活动审核">
      <p><strong>活动：</strong>{{ currentRow.title }}</p>
      <p><strong>预算：</strong>{{ currentRow.budgetAmount }} 元</p>
      <el-divider />
      <el-form>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.action">
            <el-radio :label="1">批准</el-radio>
            <el-radio :label="2">驳回</el-radio>
            <el-radio :label="3">要求补充材料</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            type="textarea"
            v-model="auditForm.comment"
            placeholder="请输入理由..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">提交</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="申请详情" size="40%">
      <div v-if="detailData">
        <h3>基本信息</h3>
        <p>标题：{{ detailData.apply.title }}</p>
        <p>内容：<br />{{ detailData.apply.content }}</p>
        <el-divider />
        <h3>审批历史</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(log, index) in detailData.logs"
            :key="index"
            :timestamp="log.createTime"
            placement="top"
          >
            <el-card>
              <h4>
                {{ getActionText(log.operateAction) }} - 操作人：{{
                  log.operatorName
                }}
              </h4>
              <p>{{ log.comment }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const activeTab = ref("0");
const tableData = ref([]);
const auditVisible = ref(false);
const detailVisible = ref(false);
const currentRow = ref({});
const detailData = ref(null);
const auditForm = reactive({ applyId: null, action: 1, comment: "" });

const fetchList = async () => {
  const status = activeTab.value === "all" ? null : parseInt(activeTab.value);
  const res = await request.post("/activity/list", {
    pageNum: 1,
    pageSize: 20,
    status,
  });
  tableData.value = res.data.records;
};

const handleTabChange = () => fetchList();

const viewDetail = async (id) => {
  const res = await request.get(`/activity/detail/${id}`);
  detailData.value = res.data;
  detailVisible.value = true;
};

const openAudit = (row) => {
  currentRow.value = row;
  auditForm.applyId = row.id;
  auditForm.action = 1;
  auditForm.comment = "";
  auditVisible.value = true;
};

const submitAudit = async () => {
  await request.post("/activity/audit", auditForm);
  ElMessage.success("审核完成");
  auditVisible.value = false;
  fetchList();
};

const getActionText = (action) => {
  const map = { 1: "批准", 2: "驳回", 3: "要求补充" };
  return map[action] || "未知操作";
};

onMounted(() => fetchList());
</script>
