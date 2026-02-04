<template>
  <div class="page-container">
    <el-table :data="tableData" border>
      <el-table-column prop="activityId" label="关联活动ID" width="100" />
      <el-table-column prop="clubName" label="社团" width="150" />
      <el-table-column prop="amount" label="报销金额" width="120">
        <template #default="{ row }">
          <span style="color: red; font-weight: bold">¥{{ row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="说明" />
      <el-table-column label="财务凭证" width="150">
        <template #default="{ row }">
          <el-image
            style="width: 100px; height: 100px"
            :src="row.voucherUrl"
            :preview-src-list="[row.voucherUrl]"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag
            :type="
              row.status === 0
                ? 'warning'
                : row.status === 1
                ? 'success'
                : 'danger'
            "
          >
            {{
              row.status === 0
                ? "待核销"
                : row.status === 1
                ? "已核销"
                : "已驳回"
            }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            type="success"
            size="small"
            @click="handleAudit(row, 1)"
            >核销通过</el-button
          >
          <el-button
            v-if="row.status === 0"
            type="danger"
            size="small"
            @click="handleAudit(row, 2)"
            >驳回</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";

const tableData = ref([]);

const fetchList = async () => {
  // 传 status=null 查所有，或者默认查待核销，这里演示查所有
  const res = await request.get("/reimburse/list", {
    params: { pageNum: 1, pageSize: 20 },
  });
  tableData.value = res.data.records;
};

const handleAudit = (row, status) => {
  const actionText = status === 1 ? "确认核销并打款" : "驳回该申请";
  ElMessageBox.prompt(`请输入${actionText}的备注`, "财务审核", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(async ({ value }) => {
    await request.post("/reimburse/audit", {
      id: row.id,
      status: status,
      auditComment: value,
    });
    ElMessage.success("操作成功");
    fetchList();
  });
};

onMounted(() => fetchList());
</script>
