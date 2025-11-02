package org.yeah.scc;

import org.yeah.model.Edge;
import org.yeah.model.Graph;

import java.util.*;

public class CondensationBuilder {

    public static final class Condensed {
        public final int compCount;
        public final List<List<Integer>> dag;
        public final int[] compOf;
        public final List<List<Integer>> components;

        public Condensed(int compCount, List<List<Integer>> dag, int[] compOf, List<List<Integer>> components) {
            this.compCount = compCount;
            this.dag = dag;
            this.compOf = compOf;
            this.components = components;
        }
    }

    public Condensed build(Graph g, TarjanSCC.Result scc) {
        int k = scc.compCount;
        @SuppressWarnings("unchecked")
        Set<Integer>[] dagSet = new HashSet[k];
        for (int i = 0; i < k; i++) dagSet[i] = new HashSet<>();

        for (Edge e : g.edges) {
            int cu = scc.compOf[e.u()];
            int cv = scc.compOf[e.v()];
            if (cu != cv) dagSet[cu].add(cv);
        }

        List<List<Integer>> dag = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            dag.add(new ArrayList<>(dagSet[i]));
        }

        return new Condensed(k, dag, scc.compOf, scc.components);
    }
}
