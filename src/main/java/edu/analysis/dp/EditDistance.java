package edu.analysis.dp;

public class EditDistance {
    // Recurrence: dp[i][j] = dp[i-1][j-1] if match, else 1 + min(insert, delete, replace)
    public static DPResult compute(String s1, String s2) {
        if (s1 == null || s2 == null) throw new IllegalArgumentException("Null input");
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];
        long ops = 0;

        for (int i = 0; i <= n; i++) dp[i][0] = i; // Base case: deletions
        for (int j = 0; j <= m; j++) dp[0][j] = j; // Base case: insertions

        for (int i = 1; i <= n; i++) {
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                ops++;
                if (c1 == s2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i][j - 1], dp[i - 1][j]));
            }
        }
        return new DPResult(dp[n][m], ops);
    }
}