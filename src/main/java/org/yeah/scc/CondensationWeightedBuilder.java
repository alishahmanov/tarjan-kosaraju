package org.yeah.scc;

import org.yeah.model.Edge;
import org.yeah.model.Graph;

import java.util.*;


public class CondensationWeightedBuilder {

    public static final class EdgeW {
        public final int to;
        public final int w;
        public EdgeW(int to, int w) { this.to = to; this.w = w; }
        @Override public String toString() { return "(" + to + "," + w + ")"; }
    }

    public static final class CondensedW {
        public final int compCount;
        public final List<List<EdgeW>> dagW;
        public final int[] compOf;
        public final List<List<Integer>> components;

        public CondensedW(int compCount, List<List<EdgeW>> dagW, int[] compOf, List<List<Integer>> components) {
            this.compCount = compCount;
            this.dagW = dagW;
            this.compOf = compOf;
            this.components = components;
        }
    }

    public CondensedW build(Graph g, TarjanSCC.Result scc) {
        int k = scc.compCount;

        Map<Integer, Integer>[] tmp = new HashMap[k];
        for (int i = 0; i < k; i++) tmp[i] = new HashMap<>();

        for (Edge e : g.edges) {
            int cu = scc.compOf[e.u()];
            int cv = scc.compOf[e.v()];
            if (cu == cv) continue;
            tmp[cu].merge(cv, e.w(), Math::min);
        }

        List<List<EdgeW>> dagW = new ArrayList<>(k);
        for (int u = 0; u < k; u++) {
            List<EdgeW> list = new ArrayList<>();
            for (var entry : tmp[u].entrySet()) {
                list.add(new EdgeW(entry.getKey(), entry.getValue()));
            }
            dagW.add(list);
        }

        return new CondensedW(k, dagW, scc.compOf, scc.components);
    }
}
