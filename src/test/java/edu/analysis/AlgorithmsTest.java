package edu.analysis;

import edu.analysis.advanced.KasaiLCP;
import edu.analysis.dp.EditDistance;
import edu.analysis.dp.LCS;
import edu.analysis.matching.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmsTest {

    @Test
    void testLCS() {
        assertEquals(5, LCS.compute("HELLO", "HELLO").value);
        assertEquals(0, LCS.compute("ABC", "XYZ").value);
        assertEquals(0, LCS.compute("", "ABC").value);
        assertEquals(4, LCS.compute("AGGTAB", "GXTXAYB").value);
    }

    @Test
    void testEditDistance() {
        assertEquals(0, EditDistance.compute("SAME", "SAME").value);
        assertEquals(4, EditDistance.compute("", "TEST").value);
        assertEquals(1, EditDistance.compute("CAT", "CUT").value);
        assertEquals(1, EditDistance.compute("CAT", "CATS").value);
        assertEquals(1, EditDistance.compute("CATS", "CAT").value);
        assertEquals(3, EditDistance.compute("KITTEN", "SITTING").value);
    }

    private void assertAllMatchersEqual(String text, String pattern, List<Integer> expected) {
        List<Integer> naive = NaiveMatcher.search(text, pattern).indices;
        List<Integer> kmp = KMPMatcher.search(text, pattern).indices;
        List<Integer> rk = RabinKarpMatcher.search(text, pattern).indices;

        assertEquals(expected, naive);
        assertEquals(naive, kmp);
        assertEquals(naive, rk);
    }

    @Test
    void testStringMatching() {
        assertAllMatchersEqual("ABCDEFG", "ABC", Collections.singletonList(0));
        assertAllMatchersEqual("ABCDEFG", "CDE", Collections.singletonList(2));
        assertAllMatchersEqual("ABCDEFG", "EFG", Collections.singletonList(4));
        assertAllMatchersEqual("ABABABA", "ABA", Arrays.asList(0, 2, 4));
        assertAllMatchersEqual("ABCDEFG", "XYZ", Collections.emptyList());
        assertAllMatchersEqual("SHORT", "LONGERPATTERN", Collections.emptyList());
        assertAllMatchersEqual("ABCDEFG", "", Collections.emptyList());
    }

    @Test
    void testKasaiManual() {
        String s = "banana";
        int[] sa = {5, 3, 1, 0, 4, 2};
        int[] expectedLcp = {1, 3, 0, 0, 2, 0};

        assertArrayEquals(sa, KasaiLCP.buildSimpleSuffixArray(s));
        assertArrayEquals(expectedLcp, KasaiLCP.buildLCP(s, sa).lcp);
    }
}