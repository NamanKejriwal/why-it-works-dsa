import java.util.Arrays;
import java.util.Random;

/**
 * Why It Works #01: why two pointers is allowed to skip pairs.
 *
 * Run with:  java TwoPointers.java
 */
public class TwoPointers {

    /** A pair of indices (or -1, -1) and how many sums were actually computed. */
    record Result(int left, int right, int checks) {
        boolean found() {
            return left >= 0;
        }
    }

    /**
     * Finds i < j with a[i] + a[j] == target in a SORTED array.
     *
     * Invariant: if a valid pair exists, both of its indices are inside [left, right].
     *
     * Why each move is safe (this is where sorted order is used):
     *  - sum too big: a[right] is the largest value in range and a[left] the smallest, so a[right] plus
     *    any other partner in range is at least this sum, which is already too big. Nothing can pair
     *    with a[right]. Drop it.
     *  - sum too small: the mirror image. a[left] plus any partner in range is at most this sum.
     *    Nothing can pair with a[left]. Drop it.
     * Each drop rules out a whole row or column of pairs, not just one.
     */
    static Result twoSumSorted(int[] a, int target, boolean verbose) {
        int left = 0, right = a.length - 1, checks = 0;
        while (left < right) {
            int sum = a[left] + a[right];
            checks++;
            String equation = a[left] + " + " + a[right] + " = " + sum;
            if (sum == target) {
                if (verbose) System.out.printf("  %-12s found%n", equation);
                return new Result(left, right, checks);
            }
            if (sum > target) {
                if (verbose) System.out.printf("  %-12s too big    -> drop %d%n", equation, a[right]);
                right--;
            } else {
                if (verbose) System.out.printf("  %-12s too small  -> drop %d%n", equation, a[left]);
                left++;
            }
        }
        return new Result(-1, -1, checks);
    }

    /** Slow but obviously correct: tries every pair. Used as the reference. */
    static boolean pairExists(int[] a, int target) {
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] + a[j] == target) return true;
        return false;
    }

    /** The example from the post: 5 checks out of 15 pairs. */
    static boolean demoExample() {
        int[] a = {1, 3, 4, 6, 8, 11};
        int target = 10;
        System.out.println("Example: " + Arrays.toString(a) + ", target " + target);
        Result r = twoSumSorted(a, target, true);
        int pairs = a.length * (a.length - 1) / 2;
        int skipped = pairs - r.checks();
        System.out.printf("  checked %d, skipped %d, total %d pairs%n", r.checks(), skipped, pairs);
        boolean ok = r.found() && a[r.left()] + a[r.right()] == target && r.checks() == 5 && skipped == 10;
        System.out.println(ok ? "PASS example: 5 checked + 10 skipped = 15" : "FAIL example");
        return ok;
    }

    /** Same moves, unsorted input: they quietly miss a pair that exists. */
    static boolean demoUnsorted() {
        int[] a = {9, 7, 1, 2};
        int target = 8;
        Result r = twoSumSorted(a, target, true);
        boolean exists = pairExists(a, target);
        System.out.println("Unsorted: " + Arrays.toString(a) + ", target " + target);
        System.out.println("  two pointers found a pair: " + r.found() + ", but a pair exists (7 + 1): " + exists);
        boolean ok = !r.found() && exists;
        System.out.println(ok ? "PASS unsorted: two pointers misses the pair, as the post says" : "FAIL unsorted");
        return ok;
    }

    /** Compares two pointers with the brute force on random SORTED arrays (fixed seed, so reruns match). */
    static boolean randomTest() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] a = new int[rnd.nextInt(8)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(21) - 10;
            Arrays.sort(a);
            int target = rnd.nextInt(41) - 20;

            Result r = twoSumSorted(a, target, false);
            boolean expected = pairExists(a, target);
            boolean validPair = !r.found() || (r.left() < r.right() && a[r.left()] + a[r.right()] == target);
            boolean linear = r.checks() <= Math.max(0, a.length - 1);   // at most n - 1 checks

            if (r.found() != expected || !validPair || !linear) {
                System.out.printf("FAIL random: a=%s target=%d found=%b expected=%b checks=%d%n",
                        Arrays.toString(a), target, r.found(), expected, r.checks());
                return false;
            }
        }
        System.out.println("PASS random: " + runs + " random sorted arrays matched the brute force (seed 42)");
        return true;
    }

    public static void main(String[] args) {
        boolean ok = demoExample();
        System.out.println();
        ok &= demoUnsorted();
        System.out.println();
        ok &= randomTest();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
