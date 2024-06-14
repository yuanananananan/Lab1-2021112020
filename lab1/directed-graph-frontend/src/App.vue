<template>
  <div id="app">
    <header class="header">
      <h1 class="title">有向图可视化操作工具 v0.0.1</h1>
    </header>
    <div class="container">
      <div class="left-panel">
        <GraphVisualization />
      </div>
      <div class="right-panel">
        <BridgeWords />
        <NewTextGeneration />
        <ShortestPath />
        <RandomWalk />
      </div>
    </div>
  </div>
</template>

<script>
import GraphVisualization from './components/GraphVisualization.vue';
import BridgeWords from './components/BridgeWords.vue';
import NewTextGeneration from './components/NewTextGeneration.vue';
import ShortestPath from './components/ShortestPath.vue';
import RandomWalk from './components/RandomWalk.vue';

export default {
  style: require('@/assets/global.css'),
  components: {
    GraphVisualization,
    BridgeWords,
    NewTextGeneration,
    ShortestPath,
    RandomWalk,
  },
};
</script>

<style>
body {
  margin: 0;
  font-family: Arial, sans-serif;
}

#app {
  text-align: center;
}

.header {
  background-color: #A59DDA;
  color: black;
  padding: 30px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.title {
  color: #fffafa;
  margin: 0;
}

.container {
  display: flex;
  flex-wrap: wrap;
}

.left-panel {
  flex: 1;
  padding: 20px;
}

.right-panel {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 20px;
}

.component-box {
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.component-box h3 {
  margin-top: 0;
}

.component-box input,
.component-box button {
  margin: 5px 0;
  display: block;
}

.output-box {
  border: 1px solid #ddd;
  padding: 10px;
  min-height: 50px;
  background-color: #fff;
}
</style>