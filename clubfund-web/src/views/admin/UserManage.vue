<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索用户名/姓名"
        style="width: 200px; margin-right: 10px"
      />
      <el-select
        v-model="queryParams.role"
        placeholder="角色筛选"
        clearable
        style="width: 150px; margin-right: 10px"
      >
        <el-option label="管理员" :value="0" />
        <el-option label="学生" :value="1" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增用户</el-button>
    </el-card>

    <el-table :data="tableData" border style="margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="clubName" label="所属社团" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="scope.row.role === 0 ? 'danger' : 'info'">
            {{ scope.row.role === 0 ? "管理员" : "学生" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button
            size="small"
            type="warning"
            @click="handleResetPwd(scope.row.id)"
            >重置密码</el-button
          >
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="queryParams.pageSize"
      @current-change="handlePageChange"
      style="margin-top: 20px; justify-content: center; display: flex"
    />

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" v-if="!isEdit">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" placeholder="默认123456" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="所属社团">
          <el-input v-model="form.clubName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";

const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  role: null,
});
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = reactive({
  username: "",
  password: "",
  realName: "",
  clubName: "",
  phone: "",
  id: null,
});

// 获取列表
const fetchList = async () => {
  const res = await request.post("/user/list", queryParams);
  tableData.value = res.data.records;
  total.value = res.data.total;
};

// 分页
const handlePageChange = (page) => {
  queryParams.pageNum = page;
  fetchList();
};

// 重置密码
const handleResetPwd = (id) => {
  ElMessageBox.confirm("确定要重置该用户的密码为 123456 吗?", "提示", {
    type: "warning",
  }).then(async () => {
    await request.put(`/user/resetPwd/${id}`);
    ElMessage.success("重置成功");
  });
};

// 删除用户
const handleDelete = (id) => {
  ElMessageBox.confirm("确定删除该用户吗?", "警告", { type: "error" }).then(
    async () => {
      await request.delete(`/user/${id}`);
      ElMessage.success("删除成功");
      fetchList();
    }
  );
};

// 打开新增
const openAddDialog = () => {
  isEdit.value = false;
  Object.assign(form, {
    username: "",
    password: "",
    realName: "",
    clubName: "",
    phone: "",
  });
  dialogVisible.value = true;
};

// 打开编辑
const handleEdit = (row) => {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (isEdit.value) {
    await request.put("/user/update", form);
  } else {
    await request.post("/user/register", form);
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  fetchList();
};

onMounted(() => fetchList());
</script>
