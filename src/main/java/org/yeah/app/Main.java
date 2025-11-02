package org.yeah.app;

import org.yeah.dagsp.DAGShortestPaths;
import org.yeah.dagsp.Adapters;
import org.yeah.io.JSONIO;
import org.yeah.scc.CondensationBuilder;
import org.yeah.scc.CondensationWeightedBuilder;
import org.yeah.scc.TarjanSCC;
import org.yeah.topo.TopologicalSortKahn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: mvn -q -Dexec.args=\"data/sample.json\" exec:java");
            System.exit(1);
        }
        JSONIO.Loaded loaded = JSONIO.load(Path.of(args[0]));
        System.out.println("Loaded graph: n=" + loaded.g.n +
                ", directed=" + loaded.g.directed +
                ", edges=" + loaded.g.edges.size());
        System.out.println("Source: " + loaded.source +
                ", weight_model: " + loaded.weightModel);

        // 1) SCC
        TarjanSCC scc = new TarjanSCC();
        TarjanSCC.Result sccRes = scc.run(loaded.g);
        System.out.println("SCC count = " + sccRes.compCount);
        for (int cid = 0; cid < sccRes.components.size(); cid++) {
            System.out.println("  comp " + cid + " size=" + sccRes.components.get(cid).size()
                    + " : " + sccRes.components.get(cid));
        }

        // 2) Взвешенная конденсация
        CondensationWeightedBuilder wb = new CondensationWeightedBuilder();
        CondensationWeightedBuilder.CondensedW condensedW = wb.build(loaded.g, sccRes);

        System.out.println("\nWeighted Condensation DAG:");
        for (int u = 0; u < condensedW.compCount; u++) {
            System.out.print("  " + u + " -> ");
            System.out.println(condensedW.dagW.get(u));
        }

        // 3) Топосорт компонент
        TopologicalSortKahn kahn = new TopologicalSortKahn();
        // получим безвесовую структуру для подсчёта порядка
        List<List<Integer>> dagPlain = new ArrayList<>();
        for (int u = 0; u < condensedW.compCount; u++) {
            List<Integer> out = new ArrayList<>();
            for (var e : condensedW.dagW.get(u)) out.add(e.to);
            dagPlain.add(out);
        }
        List<Integer> topo = kahn.order(dagPlain);
        System.out.println("\nTopological order of components: " + topo);

        // 4) SSSP по DAG конденсации от компоненты, содержащей исходную вершину source
        int compSource = (loaded.source != null) ? sccRes.compOf[loaded.source] : topo.get(0);
        DAGShortestPaths sssp = new DAGShortestPaths();

        // адаптируем тип ребра к EdgeWLike
        List<List<DAGShortestPaths.EdgeWLike>> dagLike = new ArrayList<>();
        for (int u = 0; u < condensedW.compCount; u++) {
            List<DAGShortestPaths.EdgeWLike> row = new ArrayList<>();
            for (var e : condensedW.dagW.get(u)) row.add(Adapters.adapt(e));
            dagLike.add(row);
        }

        DAGShortestPaths.Result r = sssp.sssp(dagLike, topo, compSource);

        System.out.println("\nShortest distances from component " + compSource + ":");
        for (int c = 0; c < condensedW.compCount; c++) {
            String d = (r.dist[c] >= DAGShortestPaths.INF) ? "INF" : String.valueOf(r.dist[c]);
            System.out.println("  comp " + c + " = " + d);
        }

        // пример: восстановим путь до последней в топосорт компонент
        int compTarget = topo.get(topo.size() - 1);
        var path = r.reconstructPath(compSource, compTarget);
        System.out.println("\nExample path " + compSource + " -> " + compTarget + ": " + path);
    }
}
