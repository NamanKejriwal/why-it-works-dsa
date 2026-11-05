import java.util.Arrays;
import java.util.Random;
import java.util.function.IntPredicate;

/**
 * Why It Works #3: binary search doesn't need a sorted array. It needs a check that tells you which side to throw away,
 * and that check has to flip only once (no, no, ..., yes, yes) or the search returns a wrong answer without any warning.
 *
 *   find a peak in an unsorted array      the check a[mid] < a[mid + 1] says which side still holds a peak
 *   smallest amount payable with 3 and 5  the check "can I pay x?" flips back and forth, so binary search gets it wrong
 *   Koko Eating Bananas                   the check "can she finish at speed k?" flips once, so binary search is safe
 *
 * Run with:  java BinarySearchWithoutSorting.java
 */
public class BinarySearchWithoutSorting {

    // ---------------------------------------------------------------- peak in an unsorted array (LeetCode 162)

    /**
     * One step of the peak search on [lo, hi]. If a[mid] < a[mid + 1] the numbers climb to the right, and walking right they
     * either climb to the end of the array or drop somewhere, so a peak is on the right. Otherwise a peak is at mid or to its left.
     * Returns the new {lo, hi}. Assumes neighbours are never equal, as LeetCode 162 does.
     */
    static int[] peakStep(int[] a, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        if (a[mid] < a[mid + 1]) return new int[] {mid + 1, hi};
        return new int[] {lo, mid};
    }

