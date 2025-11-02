package org.yeah.runner;

import org.yeah.dagsp.DAGLongestPath;
import org.yeah.dagsp.DAGShortestPaths;
import org.yeah.dagsp.Adapters;
import org.yeah.io.JSONIO;
import org.yeah.metrics.Stopwatch;
import org.yeah.scc.CondensationWeightedBuilder;
import org.yeah.scc.TarjanSCC;
import org.yeah.topo.TopologicalSortKahn;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class BatchRunner {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: mvn -q -Dexec.mainClass=org.yeah.runner.BatchRunner -Dexec.args=\"data results\"");
            System.exit(1);
        }
        Path dataDir = Path.of(args[0]);
        Path outDir = Path.of(args[1]);
        Path out = outDir.resolve("summary.csv");
        CSV.writeHeader(out, "Dataset,n,edges,SCC_count,SCC_time_ns,Topo_time_ns,SP_relax,SP_time_ns,LP_relax,LP_time_ns");

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.json")) {
            for (Path p : ds) {
                runOne(p, out);
            }
        }
    }

    private static void runOne(Path json, Path out) throws Exception {
        JSONIO.Loaded loaded = JSONIO.load(json);
        TarjanSCC scc = new TarjanSCC();
        Stopwatch sw = new Stopwatch();

        sw.start();
        TarjanSCC.Result sccRes = scc.run(loaded.g);
        long tScc = sw.stop();

        CondensationWeightedBuilder wb = new CondensationWeightedBuilder();
        CondensationWeightedBuilder.CondensedW cw = wb.build(loaded.g, sccRes);

        List<List<Integer>> dagPlain = new ArrayList<>();
        for (int u = 0; u < cw.compCount; u++) {
            List<Integer> outNeighbors = new ArrayList<>();
            for (var e : cw.dagW.get(u)) outNeighbors.add(e.to);
            dagPlain.add(outNeighbors);
        }

        TopologicalSortKahn kahn = new TopologicalSortKahn();
        sw.start();
        List<Integer> topo = kahn.order(dagPlain);
        long tTopo = sw.stop();

        int compSource = (loaded.source != null) ? sccRes.compOf[loaded.source] : topo.get(0);
        List<List<DAGShortestPaths.EdgeWLike>> dagLike = new ArrayList<>();
        for (int u = 0; u < cw.compCount; u++) {
            List<DAGShortestPaths.EdgeWLike> row = new ArrayList<>();
            for (var e : cw.dagW.get(u)) row.add(Adapters.adapt(e));
            dagLike.add(row);
        }

        DAGShortestPaths sp = new DAGShortestPaths();
        sw.start();
        DAGShortestPaths.Result rS = sp.sssp(dagLike, topo, compSource);
        long tSp = sw.stop();

        DAGLongestPath lp = new DAGLongestPath();
        sw.start();
        DAGLongestPath.Result rL = lp.lpp(dagLike, topo, compSource);
        long tLp = sw.stop();

        int m = loaded.g.edges.size();
        String row = String.join(",",
                json.toString().replace('\\','/'),
                String.valueOf(loaded.g.n),
                String.valueOf(m),
                String.valueOf(sccRes.compCount),
                String.valueOf(tScc),
                String.valueOf(tTopo),
                String.valueOf(rS.relaxations),
                String.valueOf(tSp),
                String.valueOf(rL.relaxations),
                String.valueOf(tLp)
        );
        CSV.append(out, row);
    }
}
