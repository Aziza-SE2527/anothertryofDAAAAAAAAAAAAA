package edu.analysis.benchmark;

import edu.analysis.advanced.KasaiLCP;
import edu.analysis.dp.*;
import edu.analysis.matching.*;
import java.util.Random;

public class BenchmarkRunner {
    private static final String ALPHABET = "ABCD";
    private static final int RUNS = 5;
    private static final long SEED = 42L;

    public static void main(String[] args) {
        Random rand = new Random(SEED);
        runDPBenchmarks(rand);
        runMatchingBenchmarks(rand);
        runKasaiBenchmarks(rand);
    }

    private static String randomString(Random rand, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(rand.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static void runDPBenchmarks(Random rand) {
        System.out.println("### Part A: DP Benchmarks\n| Algorithm | Length | Avg Time (ms) | Operations |\n|---|---|---|---|");
        int[] sizes = {100, 500, 1000};
        for (int n : sizes) {
            String s1 = randomString(rand, n);
            String s2 = randomString(rand, n);

            long lcsTime = 0, edTime = 0, lcsOps = 0, edOps = 0;
            for (int r = 0; r < RUNS; r++) {
                long t0 = System.nanoTime();
                DPResult r1 = LCS.compute(s1, s2);
                lcsTime += (System.nanoTime() - t0);
                lcsOps = r1.operations;

                long t1 = System.nanoTime();
                DPResult r2 = EditDistance.compute(s1, s2);
                edTime += (System.nanoTime() - t1);
                edOps = r2.operations;
            }
            System.out.printf("| LCS | %d | %.4f | %d |\n", n, (lcsTime / 5.0) / 1e6, lcsOps);
            System.out.printf("| Edit Distance | %d | %.4f | %d |\n", n, (edTime / 5.0) / 1e6, edOps);
        }
        System.out.println();
    }

    private static void runMatchingBenchmarks(Random rand) {
        System.out.println("### Part B: String Matching Benchmarks\n| Algorithm | Text Len | Pattern | Avg Time (ms) | Comparisons | Matches |\n|---|---|---|---|---|---|");
        int[] sizes = {10000, 50000, 100000};
        int patLen = 20;

        for (int n : sizes) {
            String text = randomString(rand, n);
            String presentPat = text.substring(n / 2, n / 2 + patLen);
            String absentPat = "ZZZZZZZZZZZZZZZZZZZZ";

            for (String type : new String[]{"Present", "Absent"}) {
                String pat = type.equals("Present") ? presentPat : absentPat;
                benchMatcher("Naive", text, pat, type);
                benchMatcher("KMP", text, pat, type);
                benchMatcher("Rabin-Karp", text, pat, type);
            }
        }
        System.out.println();
    }

    private static void benchMatcher(String name, String text, String pat, String type) {
        long totalTime = 0, comps = 0;
        int matches = 0;
        for (int r = 0; r < RUNS; r++) {
            long t0 = System.nanoTime();
            MatchResult res;
            if ("Naive".equals(name)) res = NaiveMatcher.search(text, pat);
            else if ("KMP".equals(name)) res = KMPMatcher.search(text, pat);
            else res = RabinKarpMatcher.search(text, pat);
            totalTime += (System.nanoTime() - t0);
            comps = res.charComparisons;
            matches = res.indices.size();
        }
        System.out.printf("| %s | %d | %s | %.4f | %d | %d |\n",
                name, text.length(), type, (totalTime / 5.0) / 1e6, comps, matches);
    }

    private static void runKasaiBenchmarks(Random rand) {
        System.out.println("### Part C: Kasai LCP Benchmarks\n| Length | Avg Time (ms) | Comparisons |\n|---|---|---|");
        int[] sizes = {1000, 5000, 10000};
        for (int n : sizes) {
            String s = randomString(rand, n);
            int[] sa = KasaiLCP.buildSimpleSuffixArray(s);

            long totalTime = 0, comps = 0;
            for (int r = 0; r < RUNS; r++) {
                long t0 = System.nanoTime();
                KasaiLCP.LCPResult res = KasaiLCP.buildLCP(s, sa);
                totalTime += (System.nanoTime() - t0);
                comps = res.charComparisons;
            }
            System.out.printf("| %d | %.4f | %d |\n", n, (totalTime / 5.0) / 1e6, comps);
        }
    }
}