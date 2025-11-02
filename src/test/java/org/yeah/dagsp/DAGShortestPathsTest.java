package org.yeah.dagsp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathsTest {

    private record EW(int to, int w) implements DAGShortestPaths.EdgeWLike {}

    @Test
    public void sssp_smallWeightedDag() {
        List<List<DAGShortestPaths.EdgeWLike>> dag = new ArrayList<>();
        for (int i = 0; i < 4; i++) dag.add(new ArrayList<>());
        dag.get(0).add(new EW(1,5));
        dag.get(0).add(new EW(2,2));
        dag.get(2).add(new EW(1,1));
        dag.get(1).add(new EW(3,3));
        dag.get(2).add(new EW(3,10));

        List<Integer> topo = List.of(0,2,1,3);

        DAGShortestPaths algo = new DAGShortestPaths();
        var res = algo.sssp(dag, topo, 0);

        assertEquals(0, res.dist[0]);
        assertEquals(3, res.dist[1]);
        assertEquals(2, res.dist[2]);
        assertEquals(6, res.dist[3]);
        var path03 = res.reconstructPath(0, 3);
        assertEquals(List.of(0,2,1,3), path03);
    }
}
