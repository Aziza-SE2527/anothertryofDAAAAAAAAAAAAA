package edu.analysis.matching;

import java.util.ArrayList;
import java.util.List;

public class NaiveMatcher {
    // Slides pattern one by one; empty or oversized pattern returns empty list
    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Null input");
        List<Integer> indices = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        long comparisons = 0;

        if (m == 0 || m > n) return new MatchResult(indices, comparisons);

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
                j++;
            }
            if (j == m) indices.add(i);
        }
        return new MatchResult(indices, comparisons);
    }
}