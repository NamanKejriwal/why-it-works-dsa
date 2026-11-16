import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Why It Works #4: next greater element, and why it has to be a stack.
 *
 * Walk left to right and keep the numbers still waiting for something bigger. Bottom to top they never go up, because a bigger
 * number would already have answered the smaller ones. So when a new number comes in, the newest waiting number is the first one
 * it can answer: last in, first out. That is a stack, not a trick.
 *
 * Run with:  java NextGreaterElement.java
 */
public class NextGreaterElement {

    /** Total pops in the last call, to show the while loop inside the for loop is O(n) overall. */
    static long pops;
    /** Set by the tests: times the waiting values went up from bottom to top. Must stay 0. */
    static long orderBroken;

    // ---------------------------------------------------------------- the stack version

    /** For each index, the first value to its right that is strictly bigger, or -1 if there is none. */
    static int[] nextGreater(int[] a, List<String> log) {
        int[] answer = new int[a.length];
        Arrays.fill(answer, -1);
        Deque<Integer> waiting = new ArrayDeque<>();          // indices still waiting for a bigger value
        pops = 0;
        for (int i = 0; i < a.length; i++) {
            while (!waiting.isEmpty() && a[i] > a[waiting.peek()]) {   // strict: an equal value is not bigger
                int j = waiting.pop();                        // the newest waiting number is answered first
                answer[j] = a[i];
                pops++;
                if (log != null) log.add(a[i] + " answers " + a[j]);
            }
            waiting.push(i);
            if (!neverGoesUp(a, waiting)) orderBroken++;
            if (log != null) log.add("waiting, bottom to top: " + bottomToTop(a, waiting));
        }
        return answer;
    }

    /** The same loop with >= . Equal values answer each other, which is "next greater or equal", not "next greater". */
    static int[] nextGreaterOrEqual(int[] a) {
        int[] answer = new int[a.length];
        Arrays.fill(answer, -1);
        Deque<Integer> waiting = new ArrayDeque<>();
        for (int i = 0; i < a.length; i++) {
            while (!waiting.isEmpty() && a[i] >= a[waiting.peek()]) answer[waiting.pop()] = a[i];
            waiting.push(i);
        }
        return answer;
    }

    /** The same loop with a queue, so the oldest waiting number is checked first. It stops at the first number it can't answer. */
    static int[] nextGreaterWithQueue(int[] a) {
        int[] answer = new int[a.length];
        Arrays.fill(answer, -1);
        Deque<Integer> waiting = new ArrayDeque<>();
        for (int i = 0; i < a.length; i++) {
            while (!waiting.isEmpty() && a[i] > a[waiting.peekLast()]) answer[waiting.pollLast()] = a[i];
            waiting.push(i);
        }
        return answer;
    }

