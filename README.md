# Assignment 4 — Smart City / Smart Campus Scheduling

Course: Design and Analysis of Algorithms
Student: Alihan (SE-2401)

## Overview

Tasks are modeled as a directed, edge-weighted graph:

Vertices = tasks

Edges = precedence constraints

Weights = edge costs/durations (edge model)

Pipeline: SCC (Tarjan) → Condensation DAG → Topological Order (Kahn) → DAG Shortest Paths → Critical (Longest) Path.
A batch runner exports timings and counters to CSV.

## Implemented

Tarjan’s SCC O(V+E)

Condensation DAG (merge multi-edges; weight(A→B) = min edge between components)

Topological Sort (Kahn)

DAG SSSP (DP along topo, path reconstruction)

DAG Longest/Critical Path (max-DP, path reconstruction)

Metrics: nanosecond timings per stage; relaxation counts (SSSP/LP)

## Project Structure
org.yeah/
  app/Main.java
  dagsp/{DAGShortestPaths.java, DAGLongestPath.java, Adapters.java}
  io/JSONIO.java
  metrics/Stopwatch.java
  model/{Graph.java, Edge.java}
  runner/{BatchRunner.java, CSV.java}
  scc/{TarjanSCC.java, CondensationBuilder.java, CondensationWeightedBuilder.java}
  topo/TopologicalSortKahn.java
tests in src/test/java/org/yeah/{scc,topo,dagsp}
data/      # *.json inputs
results/   # summary.csv (generated)

## Input (JSON)
{
  "directed": true,
  "n": 8,
  "edges": [{ "u": 0, "v": 1, "w": 3 }],
  "source": 4,
  "weight_model": "edge"
}

## Build & Run

Tests
```
mvn clean test
```

Single dataset
```
mvn -q exec:java -Dexec.args="data/sample.json"
```
or, if exec IDs are configured:
```
mvn -q exec:java@app -Dexec.args="data/sample.json"
```


Batch over folder → results/summary.csv
```
mvn -q exec:java -Dexec.mainClass=org.yeah.runner.BatchRunner -Dexec.args="data results"
```
or:
```
mvn -q exec:java@batch -Dexec.args="data results"
```

## Data Summary (9 datasets)

Weight model: edge weights. Counts are from the provided generator.

File	n	m	Type
small1.json	8	9	DAG
small2.json	9	10	cyclic
small3.json	10	14	DAG
medium1.json	12	15	DAG
medium2.json	15	20	cyclic
medium3.json	18	25	cyclic
large1.json	25	44	cyclic*
large2.json	40	80	cyclic
large3.json	45	129	cyclic

* large1.json включает случайные рёбра → почти наверняка содержит циклы.

## CSV Output (Batch)

Header

Dataset,n,edges,SCC_count,SCC_time_ns,Topo_time_ns,SP_relax,SP_time_ns,LP_relax,LP_time_ns


Each row = one dataset’s metrics (timings per stage + relaxation counts).

## Example (Single Run, abridged)
SCC count = 6
Topological order: [1, 5, 0, 4, 3, 2]
Shortest dist from comp 5: ... comp 2 = 8
Critical path length = 8, path = [5, 4, 3, 2]

## Complexity
Stage	Time	Space
Tarjan SCC	O(V+E)	O(V)
Condensation build	O(V+E)	O(V+E)
Topological sort (Kahn)	O(V+E)	O(V)
DAG SSSP	O(V+E)	O(V)
DAG Longest (Critical)	O(V+E)	O(V)

## Results
```
### Results — SCC
| Dataset            | n | m | SCC_count | SCC_time_ns |
|-------------------:|--:|--:|----------:|------------:|
| data/sample.json   | 8 | 7 | 6         | 67500       |

### Results — Topological Sort
| Dataset            | n | m | Topo_time_ns |
|-------------------:|--:|--:|-------------:|
| data/sample.json   | 8 | 7 | 11458        |

### Results — DAG Shortest / Longest Paths
| Dataset            | SP_relax | SP_time_ns | LP_relax | LP_time_ns |
|-------------------:|---------:|-----------:|---------:|-----------:|
| data/sample.json   | 3        | 96750      | 3        | 49291      |

(Extracted from `results/summary.csv`.)
```


Analysis

### Analysis
- SCC structure & impact: With SCC_count = 6 on n = 8, the graph is near-DAG; SCC detection is modest and the condensed DAG stays small, which aligns with the low Topo_time_ns.

- Shortest vs. Longest passes: SP_time_ns (96.8 µs) > LP_time_ns (49.3 µs) with identical relaxations (3). This difference is plausible due to traversal order and memory access; both remain linear over the topo order.

- Density sensitivity: Here m/n ≈ 0.88 (sparse). Denser DAGs typically increase relaxation counts and thus SP/LP_time_ns; sparser graphs keep them lower.

- Practical takeaway: When SCC_count ≈ n, SCC time is low and DP phases dominate. For inputs with larger SCCs, SCC time grows but the condensation shrinks the problem, often keeping SP/LP efficient overall.

## Conclusions

- Use SCC → condensation first; then run all DP on the DAG.

- For edge weights, SSSP/LP along topo order is optimal and simple.

- For node durations, use node-splitting or a dedicated pass (future work).

- Prefer batch runs + CSV on larger graphs to profile structure-dependent costs.

- Keep datasets & results versioned for reproducibility.
