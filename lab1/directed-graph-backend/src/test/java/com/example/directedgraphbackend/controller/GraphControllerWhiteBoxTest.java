package com.example.directedgraphbackend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.directedgraphbackend.service.GraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class GraphControllerWhiteBoxTest {

    @Mock
    private GraphService graphService;

    @InjectMocks
    private GraphController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalcShortestPath_validPath() {
        when(graphService.calcShortestPath("start", "end")).thenReturn("start -> mid -> end");
        ResponseEntity<String> response = controller.calcShortestPath("start", "end");
        assertEquals("start -> mid -> end", response.getBody());
    }

    @Test
    public void testCalcShortestPath_noPath() {
        when(graphService.calcShortestPath("isolated1", "isolated2")).thenReturn("No path exists");
        ResponseEntity<String> response = controller.calcShortestPath("isolated1", "isolated2");
        assertEquals("No path exists", response.getBody());
    }

    @Test
    public void testCalcShortestPath_invalidInput() {
        when(graphService.calcShortestPath("notInGraph1", "end")).thenReturn("Invalid input");
        ResponseEntity<String> response = controller.calcShortestPath("notInGraph1", "end");
        assertEquals("Invalid input", response.getBody());
    }

    @Test
    public void testCalcShortestPath_sameWord() {
        when(graphService.calcShortestPath("word", "word")).thenReturn("word");
        ResponseEntity<String> response = controller.calcShortestPath("word", "word");
        assertEquals("word", response.getBody());
    }

    @Test
    public void testCalcShortestPath_minPath() {
        when(graphService.calcShortestPath("node1", "node2")).thenReturn("node1 -> node2");
        ResponseEntity<String> response = controller.calcShortestPath("node1", "node2");
        assertEquals("node1 -> node2", response.getBody());
    }
}
