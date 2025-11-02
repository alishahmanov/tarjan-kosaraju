
package org.yeah.topo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSortKahnTest {

    @Test
    public void simpleDag() {
        // 0->1, 0->2, 1->3, 2->3
        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < 4; i++) dag.add(new ArrayList<>());
        dag.get(0).add(1); dag.get(0).add(2);
        dag.get(1).add(3);
        dag.get(2).add(3);

        TopologicalSortKahn k = new TopologicalSortKahn();
        var order = k.order(dag);
        assertEquals(4, order.size());
        // 0 должен идти раньше 1 и 2; 1/2 раньше 3
        int p0 = order.indexOf(0);
        int p1 = order.indexOf(1);
        int p2 = order.indexOf(2);
        int p3 = order.indexOf(3);
        assertTrue(p0 < p1 && p0 < p2);
        assertTrue(p1 < p3 && p2 < p3);
    }
}
