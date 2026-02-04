<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">学生社团经费管理系统</h2>

      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-width="0">
            <el-form-item>
              <el-input
                v-model="loginForm.username"
                placeholder="请输入账号"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-button
              type="primary"
              style="width: 100%"
              @click="handleLogin"
              :loading="loading"
              >登 录</el-button
            >
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="学生注册" name="register">
          <el-form :model="regForm" label-width="80px">
            <el-form-item label="账号">
              <el-input v-model="regForm.username" placeholder="学号或英文名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="regForm.password" type="password" />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="regForm.realName" />
            </el-form-item>
            <el-form-item label="所属社团">
              <el-input v-model="regForm.clubName" placeholder="例如：街舞社" />
            </el-form-item>
            <el-button
              type="success"
              style="width: 100%"
              @click="handleRegister"
              >立即注册</el-button
            >
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import request from "@/utils/request";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter();
const activeTab = ref("login");
const loading = ref(false);

const loginForm = reactive({ username: "", password: "" });
const regForm = reactive({
  username: "",
  password: "",
  realName: "",
  clubName: "",
  role: 1,
}); // 默认为学生

const handleLogin = async () => {
  loading.value = true;
  try {
    const res = await request.post("/user/login", loginForm);
    const { token, user } = res.data;

    // 存储 Token 和用户信息
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));

    ElMessage.success("登录成功");

    // 简单的权限路由跳转
    if (user.role === 0) {
      router.push("/admin/users");
    } else {
      router.push("/user/home"); // 用户端首页
    }
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  if (!regForm.username || !regForm.password || !regForm.clubName) {
    return ElMessage.warning("请填写完整信息");
  }
  await request.post("/user/register", regForm);
  ElMessage.success("注册成功，请登录");
  activeTab.value = "login";
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
}
.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
