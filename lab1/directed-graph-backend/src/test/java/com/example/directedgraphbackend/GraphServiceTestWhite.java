package com.example.directedgraphbackend;

import com.example.directedgraphbackend.service.GraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphServiceTestWhite {

    private GraphService graphService;

    @BeforeEach
    public void setUp() {
        graphService = new GraphService();
        String filePath = getClass().getClassLoader().getResource("testcaseWhite.txt").getPath();
        graphService.uploadFile(filePath);
    }

    /**
     * 测试两个单词都不存在的情况
     * 预期输出：0 nonexistent1 nonexistent2
     */
    @Test
    public void testCalcShortestPath_BothWordsNonexistent() {
        String result = graphService.calcShortestPath("nonexistent1", "nonexistent2");
        assertEquals("0 nonexistent1 nonexistent2", result);
    }

    /**
     * 测试第一个单词不存在的情况
     * 预期输出：0 nonexistent1
     */
    @Test
    public void testCalcShortestPath_FirstWordNonexistent() {
        String result = graphService.calcShortestPath("nonexistent1", "word2");
        assertEquals("0 nonexistent1", result);
    }

    /**
     * 测试第二个单词不存在的情况
     * 预期输出：0 word1
     */
    @Test
    public void testCalcShortestPath_SecondWordNonexistent() {
        String result = graphService.calcShortestPath("word1", "nonexistent2");
        assertEquals("0 nonexistent2", result);
    }

    /**
     * 测试两个单词之间有路径的情况
     * 预期输出：路径的距离值和路径
     */
    @Test
    public void testCalcShortestPath_PathExists() {
        String result = graphService.calcShortestPath("word1", "word4");
        assertTrue(result.matches("\\d+ word1 word2 word3 word4"));
    }
}
