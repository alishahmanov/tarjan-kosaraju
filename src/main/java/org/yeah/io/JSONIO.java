package org.yeah.io;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.yeah.model.Edge;
import org.yeah.model.Graph;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

public class JSONIO {

    public static class Input {
        public boolean directed;
        public int n;
        public List<InEdge> edges;
        public Integer source;
        @SerializedName("weight_model")
        public String weightModel;

        public static class InEdge { public int u, v, w; }
    }

    public static class Loaded {
        public final Graph g;
        public final Integer source;
        public final String weightModel;

        public Loaded(Graph g, Integer source, String weightModel) {
            this.g = g;
            this.source = source;
            this.weightModel = weightModel;
        }
    }

    public static Loaded load(Path path) throws Exception {
        try (FileReader fr = new FileReader(path.toFile())) {
            Gson gson = new Gson();
            Input in = gson.fromJson(fr, Input.class);

            Graph g = new Graph(in.n, in.directed);
            if (in.edges != null) {
                for (Input.InEdge e : in.edges) g.addEdge(e.u, e.v, e.w);
            }
            return new Loaded(g, in.source, in.weightModel);
        }
    }
}
