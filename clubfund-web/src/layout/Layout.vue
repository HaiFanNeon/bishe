<template>
  <div class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">社团经费系统</div>
      <el-menu
        :default-active="activePath"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-if="user.role === 0">
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon><span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/activity">
            <el-icon><List /></el-icon><span>活动审批</span>
          </el-menu-item>
          <el-menu-item index="/admin/reimburse">
            <el-icon><Money /></el-icon><span>财务核销</span>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><DataLine /></el-icon><span>统计公示</span>
          </el-menu-item>
        </template>

        <template v-else>
          <el-menu-item index="/user/home">
            <el-icon><UserFilled /></el-icon><span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/user/activity">
            <el-icon><Edit /></el-icon><span>活动申请</span>
          </el-menu-item>
          <el-menu-item index="/user/reimburse">
            <el-icon><Wallet /></el-icon><span>报销申请</span>
          </el-menu-item>
          <el-menu-item index="/user/message">
            <el-icon><Bell /></el-icon><span>消息通知</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span>当前用户：{{ user.realName || user.username }}</span>
          <el-tag size="small" style="margin-left: 10px">{{
            user.role === 0 ? "管理员" : "学生"
          }}</el-tag>
        </div>
        <el-button type="danger" link @click="logout">退出登录</el-button>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

// 从 localStorage 获取用户信息
const userStr = localStorage.getItem("user");
const user = userStr ? JSON.parse(userStr) : { role: 1 };

// 当前激活的菜单路径
const activePath = route.path;

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  router.push("/login");
};
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}
.aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3649;
}
.el-menu {
  border-right: none;
}
.header {
  background-color: #fff;
  border-bottom: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