    static int[] brute(int[] a) {
        int[] answer = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            answer[i] = -1;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] > a[i]) {
                    answer[i] = a[j];
                    break;
                }
            }
        }
        return answer;
    }

    static boolean neverGoesUp(int[] a, Deque<Integer> waiting) {
        Iterator<Integer> it = waiting.descendingIterator();   // bottom to top
        int prev = Integer.MAX_VALUE;
        while (it.hasNext()) {
            int v = a[it.next()];
            if (v > prev) return false;
            prev = v;
        }
        return true;
    }

    static String bottomToTop(int[] a, Deque<Integer> waiting) {
        List<Integer> vals = new ArrayList<>();
        waiting.descendingIterator().forEachRemaining(i -> vals.add(a[i]));
        return vals.toString();
    }

    // ---------------------------------------------------------------- checks

    /** The example from the post: 4 answers 1 first, then 3, and 5 keeps waiting. */
    static boolean demoWhyStack() {
        int[] a = {5, 3, 1, 4};
        List<String> log = new ArrayList<>();
        int[] got = nextGreater(a, log);
        System.out.println("Next greater element of " + Arrays.toString(a) + ":");
        log.forEach(s -> System.out.println("  " + s));
        System.out.println("  result: " + Arrays.toString(got));
        boolean ok = Arrays.equals(got, new int[] {-1, 4, 4, -1}) && log.indexOf("4 answers 1") >= 0
                && log.indexOf("4 answers 1") < log.indexOf("4 answers 3");
        System.out.println(ok ? "PASS example: 4 answers 1 first, then 3 (newest first), and 5 keeps waiting" : "FAIL example");
        return ok;
    }

    /** With a queue the oldest waiting number is checked first. 4 can't answer 5, so it stops, and 3 and 1 never get answered. */
    static boolean demoQueue() {
        int[] a = {5, 3, 1, 4};
        int[] queue = nextGreaterWithQueue(a);
        System.out.println("Same loop with a queue (oldest first): " + Arrays.toString(queue) + ", correct: " + Arrays.toString(brute(a)));
        boolean ok = Arrays.equals(queue, new int[] {-1, -1, -1, -1});
        System.out.println(ok ? "PASS queue: checking the oldest first gets it wrong" : "FAIL queue");
        return ok;
    }

    /** The other example from the post: with >= the second 2 answers the first 2. */
    static boolean demoEquals() {
        int[] a = {2, 2, 3};
        int[] strict = nextGreater(a, null), loose = nextGreaterOrEqual(a);
        System.out.println("On " + Arrays.toString(a) + ":  with >  " + Arrays.toString(strict) + "   with >=  " + Arrays.toString(loose));
        boolean ok = Arrays.equals(strict, new int[] {3, 3, -1}) && Arrays.equals(loose, new int[] {2, 3, -1});
        System.out.println(ok ? "PASS equal values: > gives [3, 3, -1], >= gives [2, 3, -1]" : "FAIL equal values");
        return ok;
    }

    static boolean randomChecks() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        int geWrong = 0, queueWrong = 0;
        orderBroken = 0;
        for (int run = 0; run < runs; run++) {
            int[] a = new int[rnd.nextInt(11)];                 // 0 to 10 elements
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(5);   // values 0 to 4, so duplicates are everywhere
            int[] expected = brute(a);
            if (!Arrays.equals(nextGreater(a, null), expected)) {
                System.out.println("FAIL stack version: " + Arrays.toString(a));
                return false;
            }
            if (pops > a.length) {
                System.out.println("FAIL more pops than elements: " + Arrays.toString(a));
                return false;
            }
            if (!Arrays.equals(nextGreaterOrEqual(a), expected)) geWrong++;
            if (!Arrays.equals(nextGreaterWithQueue(a), expected)) queueWrong++;
        }
        boolean ok = orderBroken == 0 && geWrong > 0 && queueWrong > 0;
        System.out.println("PASS stack version: matched a brute force on " + runs + " random arrays with lots of duplicates (seed 42),");
        System.out.println("  the waiting numbers never went up from bottom to top, and there were never more pops than elements");
        System.out.println("  for comparison, >= was wrong on " + geWrong + " of them and the queue version on " + queueWrong);
        return ok;
    }

    /** A while inside a for, and still O(n): 99,999 numbers going down, then one bigger number that answers all of them at once. */
    static boolean demoPops() {
        int n = 100_000;
        int[] a = new int[n];
        for (int i = 0; i < n - 1; i++) a[i] = n - i;             // 100000, 99999, ..., 2
        a[n - 1] = n + 1;                                          // one bigger number at the end
        nextGreater(Arrays.copyOf(a, n - 1), null);
        long before = pops;                                        // pops before the last number arrives
        nextGreater(a, null);
        System.out.printf("%,d elements (99,999 going down, then one bigger): %,d pops in total, %,d of them before the last step%n", n, pops, before);
        boolean ok = pops == n - 1 && before == 0;
        System.out.println(ok ? "PASS pops: never more than one pop per element, however they're spread out" : "FAIL pops");
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = demoWhyStack();
        System.out.println();
        ok &= demoQueue();
        System.out.println();
        ok &= demoEquals();
        System.out.println();
        ok &= randomChecks();
        System.out.println();
        ok &= demoPops();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
