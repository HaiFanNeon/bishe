<template>
  <el-card title="个人中心" style="max-width: 800px; margin: 20px auto">
    <template #header>
      <div class="card-header">
        <span>我的基本资料</span>
        <el-button type="primary" @click="handleUpdate">保存修改</el-button>
      </div>
    </template>

    <el-form :model="form" label-width="100px" style="margin-top: 20px">
      <el-form-item label="账号">
        <el-input v-model="form.username" disabled placeholder="账号不可修改" />
      </el-form-item>
      <el-form-item label="真实姓名">
        <el-input v-model="form.realName" />
      </el-form-item>
      <el-form-item label="学号">
        <el-input v-model="form.studentId" />
      </el-form-item>
      <el-form-item label="手机号码">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="所属社团">
        <el-input
          v-model="form.clubName"
          placeholder="如更换社团请联系管理员"
        />
      </el-form-item>
      <el-form-item label="角色">
        <el-tag>{{ form.role === 0 ? "管理员" : "社团负责人/学生" }}</el-tag>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const form = reactive({
  username: "",
  realName: "",
  studentId: "",
  phone: "",
  clubName: "",
  role: 1,
});

// 获取最新信息
const loadProfile = async () => {
  const res = await request.get("/user/profile");
  Object.assign(form, res.data);
};

// 提交修改
const handleUpdate = async () => {
  await request.put("/user/profile", {
    realName: form.realName,
    studentId: form.studentId,
    phone: form.phone,
    clubName: form.clubName,
  });
  ElMessage.success("个人信息更新成功");
  loadProfile(); // 刷新
};

onMounted(() => loadProfile());
</script>
