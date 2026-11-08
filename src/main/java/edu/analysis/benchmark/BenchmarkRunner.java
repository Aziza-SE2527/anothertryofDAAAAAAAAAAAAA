package edu.analysis;

import edu.analysis.advanced.KasaiLCP;
import edu.analysis.dp.EditDistance;
import edu.analysis.dp.LCS;
import edu.analysis.matching.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmsTest {

    @Test
    void testLCS() {
        assertEquals(5, LCS.compute("HELLO", "HELLO").value()); // Identical
        assertEquals(0, LCS.compute("ABC", "XYZ").value());     // Completely different
        assertEquals(0, LCS.compute("", "ABC").value());        // Empty string
        assertEquals(4, LCS.compute("AGGTAB", "GXTXAYB").value());
    }

    @Test
    void testEditDistance() {
        assertEquals(0, EditDistance.compute("SAME", "SAME").value()); // Identical
        assertEquals(4, EditDistance.compute("", "TEST").value());     // Empty string
        assertEquals(1, EditDistance.compute("CAT", "CUT").value());   // Substitution
        assertEquals(1, EditDistance.compute("CAT", "CATS").value());  // Insertion
        assertEquals(1, EditDistance.compute("CATS", "CAT").value());  // Deletion
        assertEquals(3, EditDistance.compute("KITTEN", "SITTING").value());
    }

    private void assertAllMatchersEqual(String text, String pattern, List<Integer> expected) {
        List<Integer> naive = NaiveMatcher.search(text, pattern).indices();
        List<Integer> kmp = KMPMatcher.search(text, pattern).indices();
        List<Integer> rk = RabinKarpMatcher.search(text, pattern).indices();

        assertEquals(expected, naive);
        assertEquals(naive, kmp);
        assertEquals(naive, rk);
    }

    @Test
    void testStringMatching() {
        assertAllMatchersEqual("ABCDEFG", "ABC", List.of(0));          // Beginning
        assertAllMatchersEqual("ABCDEFG", "CDE", List.of(2));          // Middle
        assertAllMatchersEqual("ABCDEFG", "EFG", List.of(4));          // End
        assertAllMatchersEqual("ABABABA", "ABA", List.of(0, 2, 4));    // Multiple overlapping
        assertAllMatchersEqual("ABCDEFG", "XYZ", List.of());           // Absent
        assertAllMatchersEqual("SHORT", "LONGERPATTERN", List.of());   // Pattern > text
        assertAllMatchersEqual("ABCDEFG", "", List.of());              // Empty pattern
    }

    @Test
    void testKasaiManual() {
        // Manual test on "banana"
        // Sorted suffixes: "a"(5), "ana"(3), "anana"(1), "banana"(0), "na"(4), "nana"(2)
        String s = "banana";
        int[] sa = {5, 3, 1, 0, 4, 2};
        int[] expectedLcp = {1, 3, 0, 0, 2, 0};

        assertArrayEquals(sa, KasaiLCP.buildSimpleSuffixArray(s));
        assertArrayEquals(expectedLcp, KasaiLCP.buildLCP(s, sa).lcp());
    }
}