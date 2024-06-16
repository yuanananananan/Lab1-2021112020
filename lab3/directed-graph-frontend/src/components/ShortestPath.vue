<template>
  <div class="component-box">
    <h3>计算最短路径</h3>
    <input class="bridge-input-box" v-model="startWord" placeholder="输入起点单词">
    <input class="bridge-input-box" v-model="endWord" placeholder="输入终点单词">
    <button @click="calculateShortestPath">计算</button>
    <div class="output-box">{{ shortestPathResult }}</div>
  </div>
</template>

<script>
import axios from 'axios';
import emitter from '@/eventBus';

export default {
  data() {
    return {
      startWord: '',
      endWord: '',
      shortestPathResult: '',
    };
  },
  methods: {
    async calculateShortestPath() {
      try {
        const response = await axios.get('http://localhost:8081/api/graph/shortest-path', {
          params: {
            word1: this.startWord,
            word2: this.endWord
          }
        });
        const data = response.data;
        if (data.startsWith('0')) {
          const words = data.split(' ');
          if (words.length === 3) {
            this.bridgeWordResult = `No ${words[1]} and ${words[2]} in the graph!`;
          } else {
            this.bridgeWordResult = `No ${words[1]} in the graph!`;
          }
          // 清除高亮显示
          emitter.emit('clear-highlight-all');
        } else if (data.startsWith('\0')) {
          this.shortestPathResult = `No shortest path found from ${this.startWord} to ${this.endWord}!`;
          // 清除高亮显示
          emitter.emit('clear-highlight-all');
        } else {
          const shortestPathInfo = data.split(' ');
          const pathLength = shortestPathInfo[0];
          const path = shortestPathInfo.slice(1).join(' -> ');
          this.shortestPathResult = `Length: ${pathLength}; Path: ${path}`;
          emitter.emit('highlight-shortest-path', { path: shortestPathInfo.slice(1) }); // 高亮显示最短路径
        }
      } catch (error) {
        console.error('计算最短路径失败:', error);
      }
    },
  },
};
</script>

<style scoped>
.component-box {
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.component-box h3 {
  margin-top: 0;
}

.bridge-input-box {
  width: calc(100%); /* 考虑边框的宽度 */
  padding: 10px;
  height: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.output-box {
  border: 1px solid #ddd;
  padding: 10px;
  min-height: 50px;
  background-color: #fff;
}
</style>
