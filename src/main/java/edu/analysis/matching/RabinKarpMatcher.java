package edu.analysis.matching;

import java.util.ArrayList;
import java.util.List;

public class RabinKarpMatcher {
    private static final long BASE = 256;
    private static final long MOD = 1_000_000_007L;

    // Uses rolling hash; verifies character-by-character on hash match
    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Null input");
        List<Integer> indices = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        long comparisons = 0, collisions = 0;

        if (m == 0 || m > n) return new MatchResult(indices, comparisons, collisions);

        long highPow = 1, patHash = 0, winHash = 0;
        for (int i = 0; i < m - 1; i++) highPow = (highPow * BASE) % MOD;
        for (int i = 0; i < m; i++) {
            patHash = (BASE * patHash + pattern.charAt(i)) % MOD;
            winHash = (BASE * winHash + text.charAt(i)) % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patHash == winHash) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        collisions++;
                        break;
                    }
                    j++;
                }
                if (j == m) indices.add(i);
            }
            if (i < n - m) {
                long rem = (winHash - text.charAt(i) * highPow) % MOD;
                if (rem < 0) rem += MOD;
                winHash = (rem * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return new MatchResult(indices, comparisons, collisions);
    }
}