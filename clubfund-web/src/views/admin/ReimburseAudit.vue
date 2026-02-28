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
            :src="getImageUrl(row.voucherUrl)"
            :preview-src-list="[getImageUrl(row.voucherUrl)]"
            fit="cover"
            :hide-on-click-modal="true"
          >
            <template #error>
              <div class="image-error">
                <el-icon><picture-filled /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
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

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";
import { PictureFilled } from '@element-plus/icons-vue';

const tableData = ref([]);

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
});

// 获取图片完整 URL
const getImageUrl = (url) => {
  if (!url) return '';
  // 如果已经是完整 URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 否则拼接后端服务器地址
  return 'http://localhost:8080' + url;
};

const fetchList = async () => {
  // 传 status=null 查所有，或者默认查待核销，这里演示查所有
  const res = await request.get("/reimburse/list", {
    params: { 
      pageNum: pagination.pageNum, 
      pageSize: pagination.pageSize 
    },
  });
  tableData.value = res.data.records;
  pagination.total = res.data.total;
};

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size;
  pagination.pageNum = 1; // 重置到第一页
  fetchList();
};

// 页码改变
const handleCurrentChange = (page) => {
  pagination.pageNum = page;
  fetchList();
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

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 12px;
  border-radius: 4px;
}

.image-error .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}
</style>
