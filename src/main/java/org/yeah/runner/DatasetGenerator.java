package org.yeah.runner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DatasetGenerator {

    static class In {
        boolean directed = true;
        int n;
        List<E> edges = new ArrayList<>();
        Integer source = 0;
        String weight_model = "edge";
    }
    static class E { int u,v,w; E(int u,int v,int w){this.u=u;this.v=v;this.w=w;} }

    public static void main(String[] args) throws Exception {
        Path out = Path.of("data");
        if (!Files.exists(out)) Files.createDirectories(out);

        genSmall1(out.resolve("small1.json"));
        genSmall2(out.resolve("small2.json"));
        genSmall3(out.resolve("small3.json"));

        genMedium1(out.resolve("medium1.json"));
        genMedium2(out.resolve("medium2.json"));
        genMedium3(out.resolve("medium3.json"));

        genLarge1(out.resolve("large1.json"));
        genLarge2(out.resolve("large2.json"));
        genLarge3(out.resolve("large3.json"));
    }

    static void write(Path p, In in) throws Exception {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(p.toFile())) {
            fw.write(g.toJson(in));
        }
    }

    static Random rnd(long seed){ return new Random(seed); }
    static void add(In in, int u,int v,int w){ in.edges.add(new E(u,v,w)); }

    static void dagChain(In in, int n, int baseW){
        in.n=n; in.source=0;
        for(int i=0;i<n-1;i++) add(in,i,i+1,baseW+(i%3)+1);
    }
    static void makeCycle(In in, int a,int b,int baseW){
        for(int i=a;i<b;i++) add(in,i,i+1,baseW+1);
        add(in,b,a,baseW+2);
    }
    static void sprinkle(In in, int edges, int maxW, long seed){
        Random r=rnd(seed);
        for(int k=0;k<edges;k++){
            int u=r.nextInt(in.n), v=r.nextInt(in.n);
            if(u==v) {k--;continue;}
            add(in,u,v,1+r.nextInt(maxW));
        }
    }

    static void genSmall1(Path p) throws Exception {
        In in=new In(); dagChain(in,8,2); add(in,0,2,3); add(in,2,5,2);
        write(p,in);
    }
    static void genSmall2(Path p) throws Exception {
        In in=new In(); in.n=9; in.source=0;
        makeCycle(in,0,2,1); makeCycle(in,3,4,2); add(in,2,3,3); add(in,4,5,2); add(in,5,6,2); add(in,6,7,2); add(in,7,8,2);
        write(p,in);
    }
    static void genSmall3(Path p) throws Exception {
        In in=new In(); dagChain(in,10,1); add(in,0,3,2); add(in,1,4,3); add(in,2,6,2); add(in,3,7,4); add(in,4,8,1);
        write(p,in);
    }

    static void genMedium1(Path p) throws Exception {
        In in=new In(); dagChain(in,12,2); add(in,1,5,2); add(in,2,6,3); add(in,6,9,2); add(in,3,8,4);
        write(p,in);
    }
    static void genMedium2(Path p) throws Exception {
        In in=new In(); in.n=15; in.source=0;
        makeCycle(in,0,3,1); makeCycle(in,6,8,1); add(in,3,5,2); add(in,5,6,2); add(in,8,10,2); add(in,10,12,2); add(in,12,14,2);
        sprinkle(in,8,5,42);
        write(p,in);
    }
    static void genMedium3(Path p) throws Exception {
        In in=new In(); in.n=18; in.source=0;
        makeCycle(in,0,2,1); makeCycle(in,3,5,1); makeCycle(in,9,11,1);
        add(in,2,3,2); add(in,5,6,2); add(in,6,9,3); add(in,11,13,2); add(in,13,15,2); add(in,15,17,2);
        sprinkle(in,10,6,99);
        write(p,in);
    }

    static void genLarge1(Path p) throws Exception {
        In in=new In(); in.n=25; in.source=0;
        dagChain(in,25,2); sprinkle(in,20,7,7);
        write(p,in);
    }
    static void genLarge2(Path p) throws Exception {
        In in=new In(); in.n=40; in.source=0;
        makeCycle(in,0,4,1); makeCycle(in,10,13,1); makeCycle(in,20,23,1); makeCycle(in,30,32,1);
        for(int i=0;i<39;i++) add(in,i,i+1,2+((i%3)));
        sprinkle(in,25,8,21);
        write(p,in);
    }
    static void genLarge3(Path p) throws Exception {
        In in=new In(); in.n=45; in.source=0;
        for(int i=0;i<44;i++){ add(in,i,i+1,1+(i%4)); if(i+2<45) add(in,i,i+2,2+((i%3))); }
        makeCycle(in,5,7,1); makeCycle(in,15,17,1); makeCycle(in,25,27,1); makeCycle(in,35,37,1);
        sprinkle(in,30,9,123);
        write(p,in);
    }
}
