package org.yeah.dagsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAGLongestPath {

    public static final int NEG_INF = -1_000_000_000;

    public static final class Result {
        public final int[] dist;
        public final int[] parent;
        public Result(int[] dist, int[] parent) {
            this.dist = dist;
            this.parent = parent;
        }
        public List<Integer> reconstructPath(int source, int target) {
            if (dist[target] <= NEG_INF) return List.of();
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = parent[v]) {
                path.add(v);
                if (v == source) break;
            }
            Collections.reverse(path);
            return path;
        }
        public int argmax() {
            int idx = 0, best = dist[0];
            for (int i = 1; i < dist.length; i++) if (dist[i] > best) { best = dist[i]; idx = i; }
            return idx;
        }
    }

    public Result lpp(List<? extends List<DAGShortestPaths.EdgeWLike>> dagW, List<Integer> topo, int source) {
        int n = dagW.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) { dist[i] = NEG_INF; parent[i] = -1; }
        dist[source] = 0;
        for (int u : topo) {
            if (dist[u] <= NEG_INF) continue;
            for (var e : dagW.get(u)) {
                int v = e.to();
                int w = e.w();
                if (dist[v] < dist[u] + w) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                }
            }
        }
        return new Result(dist, parent);
    }
}
