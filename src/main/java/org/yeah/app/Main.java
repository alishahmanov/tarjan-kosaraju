package org.yeah.app;

import org.yeah.io.JSONIO;
import org.yeah.scc.TarjanSCC;

import java.nio.file.Path;

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

        // Tarjan SCC
        TarjanSCC scc = new TarjanSCC();
        TarjanSCC.Result res = scc.run(loaded.g);

        System.out.println("SCC count = " + res.compCount);
        for (int cid = 0; cid < res.components.size(); cid++) {
            System.out.println("  comp " + cid + " size=" + res.components.get(cid).size()
                    + " : " + res.components.get(cid));
        }
    }
}
