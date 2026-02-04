<template>
  <router-view />
</template>

<style>
/* 全局样式重置 */
body,
html {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  background-color: #f0f2f5;
}
#app {
  height: 100%;
}
.page-container {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 100px); /* 减去顶部高度 */
}
</style>
