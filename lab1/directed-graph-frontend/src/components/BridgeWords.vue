<template>
  <div class="component-box">
    <h3>查询桥接词</h3>
    <input class="input-box" v-model="word1" placeholder="输入第一个单词">
    <input class="input-box" v-model="word2" placeholder="输入第二个单词">
    <button @click="queryBridgeWord">查询</button>
    <div class="output-box">{{ bridgeWordResult }}</div>
  </div>
</template>

<script>
import axios from 'axios';
import emitter from '@/eventBus';

export default {
  data() {
    return {
      word1: '',
      word2: '',
      bridgeWordResult: '',
    };
  },
  methods: {
    async queryBridgeWord() {
      try {
        const response = await axios.get('http://localhost:8081/api/graph/bridge-words', {
          params: {
            word1: this.word1,
            word2: this.word2
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
          emitter.emit('clear-highlight-all');
        } else if (data.startsWith('1')) {
          const bridgeWords = data.split(' ').slice(1).join(', ');
          this.bridgeWordResult = `The bridge words from ${this.word1} to ${this.word2} are: ${bridgeWords}.`;
          emitter.emit('clear-highlight-all');
          emitter.emit('highlight-bridge-words', {
            word1: this.word1,
            word2: this.word2,
            bridgeWords: bridgeWords.split(', ')
          });
        } else {
          this.bridgeWordResult = `No bridge words from ${this.word1} to ${this.word2}!`;
          emitter.emit('clear-highlight-all');
        }
      } catch (error) {
        console.error('查询失败:', error);
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

.input-box {
  width: calc(100%);
  height: 10px;
  padding: 10px;
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
