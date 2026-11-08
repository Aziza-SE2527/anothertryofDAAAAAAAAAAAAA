package edu.analysis.benchmark;

import edu.analysis.advanced.KasaiLCP;
import edu.analysis.dp.*;
import edu.analysis.matching.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class BenchmarkRunner {
    private static final String ALPHABET = "ABCD";
    private static final int RUNS = 5;
    private static final long SEED = 42L;

    public static void main(String[] args) throws Exception {
        Random rand = new Random(SEED);
        new File("plots").mkdirs();

        runDPBenchmarks(rand);
        runMatchingBenchmarks(rand);
        runKasaiBenchmarks(rand);
        System.out.println("Plots saved in: " + new File("plots").getAbsolutePath());
    }

    private static String randomString(Random rand, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(rand.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static void runDPBenchmarks(Random rand) throws Exception {
        System.out.println("### Part A: DP Benchmarks\n| Algorithm | Length | Avg Time (ms) | Operations |\n|---|---|---|---|");
        int[] sizes = {100, 500, 1000};
        double[][] times = new double[2][sizes.length];

        for (int idx = 0; idx < sizes.length; idx++) {
            int n = sizes[idx];
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
            times[0][idx] = (lcsTime / 5.0) / 1e6;
            times[1][idx] = (edTime / 5.0) / 1e6;
            System.out.printf("| LCS | %d | %.4f | %d |\n", n, times[0][idx], lcsOps);
            System.out.printf("| Edit Distance | %d | %.4f | %d |\n", n, times[1][idx], edOps);
        }
        System.out.println();
        saveChart("plots/dp_execution_time.png", "Execution Time vs Input Size (DP)",
                "String Length (N)", sizes, new String[]{"LCS", "Edit Distance"}, times);
    }

    private static void runMatchingBenchmarks(Random rand) throws Exception {
        System.out.println("### Part B: String Matching Benchmarks\n| Algorithm | Text Len | Pattern | Avg Time (ms) | Comparisons | Matches |\n|---|---|---|---|---|---|");
        int[] sizes = {10000, 50000, 100000};
        double[][] presentTimes = new double[3][sizes.length];
        int patLen = 20;

        for (int idx = 0; idx < sizes.length; idx++) {
            int n = sizes[idx];
            String text = randomString(rand, n);
            String presentPat = text.substring(n / 2, n / 2 + patLen);
            String absentPat = "ZZZZZZZZZZZZZZZZZZZZ";

            for (String type : new String[]{"Present", "Absent"}) {
                String pat = type.equals("Present") ? presentPat : absentPat;
                double tNaive = benchMatcher("Naive", text, pat, type);
                double tKmp = benchMatcher("KMP", text, pat, type);
                double tRk = benchMatcher("Rabin-Karp", text, pat, type);
                if (type.equals("Present")) {
                    presentTimes[0][idx] = tNaive;
                    presentTimes[1][idx] = tKmp;
                    presentTimes[2][idx] = tRk;
                }
            }
        }
        System.out.println();
        saveChart("plots/string_matching_time.png", "String Matching Execution Time (Present Pattern)",
                "Text Length (N)", sizes, new String[]{"Naive", "KMP", "Rabin-Karp"}, presentTimes);
    }

    private static double benchMatcher(String name, String text, String pat, String type) {
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
        double avgMs = (totalTime / 5.0) / 1e6;
        System.out.printf("| %s | %d | %s | %.4f | %d | %d |\n",
                name, text.length(), type, avgMs, comps, matches);
        return avgMs;
    }

    private static void runKasaiBenchmarks(Random rand) throws Exception {
        System.out.println("### Part C: Kasai LCP Benchmarks\n| Length | Avg Time (ms) | Comparisons |\n|---|---|---|");
        int[] sizes = {1000, 5000, 10000};
        double[][] times = new double[1][sizes.length];

        for (int idx = 0; idx < sizes.length; idx++) {
            int n = sizes[idx];
            String s = randomString(rand, n);
            int[] sa = KasaiLCP.buildSimpleSuffixArray(s);

            long totalTime = 0, comps = 0;
            for (int r = 0; r < RUNS; r++) {
                long t0 = System.nanoTime();
                KasaiLCP.LCPResult res = KasaiLCP.buildLCP(s, sa);
                totalTime += (System.nanoTime() - t0);
                comps = res.charComparisons;
            }
            times[0][idx] = (totalTime / 5.0) / 1e6;
            System.out.printf("| %d | %.4f | %d |\n", n, times[0][idx], comps);
        }
        saveChart("plots/kasai_execution_time.png", "Kasai's Algorithm Execution Time",
                "String Length (N)", sizes, new String[]{"Kasai LCP"}, times);
    }

    // Draws and saves a clean PNG line chart using standard Java2D
    private static void saveChart(String path, String title, String xLabel, int[] xVals, String[] series, double[][] yVals) throws Exception {
        int w = 800, h = 500, padL = 80, padR = 50, padT = 60, padB = 70;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        double maxY = 0.001;
        for (double[] arr : yVals) for (double v : arr) if (v > maxY) maxY = v * 1.15;

        int plotW = w - padL - padR, plotH = h - padT - padB;
        g.setColor(new Color(230, 235, 240));
        for (int i = 0; i <= 5; i++) {
            int y = padT + plotH - (i * plotH / 5);
            g.drawLine(padL, y, w - padR, y);
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.format("%.2f", maxY * i / 5), 25, y + 5);
            g.setColor(new Color(230, 235, 240));
        }

        g.setColor(Color.DARK_GRAY);
        g.drawRect(padL, padT, plotW, plotH);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(title, padL + 20, 35);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString(xLabel, padL + plotW / 2 - 40, h - 20);
        g.drawString("Time (ms)", 15, padT - 15);

        Color[] palette = {new Color(102, 153, 204), new Color(235, 130, 150), new Color(85, 168, 104)};
        for (int i = 0; i < xVals.length; i++) {
            int x = padL + (i * plotW / (xVals.length - 1));
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.valueOf(xVals[i]), x - 15, padT + plotH + 25);
        }

        g.setStroke(new BasicStroke(2.5f));
        for (int s = 0; s < series.length; s++) {
            g.setColor(palette[s % palette.length]);
            for (int i = 0; i < xVals.length - 1; i++) {
                int x1 = padL + (i * plotW / (xVals.length - 1));
                int y1 = padT + plotH - (int) ((yVals[s][i] / maxY) * plotH);
                int x2 = padL + ((i + 1) * plotW / (xVals.length - 1));
                int y2 = padT + plotH - (int) ((yVals[s][i + 1] / maxY) * plotH);
                g.drawLine(x1, y1, x2, y2);
                g.fillOval(x1 - 4, y1 - 4, 8, 8);
                if (i == xVals.length - 2) g.fillOval(x2 - 4, y2 - 4, 8, 8);
            }
            g.fillRect(w - padR - 130, padT + 15 + s * 22, 12, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawString(series[s], w - padR - 110, padT + 26 + s * 22);
        }
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}