package org.yeah.dagsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAGShortestPaths {

    public static final int INF = 1_000_000_000;

    public static final class Result {
        public final int[] dist;
        public final int[] parent;
        public final int relaxations;
        public Result(int[] dist, int[] parent, int relaxations) {
            this.dist = dist;
            this.parent = parent;
            this.relaxations = relaxations;
        }
        public List<Integer> reconstructPath(int source, int target) {
            if (dist[target] >= INF) return List.of();
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = parent[v]) {
                path.add(v);
                if (v == source) break;
            }
            Collections.reverse(path);
            return path;
        }
    }

    public Result sssp(List<? extends List<EdgeWLike>> dagW, List<Integer> topo, int source) {
        int n = dagW.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        int relax = 0;
        for (int i = 0; i < n; i++) { dist[i] = INF; parent[i] = -1; }
        dist[source] = 0;
        for (int u : topo) {
            if (dist[u] >= INF) continue;
            for (EdgeWLike e : dagW.get(u)) {
                int v = e.to();
                int w = e.w();
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    relax++;
                }
            }
        }
        return new Result(dist, parent, relax);
    }

    public interface EdgeWLike {
        int to();
        int w();
    }
}
