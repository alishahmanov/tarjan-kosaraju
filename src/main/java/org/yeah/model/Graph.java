package org.yeah.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<Edge> edges = new ArrayList<>();

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
    }

    public void addEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
    }
}
