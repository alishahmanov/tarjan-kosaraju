package org.yeah.scc;

import org.yeah.model.Edge;
import org.yeah.model.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Tarjan SCC: O(n + m). Нумерация компонент 0..(k-1). */
public class TarjanSCC {

    public static final class Result {
        public final int compCount;
        /** compOf[v] = id компоненты вершины v */
        public final int[] compOf;
        /** components[c] = список вершин компоненты c */
        public final List<List<Integer>> components;

        public Result(int compCount, int[] compOf, List<List<Integer>> components) {
            this.compCount = compCount;
            this.compOf = compOf;
            this.components = components;
        }
    }

    public Result run(Graph g) {
        int n = g.n;
        List<List<Integer>> adj = buildAdj(g);

        int[] disc = new int[n];
        int[] low = new int[n];
        boolean[] onStack = new boolean[n];
        Deque<Integer> st = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            disc[i] = -1;
            low[i] = -1;
        }

        List<List<Integer>> comps = new ArrayList<>();
        int[] time = {0};

        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) {
                dfs(v, adj, disc, low, onStack, st, time, comps);
            }
        }

        // Построим compOf[]
        int[] compOf = new int[n];
        for (int c = 0; c < comps.size(); c++) {
            for (int v : comps.get(c)) compOf[v] = c;
        }

        return new Result(comps.size(), compOf, comps);
    }

    private static void dfs(int v,
                            List<List<Integer>> adj,
                            int[] disc, int[] low,
                            boolean[] onStack, Deque<Integer> st,
                            int[] time,
                            List<List<Integer>> comps) {
        disc[v] = low[v] = time[0]++;
        st.push(v);
        onStack[v] = true;

        for (int to : adj.get(v)) {
            if (disc[to] == -1) {
                dfs(to, adj, disc, low, onStack, st, time, comps);
                low[v] = Math.min(low[v], low[to]);
            } else if (onStack[to]) {
                low[v] = Math.min(low[v], disc[to]);
            }
        }

        if (low[v] == disc[v]) {
            // корневая вершина компоненты
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int u = st.pop();
                onStack[u] = false;
                comp.add(u);
                if (u == v) break;
            }
            comps.add(comp);
        }
    }

    private static List<List<Integer>> buildAdj(Graph g) {
        List<List<Integer>> adj = new ArrayList<>(g.n);
        for (int i = 0; i < g.n; i++) adj.add(new ArrayList<>());
        for (Edge e : g.edges) {
            adj.get(e.u()).add(e.v());
            if (!g.directed) adj.get(e.v()).add(e.u()); // на всякий случай
        }
        return adj;
    }
}
