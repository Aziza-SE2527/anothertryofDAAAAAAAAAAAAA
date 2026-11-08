package edu.analysis.matching;

import java.util.ArrayList;
import java.util.List;

public class KMPMatcher {
    // Uses LPS array to skip redundant comparisons
    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Null input");
        List<Integer> indices = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        long[] comparisons = {0};

        if (m == 0 || m > n) return new MatchResult(indices, 0);

        int[] lps = computeLPS(pattern, comparisons);
        int i = 0, j = 0;

        while (i < n) {
            comparisons[0]++;
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
                if (j == m) {
                    indices.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        return new MatchResult(indices, comparisons[0]);
    }

    // Builds Longest Proper Prefix which is also Suffix (LPS) table
    public static int[] computeLPS(String pattern, long[] comparisons) {
        int m = pattern.length(), len = 0, i = 1;
        int[] lps = new int[m];

        while (i < m) {
            comparisons[0]++;
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }
}