package org.yeah.app;

import org.yeah.io.JSONIO;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar app.jar <path-to-json>");
            System.exit(1);
        }
        JSONIO.Loaded loaded = JSONIO.load(Path.of(args[0]));
        System.out.println("Loaded graph: n=" + loaded.g.n + 
                           ", directed=" + loaded.g.directed + 
                           ", edges=" + loaded.g.edges.size());
        System.out.println("Source: " + loaded.source + 
                           ", weight_model: " + loaded.weightModel);
    }
}
