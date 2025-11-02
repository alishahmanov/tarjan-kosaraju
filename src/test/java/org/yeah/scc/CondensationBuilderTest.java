
package org.yeah.scc;

import org.junit.jupiter.api.Test;
import org.yeah.model.Graph;

import static org.junit.jupiter.api.Assertions.*;

public class CondensationBuilderTest {

    @Test
    public void condensation_removes_selfLoops_and_duplicates() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(2, 3, 1);

        TarjanSCC scc = new TarjanSCC();
        TarjanSCC.Result r = scc.run(g);
        CondensationBuilder b = new CondensationBuilder();
        CondensationBuilder.Condensed c = b.build(g, r);

        assertEquals(3, c.compCount);
        int compA = -1;
        for (int i = 0; i < c.components.size(); i++)
            if (c.components.get(i).size() == 2) compA = i;
        assertNotEquals(-1, compA);
        assertEquals(1, c.dag.get(compA).size());
    }
}
