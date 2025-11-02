package org.yeah.app;

import org.yeah.io.JSONIO;
import org.yeah.scc.CondensationBuilder;
import org.yeah.scc.TarjanSCC;
import org.yeah.topo.TopologicalSortKahn;

import java.nio.file.Path;
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

        // 2) Конденсация -> DAG
        CondensationBuilder builder = new CondensationBuilder();
        CondensationBuilder.Condensed condensed = builder.build(loaded.g, sccRes);
        System.out.println("\nCondensation DAG:");
        for (int u = 0; u < condensed.compCount; u++) {
            System.out.println("  " + u + " -> " + condensed.dag.get(u));
        }

        // 3) Топологическая сортировка компонент
        TopologicalSortKahn kahn = new TopologicalSortKahn();
        List<Integer> topo = kahn.order(condensed.dag);
        System.out.println("\nTopological order of components: " + topo);

        // (опционально) производный порядок исходных задач:
        // выводим вершины по порядку компонент
        System.out.println("Derived task order (grouped by component):");
        for (int c : topo) {
            System.out.println("  comp " + c + ": " + condensed.components.get(c));
        }
    }
}
