<template>
  <div class="component-box">
    <h3>有向图可视化</h3>
    <input type="file" @change="onFileChange" />
    <button @click="generateGraph">生成有向图</button>
    <svg width="0" height="0" style="position:absolute; z-index:-1;">
      <defs>
        <marker id="arrow-highlighted1" viewBox="0 -5 10 10" refX="20" refY="0" markerWidth="6" markerHeight="6" orient="auto">
          <path d="M0,-5L10,0L0,5" fill="lightskyblue"/>
        </marker>
        <marker id="arrow-highlighted2" viewBox="0 -5 10 10" refX="20" refY="0" markerWidth="6" markerHeight="6" orient="auto">
          <path d="M0,-5L10,0L0,5" fill="salmon"/>
        </marker>
      </defs>
    </svg>
    <svg ref="svg" class="graph-container" width="2000" height="2000"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3';
import axios from 'axios';
import emitter from '@/eventBus'; // 导入事件总线

export default {
  data() {
    return {
      selectedFile: null,
      simulation: null,
    };
  },
  methods: {
    onFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    async generateGraph() {
      if (this.selectedFile) {
        try {
          const formData = new FormData();
          formData.append('file', this.selectedFile);

          const response = await axios.post('http://localhost:8081/api/graph/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });

          const graph = response.data;
          const nodes = Object.values(graph.nodes).map(node => ({ id: node.word }));
          const links = [];
          // Push edges with weight to links array
          Object.values(graph.adjList).forEach(edges => {
            edges.forEach(edge => {
              links.push({
                source: edge.from.word,
                target: edge.to.word,
                weight: edge.weight
              });
            });
          });

          this.renderGraph(nodes, links);
        } catch (error) {
          console.error('文件上传失败:', error);
        }
      } else {
        alert('请先选择一个文件');
      }
    },
    renderGraph(nodes, links) {
      const svg = d3.select(this.$refs.svg);
      svg.selectAll('*').remove();

      this.simulation = d3.forceSimulation(nodes)
          .force('link', d3.forceLink(links).id(d => d.id).distance(20))
          .force('charge', d3.forceManyBody().strength(-200))
          .force('center', d3.forceCenter(300, 200));

      svg.append('defs').selectAll('marker')
          .data(['arrow'])
          .enter().append('marker')
          .attr('id', d => d)
          .attr('viewBox', '0 -5 10 10')
          .attr('refX', 20)
          .attr('refY', 0)
          .attr('markerWidth', 6)
          .attr('markerHeight', 6)
          .attr('orient', 'auto')
          .append('path')
          .attr('d', 'M0,-5L10,0L0,5')
          .attr('fill', '#999');

      const link = svg.selectAll('.link')
          .data(links)
          .enter().append('line')
          .attr('class', 'link')
              .attr('stroke', '#999')
              .attr('stroke-width', 2)
              .attr('marker-end', 'url(#arrow)');

      // 添加边的权值文本
      const text = svg.selectAll('.text')
          .data(links)
          .enter().append('text')
          .attr('class', 'text')
          .attr('text-anchor', 'middle')
          .attr('fill', 'black')
          .text(d => d.weight)
          .attr('dy', '-0.5em');

      const node = svg.selectAll('.node')
          .data(nodes)
          .enter().append('g')
          .attr('class', 'node')
          .call(d3.drag()
              .on('start', this.dragstarted)
              .on('drag', this.dragged)
              .on('end', this.dragended));

      node.append('circle')
          .attr('r', 10)
          .attr('fill', 'steelblue');

      node.append('text')
          .attr('x', 10)
          .attr('y', 4)
          .text(d => d.id);

      this.simulation.on('tick', () => {
        link
            .attr('x1', d => d.source.x)
            .attr('y1', d => d.source.y)
            .attr('x2', d => d.target.x)
            .attr('y2', d => d.target.y);

        // 更新文本的位置
        text
            .attr('x', d => (d.source.x + d.target.x) / 2)
            .attr('y', d => (d.source.y + d.target.y) / 2);

        node.attr('transform', d => `translate(${d.x},${d.y})`);
      });

      // 订阅事件，高亮显示桥接词节点
      emitter.on('highlight-bridge-words', ({ word1, word2, bridgeWords }) => {
        node.selectAll('circle')
            .attr('fill', d => {
              if (d.id === word1 || d.id === word2) {
                return 'gold'; // 高亮显示颜色
              } else if (bridgeWords.includes(d.id)) {
                return 'lightseagreen';
              }
              return 'steelblue'; // 默认颜色
            });
        link.attr('stroke', d => {
              // 判断当前边的源节点和目标节点是否都在路径上
              if ((d.source.id === word1 && bridgeWords.includes(d.target.id)) || (bridgeWords.includes(d.source.id) && d.target.id === word2)) {
                return 'lightskyblue'; // 高亮显示颜色
              }
              return '#999'; // 默认颜色
            })
            .attr('marker-end', d => {
              // 根据边的颜色设置箭头的颜色
              if ((d.source.id === word1 && bridgeWords.includes(d.target.id)) || (bridgeWords.includes(d.source.id) && d.target.id === word2)) {
                return 'url(#arrow-highlighted1)'; // 高亮显示箭头
              }
              return 'url(#arrow)'; // 默认箭头
            });
      });
      // emitter.on('clear-highlight', () => {
      //   node.selectAll('circle')
      //       // eslint-disable-next-line no-unused-vars
      //       .attr('fill', d => {
      //         return 'steelblue';
      //       })
      // });

      // emitter.on('highlight-shortest-path', ({ path }) => {
      //   this.highlightedPath = path; // 更新最短路径信息
      //
      //   // 高亮显示路径上的边和箭头
      //   link.attr('stroke', d => {
      //         // 判断当前边的源节点和目标节点是否都在路径上
      //         if (path.includes(d.source.id) && path.includes(d.target.id)) {
      //           return 'salmon'; // 高亮显示颜色
      //         }
      //         return '#999'; // 默认颜色
      //       })
      //       .attr('marker-end', d => {
      //         // 根据边的颜色设置箭头的颜色
      //         if (path.includes(d.source.id) && path.includes(d.target.id)) {
      //           return 'url(#arrow-highlighted2)'; // 高亮显示箭头
      //         }
      //         return 'url(#arrow)'; // 默认箭头
      //       });
      emitter.on('highlight-shortest-path', ({ path }) => {
          this.highlightedPath = path; // 更新最短路径信息

          // 创建一个集合来存储路径上的边（源节点和目标节点的对）
          const pathEdges = new Set();
          for (let i = 0; i < path.length - 1; i++) {
            pathEdges.add(`${path[i]}-${path[i+1]}`);
          }

          // 高亮显示路径上的边和箭头
          link.attr('stroke', d => {
            // 判断当前边是否在路径的边集合中
            if (pathEdges.has(`${d.source.id}-${d.target.id}`)) {
              return 'salmon'; // 高亮显示颜色
            }
            return '#999'; // 默认颜色
          })
              .attr('marker-end', d => {
                // 根据边的颜色设置箭头的颜色
                if (pathEdges.has(`${d.source.id}-${d.target.id}`)) {
                  return 'url(#arrow-highlighted2)'; // 高亮显示箭头
                }
                return 'url(#arrow)'; // 默认箭头
              });
        // 高亮显示路径上的节点
        node.selectAll('circle')
            .attr('fill', d => {
              if (path.includes(d.id)) {
                return 'lightpink'; // 高亮显示颜色
              }
              return 'steelblue'; // 默认颜色
            });
      });
      emitter.on('clear-highlight-all', () => {
        // eslint-disable-next-line no-unused-vars
        link.attr('stroke', d => {
              return '#999'; // 默认颜色
            })
            // eslint-disable-next-line no-unused-vars
            .attr('marker-end', d => {
              return 'url(#arrow)'; // 默认箭头
            });
        node.selectAll('circle')
            .attr('fill', 'steelblue'); // 恢复默认节点的颜色
      });
    },
    dragstarted(event, d) {
      if (!event.active) this.simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    },
    dragged(event, d) {
      d.fx = event.x;
      d.fy = event.y;
    },
    dragended(event, d) {
      if (!event.active) this.simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    },
  },
};
</script>

<style>
.graph-container {
  width: 100%;
  height: 400px;
  border: 1px solid #ddd;
  background-color: #fff;
}
</style>
