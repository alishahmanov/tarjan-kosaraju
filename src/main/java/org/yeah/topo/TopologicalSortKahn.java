package org.yeah.topo;

import java.util.*;

public class TopologicalSortKahn {

    public List<Integer> order(List<List<Integer>> dag) {
        int n = dag.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (int v : dag.get(u)) indeg[v]++;

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);

        List<Integer> topo = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            topo.add(u);
            for (int v : dag.get(u)) {
                if (--indeg[v] == 0) q.addLast(v);
            }
        }
        return topo;
    }
}
