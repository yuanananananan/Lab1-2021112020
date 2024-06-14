package com.example.directedgraphbackend.controller;

import com.example.directedgraphbackend.model.Graph;
import com.example.directedgraphbackend.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/upload")
    public ResponseEntity<Graph> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            System.err.println("Upload failed: file is empty.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // 定义上传目录
            String uploadDir = "F:\\JAVA\\softEn\\lab1\\directed-graph-backend\\uploads\\";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // 创建目录
                System.out.println("Directory created: " + uploadPath.toString());
            }

            // 生成保存文件的完整路径
            String filePath = uploadDir + file.getOriginalFilename();
            File dest = new File(filePath);
            System.out.println("Saving file to: " + dest.getAbsolutePath());

            // 检查文件是否可以写入
            if (dest.exists() && !dest.canWrite()) {
                throw new IOException("Cannot write to file: " + dest.getAbsolutePath());
            }

            // 实际写入文件操作
            try {
                FileCopyUtils.copy(file.getBytes(), dest);
                System.out.println("File saved successfully.");
            } catch (IOException e) {
                System.err.println("File transfer failed: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 使用上传的文件处理图数据
            Graph graph = graphService.uploadFile(filePath);
            return ResponseEntity.ok(graph);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/show")
    public ResponseEntity<String> showDirectedGraph(@RequestBody Graph graph) {
        // 实现展示有向图的逻辑
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented yet");
    }

    @GetMapping("/bridge-words")
    public ResponseEntity<String> queryBridgeWords(@RequestParam("word1") String word1,
                                                   @RequestParam("word2") String word2) {
        String result = graphService.queryBridgeWords(word1, word2);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/new-text")
    public ResponseEntity<String> generateNewText(@RequestBody String inputText) {
        String newText = graphService.generateNewText(inputText);
        return ResponseEntity.ok(newText);
    }

    @GetMapping("/shortest-path")
    public ResponseEntity<String> calcShortestPath(@RequestParam("word1") String word1,
                                                   @RequestParam("word2") String word2) {
        String result = graphService.calcShortestPath(word1, word2);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/random-walk")
    public ResponseEntity<String> randomWalk() {
        String result = graphService.randomWalk();
        return ResponseEntity.ok(result);
    }
}
