<template>
  <div class="component-box">
    <h3>随机游走</h3>
    <button @click="startRandomWalk" :disabled="timer !== null">开始</button>
    <button @click="stopRandomWalk" :disabled="timer === null">停止</button>
    <div class="output-box">{{ currentWords }}</div>
  </div>
</template>

<script>
import axios from 'axios';
import emitter from '@/eventBus';

export default {
  data() {
    return {
      randomWalkResult: [],
      currentWordIndex: 0,
      currentWords: '',
      timer: null,
    };
  },
  methods: {
    async startRandomWalk() {
      try {
        const response = await axios.get('http://localhost:8081/api/graph/random-walk');
        this.randomWalkResult = response.data.split(' '); // 将返回的结果按空格分割成单词数组
        this.currentWordIndex = 0; // 重置单词索引
        this.currentWords = ''; // 清空当前展示的单词
        emitter.emit('clear-highlight-all');
        this.startDisplayTimer(); // 开始定时展示单词
      } catch (error) {
        console.error('随机游走请求失败:', error);
      }
    },
    startDisplayTimer() {
      this.timer = setInterval(this.displayNextWord, 1000); // 每隔一秒展示一个单词
    },
    displayNextWord() {
      if (this.currentWordIndex < this.randomWalkResult.length) {
        if (this.currentWordIndex != 0 && this.currentWordIndex != this.randomWalkResult.length - 1) {
          this.currentWords += ' -> ' + this.randomWalkResult[this.currentWordIndex];
        } else {
          this.currentWords += this.randomWalkResult[this.currentWordIndex];
        }

        this.currentWordIndex++;
      } else {
        this.stopRandomWalk();
        // clearInterval(this.timer); // 当所有单词展示完毕后停止定时器
        // this.timer = null; // 将定时器置空
      }
    },
    stopRandomWalk() {
      clearInterval(this.timer); // 停止定时展示单词
      this.timer = null; // 将定时器置空
    },
  },
  beforeUnmount() {
    clearInterval(this.timer); // 组件销毁时停止定时器，避免内存泄漏
  },
};
</script>
