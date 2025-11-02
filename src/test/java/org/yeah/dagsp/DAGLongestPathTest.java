package org.yeah.dagsp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGLongestPathTest {

    private record EW(int to, int w) implements DAGShortestPaths.EdgeWLike {}

    @Test
    public void lpp_smallWeightedDag() {
        List<List<DAGShortestPaths.EdgeWLike>> dag = new ArrayList<>();
        for (int i = 0; i < 5; i++) dag.add(new ArrayList<>());
        dag.get(0).add(new EW(1,2));
        dag.get(0).add(new EW(2,5));
        dag.get(1).add(new EW(3,4));
        dag.get(2).add(new EW(3,1));
        dag.get(3).add(new EW(4,3));
        List<Integer> topo = List.of(0,1,2,3,4);

        DAGLongestPath algo = new DAGLongestPath();
        var res = algo.lpp(dag, topo, 0);

        assertEquals(0, res.dist[0]);
        assertEquals(2, res.dist[1]);
        assertEquals(5, res.dist[2]);
        assertEquals(6, res.dist[3]);
        assertEquals(9, res.dist[4]);
        var path = res.reconstructPath(0, 4);
        assertEquals(List.of(0,1,3,4), path);
    }
}
