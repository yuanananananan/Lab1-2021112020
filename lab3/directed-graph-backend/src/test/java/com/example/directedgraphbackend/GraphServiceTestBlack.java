package com.example.directedgraphbackend;

import com.example.directedgraphbackend.service.GraphService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GraphServiceTestBlack {
    private GraphService graphService;

    @Before
    public void setUp() {
        graphService = new GraphService();
        graphService.uploadFile("src/test/resources/testcaseBlack.txt"); // 假设这个文件已经存在并且包含测试数据
    }

    @Test
    public void testQueryBridgeWordsExists() {
        String result = graphService.queryBridgeWords("hello", "world");
        assertEquals("1 java", result);
    }

    @Test
    public void testQueryBridgeWordsNotExist() {
        String result = graphService.queryBridgeWords("world", "hello");
        assertEquals("\0", result);
    }

    @Test
    public void testQueryBridgeWordsWordNotInGraph() {
        String result = graphService.queryBridgeWords("foo", "world");
        assertEquals("0 foo", result);
    }

    @Test
    public void testQueryBridgeWordsWord2NotInGraph() {
        String result = graphService.queryBridgeWords("hello", "bar");
        assertEquals("0 bar", result);
    }

    @Test
    public void testQueryBridgeWordsWord1AndWord2NotInGraph() {
        String result = graphService.queryBridgeWords("foo", "bar");
        assertEquals("0 foo bar", result);
    }

    @Test
    public void testQueryBridgeWordsNonEmptyWords() {
        String result = graphService.queryBridgeWords("hello", "world");
        assertEquals("1 java", result);
    }

    @Test
    public void testQueryBridgeWordsEmptyWord1() {
        String result = graphService.queryBridgeWords("", "world");
        assertEquals("0 ", result);
    }

    @Test
    public void testQueryBridgeWordsEmptyWord2() {
        String result = graphService.queryBridgeWords("hello", "");
        assertEquals("0 ", result);
    }
    
}