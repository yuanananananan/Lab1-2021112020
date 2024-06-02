package com.example.directedgraphbackend.controller;

import com.example.directedgraphbackend.model.Graph;
import com.example.directedgraphbackend.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:8080") // 或者允许所有 origins: @CrossOrigin(origins = "*")
@RequestMapping("/api/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/upload")
    public ResponseEntity<Graph> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // System.out.println("file ok!");
            // 保存上传的文件到服务器的某个位置
            String uploadDir = "uploads/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // String filePath = uploadDir + file.getOriginalFilename();
            String filePath = "D:\\Projects\\SoftwareEngineering\\lab1\\directed-graph-backend\\" + uploadDir + file.getOriginalFilename();
            File dest = new File(filePath);
            System.out.println(dest);
            file.transferTo(dest);

            // 使用上传的文件处理图数据
            Graph graph = graphService.uploadFile(filePath);
            return ResponseEntity.ok(graph);

        } catch (IOException e) {
            e.printStackTrace();
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
