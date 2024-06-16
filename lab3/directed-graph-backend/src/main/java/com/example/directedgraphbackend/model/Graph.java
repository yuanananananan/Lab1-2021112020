package com.example.directedgraphbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {
    private Map<String, Node> nodes = new HashMap<>();
    private Map<Node, List<Edge>> adjList = new HashMap<>();

    public void addNode(String word) {
        Node node = new Node(word);
        nodes.put(word, node);
        adjList.put(node, new ArrayList<>());
    }

    public void addEdge(String fromWord, String toWord) {
        Node from = nodes.get(fromWord);
        Node to = nodes.get(toWord);
        if (from != null && to != null) {
            List<Edge> edges = adjList.get(from);

            if (edges == null) {
                edges = new ArrayList<>();
                adjList.put(from, edges);
            }

            for (Edge edge : edges) {
                if (edge.getTo().equals(to)) {
                    edge.incrementWeight();
                    return;
                }
            }

            // 如果边不存在，添加新边
            Edge newEdge = new Edge(from, to, 1);
            edges.add(newEdge);
        }
    }
}
