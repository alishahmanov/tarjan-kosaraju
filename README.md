# Assignment 4 — Smart City / Smart Campus Scheduling

Course: Design and Analysis of Algorithms
Student: Alihan (SE-2401)

## Overview

Tasks are modeled as a directed, edge-weighted graph:

Vertices = tasks

Edges = precedence constraints

Weights = edge costs/durations

Pipeline: SCC (Tarjan) → Condensation DAG → Topological Order (Kahn) → DAG Shortest Paths → Critical (Longest) Path.
Batch runner exports timings and counters to CSV.

## Implemented

Tarjan’s SCC (O(V+E))

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
tests
mvn clean test

single dataset
mvn -q exec:java -Dexec.args="data/sample.json"
or, if exec IDs are configured
mvn -q exec:java@app -Dexec.args="data/sample.json"

batch over folder → results/summary.csv
mvn -q exec:java -Dexec.mainClass=org.yeah.runner.BatchRunner -Dexec.args="data results"
or
mvn -q exec:java@batch -Dexec.args="data results"

## CSV Output (Batch)

Header:

Dataset,n,edges,SCC_count,SCC_time_ns,Topo_time_ns,SP_relax,SP_time_ns,LP_relax,LP_time_ns


Each row = one dataset’s metrics.

## Example (Single Run, abridged)
SCC count = 6
Topological order: [1,5,0,4,3,2]
Shortest dist from comp 5: ... comp 2 = 8
Critical path length = 8, path = [5,4,3,2]

## Complexity
Stage	Time	Space
Tarjan SCC	O(V+E)	O(V)
Condensation build	O(V+E)	O(V+E)
Topological sort (Kahn)	O(V+E)	O(V)
DAG SSSP	O(V+E)	O(V)
DAG Longest (Critical)	O(V+E)	O(V)
##License

MIT (or per course rules).