    /** Index of some peak: an element bigger than its neighbours (the ends only have one neighbour). */
    static int findPeak(int[] a, boolean verbose) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int[] next = peakStep(a, lo, hi);
            if (verbose) {
                boolean right = next[0] == mid + 1;
                System.out.printf("  mid=%d  %d %s %d  ->  %s, range [%d, %d]%n", mid, a[mid], right ? "<" : ">", a[mid + 1],
                        right ? "go right" : "go left", next[0], next[1]);
            }
            lo = next[0];
            hi = next[1];
        }
        return lo;
    }

    static boolean isPeak(int[] a, int i) {
        return (i == 0 || a[i] > a[i - 1]) && (i == a.length - 1 || a[i] > a[i + 1]);
    }

    static boolean hasPeakIn(int[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++) if (isPeak(a, i)) return true;
        return false;
    }

    // ---------------------------------------------------------------- the textbook "first yes" search, for any check

    /** Smallest x in [lo, hi] with check(x) true, assuming the check goes no, no, ..., yes, yes. Returns hi if nothing is yes. */
    static int firstYes(int lo, int hi, IntPredicate check, boolean verbose) {
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            boolean yes = check.test(mid);
            if (verbose) System.out.printf("  mid=%d  %s  ->  %s%n", mid, yes ? "yes" : "no", yes ? "hi = " + mid : "lo = " + (mid + 1));
            if (yes) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    static int firstYesBrute(int lo, int hi, IntPredicate check) {
        for (int x = lo; x <= hi; x++) if (check.test(x)) return x;
        return hi;
    }

    /** True if the check goes no, no, ..., yes, yes over [lo, hi] and never back to no. */
    static boolean flipsOnce(int lo, int hi, IntPredicate check) {
        boolean seenYes = false;
        for (int x = lo; x <= hi; x++) {
            boolean yes = check.test(x);
            if (seenYes && !yes) return false;
            seenYes |= yes;
        }
        return true;
    }

    // ---------------------------------------------------------------- a check that flips back and forth: coins of 3 and 5

    /** Can you pay exactly x with coins worth 3 and 5, as many as you like? */
    static boolean canPay(int x) {
        for (int fives = 0; fives * 5 <= x; fives++) if ((x - fives * 5) % 3 == 0) return true;
        return false;
    }

    // ---------------------------------------------------------------- a check that flips once: Koko Eating Bananas (LeetCode 875)

    /** Can Koko eat every pile within h hours at k bananas per hour? Eating faster never hurts, so this flips once. */
    static boolean canFinish(int[] piles, int h, int k) {
        long hours = 0;
        for (int p : piles) hours += (p + k - 1) / k;
        return hours <= h;
    }

    // ---------------------------------------------------------------- checks

    static String yesNo(int lo, int hi, IntPredicate check) {
        StringBuilder sb = new StringBuilder();
        for (int x = lo; x <= hi; x++) sb.append(x).append(check.test(x) ? " yes  " : " no  ");
        return sb.toString().trim();
    }

    /** The example from the post: three checks land on 5, in an array that was never sorted. */
    static boolean demoPeak() {
        int[] a = {1, 5, 2, 8, 3, 9, 4};
        System.out.println("Find a peak in " + Arrays.toString(a) + ":");
        int i = findPeak(a, true);
        System.out.printf("  result: index %d, value %d, is a peak: %b%n", i, a[i], isPeak(a, i));
        boolean ok = i == 1 && a[i] == 5 && isPeak(a, i);
        System.out.println(ok ? "PASS peak example: three checks land on 5" : "FAIL peak example");
        return ok;
    }

    static int[] randomUnsorted(Random rnd) {
        int[] a = new int[1 + rnd.nextInt(12)];
        for (int i = 0; i < a.length; i++) {
            do a[i] = rnd.nextInt(20); while (i > 0 && a[i] == a[i - 1]);      // neighbours never equal
        }
        return a;
    }

    static boolean randomPeak() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] a = randomUnsorted(rnd);
            int lo = 0, hi = a.length - 1;
            while (lo < hi) {                                                    // the reason it works: a peak never leaves [lo, hi]
                int[] next = peakStep(a, lo, hi);
                lo = next[0];
                hi = next[1];
                if (!hasPeakIn(a, lo, hi)) {
                    System.out.println("FAIL peak left the range: " + Arrays.toString(a));
                    return false;
                }
            }
            if (!isPeak(a, findPeak(a, false))) {
                System.out.println("FAIL not a peak: " + Arrays.toString(a));
                return false;
            }
        }
        System.out.println("PASS peak search: on " + runs + " random unsorted arrays a peak stayed inside the range after every step,"
                + " and the result was always a peak (seed 42)");
        return true;
    }

    /** The example from the post: the check flips back and forth, and binary search returns 5 when the answer is 3. */
    static boolean demoCoins() {
        IntPredicate check = BinarySearchWithoutSorting::canPay;
        System.out.println("Can I pay x with coins of 3 and 5?  " + yesNo(1, 7, check));
        System.out.println("Binary search for the first yes in [1, 7]:");
        int bs = firstYes(1, 7, check, true);
        int real = firstYesBrute(1, 7, check);
        System.out.printf("  binary search says %d, the real answer is %d, the check flips only once: %b%n", bs, real, flipsOnce(1, 7, check));
        boolean ok = bs == 5 && real == 3 && !flipsOnce(1, 7, check);
        System.out.println(ok ? "PASS coins example: binary search says 5, the answer is 3" : "FAIL coins example");
        return ok;
    }

    static boolean coinsRanges() {
        IntPredicate check = BinarySearchWithoutSorting::canPay;
        int wrong = 0, total = 0;
        for (int hi = 3; hi <= 40; hi++) {
            total++;
            if (firstYes(1, hi, check, false) != firstYesBrute(1, hi, check)) wrong++;
        }
        System.out.println("PASS coins on other ranges: searching [1, hi] for hi = 3..40, binary search was wrong on " + wrong + " of " + total);
        return wrong > 0;
    }

    static boolean randomKoko() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] piles = new int[1 + rnd.nextInt(8)];
            for (int i = 0; i < piles.length; i++) piles[i] = 1 + rnd.nextInt(30);
            int h = piles.length + rnd.nextInt(11);                              // at least one hour per pile, so an answer exists
            int max = Arrays.stream(piles).max().getAsInt();
            IntPredicate check = k -> canFinish(piles, h, k);
            if (!flipsOnce(1, max, check)) {
                System.out.printf("FAIL Koko check flips more than once: %s h=%d%n", Arrays.toString(piles), h);
                return false;
            }
            if (firstYes(1, max, check, false) != firstYesBrute(1, max, check)) {
                System.out.printf("FAIL Koko: %s h=%d%n", Arrays.toString(piles), h);
                return false;
            }
        }
        System.out.println("PASS Koko: the check flipped only once and binary search matched a brute force on " + runs
                + " random inputs (seed 42)");
        return true;
    }

    public static void main(String[] args) {
        boolean ok = demoPeak();
        System.out.println();
        ok &= randomPeak();
        System.out.println();
        ok &= demoCoins();
        System.out.println();
        ok &= coinsRanges();
        System.out.println();
        ok &= randomKoko();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
