import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Why It Works #2: sliding window, Kadane and prefix sums. What is each one allowed to forget?
 *
 * Three subarray questions, three techniques:
 *   biggest sum                          -> Kadane          (any numbers)
 *   count subarrays that sum to k        -> prefix sums     (any numbers)
 *   longest subarray with sum <= k       -> sliding window  (no negatives)
 *
 * Run with:  java SubarrayTechniques.java
 */
public class SubarrayTechniques {

    // ---------------------------------------------------------------- biggest sum

    /**
     * Kadane: best sum of a subarray that ends at i is max(a[i], best ending at i-1 + a[i]).
     * Forgets the running total once it goes negative: attaching a negative total to anything
     * can only make it smaller, so starting fresh is at least as good. Needs a non-empty array.
     */
    static int kadane(int[] a, boolean verbose) {
        int running = a[0], best = a[0];
        if (verbose) System.out.printf("  %-3d running %-3d best %d%n", a[0], running, best);
        for (int i = 1; i < a.length; i++) {
            boolean restart = running < 0;                 // the old total can only hurt
            running = Math.max(a[i], running + a[i]);
            best = Math.max(best, running);
            if (verbose) System.out.printf("  %-3d running %-3d best %d%s%n", a[i], running, best,
                    restart ? "   (dropped the negative total, restarted here)" : "");
        }
        return best;
    }

    static int biggestSumBrute(int[] a) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- count subarrays that sum to k

    /**
     * Prefix sums: if prefix[j] - prefix[i] == k, the subarray between them sums to k. Remembers every running
     * total in a HashMap, forgets nothing, so any numbers are fine. O(n) time, O(n) memory.
     */
    static int countWithPrefixSums(int[] a, int k) {
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1);                       // the empty prefix, so subarrays starting at index 0 count
        int prefix = 0, count = 0;
        for (int x : a) {
            prefix += x;
            count += seen.getOrDefault(prefix - k, 0);
            seen.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    /** The usual window for the same question. Wrong as soon as a negative can appear. */
    static int countWithWindow(int[] a, int k) {
        int count = 0, sum = 0, left = 0;
        for (int right = 0; right < a.length; right++) {
            sum += a[right];
            while (sum > k && left <= right) sum -= a[left++];
            if (sum == k && left <= right) count++;
        }
        return count;
    }

    static int countBrute(int[] a, int k) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                if (sum == k) count++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------- longest subarray with sum <= k

    /**
     * Sliding window: extend on the right, and while the sum is over k, drop the left number.
     * Forgets the left number. Safe only with no negatives: then adding on the right can only push the sum up,
     * so once [left..right] is over k, every longer window starting at left is over k too.
     */
    static int longestAtMostWithWindow(int[] a, int k) {
        int best = 0, sum = 0, left = 0;
        for (int right = 0; right < a.length; right++) {
            sum += a[right];
            while (sum > k && left <= right) sum -= a[left++];
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    static int longestAtMostBrute(int[] a, int k) {
        int best = 0;
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                if (sum <= k) best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- checks

    /** The example from the post: one array, three techniques. */
    static boolean demoExample() {
        int[] a = {2, -5, 3, 1};
        System.out.println("Array " + Arrays.toString(a));
        System.out.println("Kadane, biggest subarray sum:");
        int kadane = kadane(a, true);
        int window = countWithWindow(a, 1);
        int prefix = countWithPrefixSums(a, 1);
        int brute = countBrute(a, 1);
        System.out.printf("Subarrays that sum to 1: window %d, prefix sums %d, brute force %d%n", window, prefix, brute);
        System.out.printf("Biggest sum: Kadane %d, brute force %d%n", kadane, biggestSumBrute(a));
        boolean ok = window == 0 && prefix == 2 && brute == 2 && kadane == 4 && biggestSumBrute(a) == 4;
        System.out.println(ok ? "PASS example: window 0, prefix sums 2, Kadane 4" : "FAIL example");
        return ok;
    }

    /** All-negative arrays: the best subarray is the single largest element. */
    static boolean demoAllNegative() {
        int[] a = {-3, -1, -2};
        int got = kadane(a, false), expected = biggestSumBrute(a);
        System.out.printf("All negative: %s, Kadane %d, brute force %d%n", Arrays.toString(a), got, expected);
        boolean ok = got == -1 && expected == -1;
        System.out.println(ok ? "PASS all-negative: Kadane returns the largest element" : "FAIL all-negative");
        return ok;
    }

    static int[] randomArray(Random rnd, int minLen, int maxLen, int lo, int hi) {
        int[] a = new int[minLen + rnd.nextInt(maxLen - minLen + 1)];
        for (int i = 0; i < a.length; i++) a[i] = lo + rnd.nextInt(hi - lo + 1);
        return a;
    }

    static boolean randomKadane() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] a = randomArray(rnd, 1, 8, -6, 6);
            if (kadane(a, false) != biggestSumBrute(a)) {
                System.out.println("FAIL kadane: " + Arrays.toString(a));
                return false;
            }
        }
        System.out.println("PASS kadane: matched the brute force on " + runs + " random arrays with negatives (seed 42)");
        return true;
    }

    static boolean randomPrefixSums() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        int windowWrong = 0;
        for (int run = 0; run < runs; run++) {
            int[] a = randomArray(rnd, 0, 8, -5, 5);
            int k = rnd.nextInt(13) - 6;
            int expected = countBrute(a, k);
            if (countWithPrefixSums(a, k) != expected) {
                System.out.printf("FAIL prefix sums: %s k=%d%n", Arrays.toString(a), k);
                return false;
            }
            if (countWithWindow(a, k) != expected) windowWrong++;
        }
        System.out.println("PASS prefix sums: matched the brute force on " + runs + " random arrays with negatives (seed 42)");
        System.out.println("  for comparison, the window was wrong on " + windowWrong + " of those " + runs);
        return windowWrong > 0;
    }

    static boolean randomWindowNoNegatives() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] a = randomArray(rnd, 0, 9, 0, 5);
            int k = rnd.nextInt(26);
            if (longestAtMostWithWindow(a, k) != longestAtMostBrute(a, k)) {
                System.out.printf("FAIL window: %s k=%d%n", Arrays.toString(a), k);
                return false;
            }
        }
        System.out.println("PASS window: matched the brute force on " + runs + " random arrays with no negatives (seed 42)");
        return true;
    }

    /** Same window, same question, but with a negative: it gives up too early. */
    static boolean demoWindowWithNegative() {
        int[] a = {5, -4, 3};
        int k = 4;
        int window = longestAtMostWithWindow(a, k), brute = longestAtMostBrute(a, k);
        System.out.printf("Longest with sum <= %d on %s: window %d, brute force %d%n", k, Arrays.toString(a), window, brute);
        boolean ok = window == 2 && brute == 3;
        System.out.println(ok ? "PASS negative: the window misses the whole array (5 - 4 + 3 = 4)" : "FAIL negative");
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = demoExample();
        System.out.println();
        ok &= demoAllNegative();
        System.out.println();
        ok &= randomKadane();
        System.out.println();
        ok &= randomPrefixSums();
        System.out.println();
        ok &= randomWindowNoNegatives();
        System.out.println();
        ok &= demoWindowWithNegative();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
