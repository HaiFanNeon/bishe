<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" class="msg-tabs">
      <el-tab-pane label="我的消息通知" name="private">
        <template #label>
          <span>我的消息 </span>
          <el-badge
            :value="unreadCount"
            :max="99"
            v-if="unreadCount > 0"
            class="item"
          />
        </template>

        <div v-for="msg in privateMessages" :key="msg.id" class="msg-item">
          <el-card :class="{ 'unread-card': msg.isRead === 0 }">
            <div class="msg-header">
              <span class="msg-title">{{ msg.title }}</span>
              <span class="msg-time">{{ msg.createTime }}</span>
            </div>
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-footer" v-if="msg.isRead === 0">
              <el-button type="text" @click="markRead(msg.id)"
                >标为已读</el-button
              >
            </div>
          </el-card>
        </div>
        <el-empty v-if="privateMessages.length === 0" description="暂无消息" />
      </el-tab-pane>

      <el-tab-pane label="系统公告 & 经费公示" name="public">
        <el-timeline style="margin-top: 20px; padding-left: 20px">
          <el-timeline-item
            v-for="notice in publicNotices"
            :key="notice.id"
            :timestamp="notice.createTime"
            placement="top"
          >
            <el-card>
              <h4>{{ notice.title }}</h4>
              <p style="white-space: pre-wrap">{{ notice.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import request from "@/utils/request";

const activeTab = ref("private");
const privateMessages = ref([]);
const publicNotices = ref([]);

// 计算未读数
const unreadCount = computed(() => {
  return privateMessages.value.filter((m) => m.isRead === 0).length;
});

// 加载我的消息
const loadMyMessages = async () => {
  const res = await request.get("/message/my", { params: { pageSize: 50 } });
  privateMessages.value = res.data.records;
};

// 加载系统公告
const loadNotices = async () => {
  const res = await request.get("/stats/public/list");
  publicNotices.value = res.data.records;
};

// 标为已读
const markRead = async (id) => {
  await request.put(`/message/read/${id}`);
  // 简单处理：本地更新状态，避免重新请求
  const item = privateMessages.value.find((m) => m.id === id);
  if (item) item.isRead = 1;
};

onMounted(() => {
  loadMyMessages();
  loadNotices();
});
</script>

<style scoped>
.msg-item {
  margin-bottom: 15px;
}
.msg-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-weight: bold;
}
.msg-time {
  color: #999;
  font-size: 12px;
  font-weight: normal;
}
.msg-content {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}
.unread-card {
  border-left: 5px solid #f56c6c;
  background-color: #fff6f6;
}
.msg-footer {
  text-align: right;
  margin-top: 5px;
}
</style>
