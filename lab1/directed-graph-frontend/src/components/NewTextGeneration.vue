<template>
  <div class="component-box">
    <h3>生成新文本</h3>
    <textarea class="input-box" v-model="newTextInput" placeholder="输入内容"></textarea>
    <button @click="generateNewText">生成</button>
    <div class="output-box">{{ newTextResult }}</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      newTextInput: '',
      newTextResult: '',
    };
  },
  methods: {
    async generateNewText() {
      try {
        const response = await axios.post('http://localhost:8081/api/graph/new-text', this.newTextInput, {
          headers: {
            'Content-Type': 'text/plain',
          },
        });
        this.newTextResult = response.data;
      } catch (error) {
        console.error('生成新文本失败:', error);
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
  width: calc(100%); /* 考虑边框的宽度 */
  height: 70px;
  padding: 15px; /* 增加输入框的内边距 */
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  resize: none;
}

.output-box {
  border: 1px solid #ddd;
  padding: 10px;
  min-height: 50px;
  background-color: #fff;
}
</style>
