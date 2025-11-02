package org.yeah.app;

import org.yeah.dagsp.DAGShortestPaths;
import org.yeah.dagsp.DAGLongestPath;
import org.yeah.dagsp.Adapters;
import org.yeah.io.JSONIO;
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

        TarjanSCC scc = new TarjanSCC();
        TarjanSCC.Result sccRes = scc.run(loaded.g);
        System.out.println("SCC count = " + sccRes.compCount);
        for (int cid = 0; cid < sccRes.components.size(); cid++) {
            System.out.println("  comp " + cid + " size=" + sccRes.components.get(cid).size()
                    + " : " + sccRes.components.get(cid));
        }

        CondensationWeightedBuilder wb = new CondensationWeightedBuilder();
        CondensationWeightedBuilder.CondensedW condensedW = wb.build(loaded.g, sccRes);

        System.out.println("\nWeighted Condensation DAG:");
        for (int u = 0; u < condensedW.compCount; u++) {
            System.out.print("  " + u + " -> ");
            System.out.println(condensedW.dagW.get(u));
        }

        TopologicalSortKahn kahn = new TopologicalSortKahn();
        List<List<Integer>> dagPlain = new ArrayList<>();
        for (int u = 0; u < condensedW.compCount; u++) {
            List<Integer> out = new ArrayList<>();
            for (var e : condensedW.dagW.get(u)) out.add(e.to);
            dagPlain.add(out);
        }
        List<Integer> topo = kahn.order(dagPlain);
        System.out.println("\nTopological order of components: " + topo);

        int compSource = (loaded.source != null) ? sccRes.compOf[loaded.source] : topo.get(0);

        List<List<DAGShortestPaths.EdgeWLike>> dagLike = new ArrayList<>();
        for (int u = 0; u < condensedW.compCount; u++) {
            List<DAGShortestPaths.EdgeWLike> row = new ArrayList<>();
            for (var e : condensedW.dagW.get(u)) row.add(Adapters.adapt(e));
            dagLike.add(row);
        }

        DAGShortestPaths sssp = new DAGShortestPaths();
        DAGShortestPaths.Result rS = sssp.sssp(dagLike, topo, compSource);
        System.out.println("\nShortest distances from component " + compSource + ":");
        for (int c = 0; c < condensedW.compCount; c++) {
            String d = (rS.dist[c] >= DAGShortestPaths.INF) ? "INF" : String.valueOf(rS.dist[c]);
            System.out.println("  comp " + c + " = " + d);
        }

        int targetS = topo.get(topo.size() - 1);
        var pathS = rS.reconstructPath(compSource, targetS);
        System.out.println("\nExample shortest path " + compSource + " -> " + targetS + ": " + pathS);

        DAGLongestPath lpp = new DAGLongestPath();
        DAGLongestPath.Result rL = lpp.lpp(dagLike, topo, compSource);
        int targetL = rL.argmax();
        var pathL = rL.reconstructPath(compSource, targetL);
        System.out.println("\nCritical path start=" + compSource + " end=" + targetL);
        System.out.println("Critical length = " + rL.dist[targetL]);
        System.out.println("Critical path = " + pathL);
    }
}
