package edu.analysis.matching;

import java.util.List;

public class MatchResult {
    public final List<Integer> indices;
    public final long charComparisons;
    public final long hashCollisions;

    public MatchResult(List<Integer> indices, long charComparisons, long hashCollisions) {
        this.indices = indices;
        this.charComparisons = charComparisons;
        this.hashCollisions = hashCollisions;
    }

    public MatchResult(List<Integer> indices, long charComparisons) {
        this(indices, charComparisons, 0);
    }
}