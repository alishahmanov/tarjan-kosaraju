package org.yeah.scc;

import org.junit.jupiter.api.Test;
import org.yeah.model.Graph;

import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    public void smallGraph_twoCycles_plusSingleton() {
        // n=5
        // 0 <-> 1,   2 <-> 3,   4 одиночка
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);

        TarjanSCC algo = new TarjanSCC();
        TarjanSCC.Result r = algo.run(g);

        assertEquals(3, r.compCount); // {0,1}, {2,3}, {4}
        // Проверим что 0 и 1 в одной компоненте, 2 и 3 — в другой, а 4 — в своей
        assertEquals(r.compOf[0], r.compOf[1]);
        assertEquals(r.compOf[2], r.compOf[3]);
        assertNotEquals(r.compOf[0], r.compOf[2]);
        // 4 — singleton
        // найдём его компоненту и убедимся, что размер её = 1
        int comp4 = r.compOf[4];
        long size4 = r.components.get(comp4).size();
        assertEquals(1, size4);
    }
}
