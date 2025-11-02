package org.yeah.dagsp;

import org.yeah.scc.CondensationWeightedBuilder;

public class Adapters {
    public static DAGShortestPaths.EdgeWLike adapt(CondensationWeightedBuilder.EdgeW e) {
        return new DAGShortestPaths.EdgeWLike() {
            @Override public int to() { return e.to; }
            @Override public int w() { return e.w; }
        };
    }
}
