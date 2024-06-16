package com.example.directedgraphbackend.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    private Node from;
    private Node to;
    private int weight;

    public void incrementWeight() {
        this.weight++;
    }
}
