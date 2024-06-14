package com.example.directedgraphbackend.service;

import com.example.directedgraphbackend.model.*;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.SyncFailedException;
import java.util.*;

@Service
public class GraphService {
    private Graph graph = new Graph();

    public Graph uploadFile(String filePath) {
        // Graph graph = new Graph();
        graph.getNodes().clear();
        graph.getAdjList().clear();

        Set<String> addedWords = new HashSet<>();
        String prevWord = null;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] words = line.split("[\\s\\p{Punct}]+");
                for (String word : words) {
                    word = word.toLowerCase(); // 将单词转换为小写形式
                    if (!addedWords.contains(word)) {
                        graph.addNode(word);
                        addedWords.add(word);
                    }
                    if (prevWord != null) {
                        graph.addEdge(prevWord, word);
                    }
                    prevWord = word;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public void showDirectedGraph(Graph graph) {
    }

    public String queryBridgeWords(String word1, String word2) {
        // System.out.println(graph.getAdjList());
        Set<String> bridgeWords = new HashSet<>();
        Node node1 = graph.getNodes().get(word1.toLowerCase());
        // System.out.println(node1);
        Node node2 = graph.getNodes().get(word2.toLowerCase());
        // System.out.println(node2);

        if (node1 == null && node2 == null) {
            return "0 " + word1 + " " + word2;
        } else if (node1 == null) {
            return "0 " + word1;
        } else if (node2 == null) {
            return "0 " + word2;
        }

        for (Edge edge1 : graph.getAdjList().get(node1)) {
            for (Edge edge2 : graph.getAdjList().get(edge1.getTo())) {
                if (edge2.getTo().equals(node2)) {
                    bridgeWords.add(edge1.getTo().getWord());
                }
            }
        }

        if (bridgeWords.isEmpty()) {
            // System.out.println("noooooo!");
            return "\0";
        }

        return "1 " + String.join(" ", bridgeWords);
    }

    public String generateNewText(String inputText) {
        String[] words = inputText.split("[\\s\\p{Punct}]+");
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i].toLowerCase()).append(" ");
            String bridgeWords = queryBridgeWords(words[i].toLowerCase(), words[i + 1].toLowerCase());
            // System.out.println(bridgeWords);
            if (bridgeWords.startsWith("1")) {
                String[] bridgeWordsArray = bridgeWords.substring(bridgeWords.indexOf(" ") + 1).split(" ");
                String randomBridgeWord = bridgeWordsArray[new Random().nextInt(bridgeWordsArray.length)];
                newText.append(randomBridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        // System.out.println(newText.toString());

        return newText.toString();
    }

    public String calcShortestPath(String word1, String word2) {
        Node node1 = graph.getNodes().get(word1.toLowerCase());
        Node node2 = graph.getNodes().get(word2.toLowerCase());

        if (node1 == null && node2 == null) {
            return "0 " + word1 + " " + word2;
        } else if (node1 == null) {
            return "0 " + word1;
        } else if (node2 == null) {
            return "0 " + word2;
        }

        // 使用 Dijkstra 算法计算最短路径
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Node node : graph.getNodes().values()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }
        distances.put(node1, 0);
        queue.add(node1);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Edge edge : graph.getAdjList().get(current)) {
                Node neighbor = edge.getTo();
                int newDist = distances.get(current) + edge.getWeight();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (distances.get(node2) == Integer.MAX_VALUE) {
            return "\0";
        }

        // 构建最短路径
        List<String> path = new ArrayList<>();
        for (Node at = node2; at != null; at = predecessors.get(at)) {
            path.add(at.getWord());
        }
        Collections.reverse(path);

        return distances.get(node2)+ " " + String.join(" ", path);
    }

    public String randomWalk() {
        // System.out.println("fine");
        StringBuilder result = new StringBuilder();
        // 随机选择起始节点
        Random random = new Random();
        List<Node> nodes = new ArrayList<>(graph.getNodes().values());
        Node currentNode = nodes.get(random.nextInt(nodes.size()));

        // 用于记录访问过的边
        Set<Edge> visitedEdges = new HashSet<>();

        // 设置最大迭代次数，或者根据其他条件设置结束条件
        int maxIterations = 100; // 设置一个较大的数值，确保不会无限循环
        int iterations = 0;

        while (iterations < maxIterations) {
            // 将当前节点的单词添加到结果中
            result.append(currentNode.getWord()).append(" ");

            // 随机选择一个相邻节点进行移动
            List<Edge> neighbors = graph.getAdjList().get(currentNode);
            if (neighbors != null && !neighbors.isEmpty()) {
                Edge randomNeighborEdge = neighbors.get(random.nextInt(neighbors.size()));
                if (visitedEdges.contains(randomNeighborEdge)) {
                    // 如果这条边已经访问过，跳出循环
                    break;
                }
                // 标记这条边为已访问
                visitedEdges.add(randomNeighborEdge);
                // 移动到相邻节点
                currentNode = randomNeighborEdge.getTo();
            } else {
                // 如果没有相邻节点，则跳出循环
                break;
            }

            iterations++;
        }

        return result.toString();
    }

}
