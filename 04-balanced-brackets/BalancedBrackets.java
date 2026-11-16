import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Why It Works #4: why balanced brackets needs a stack, and when a counter is enough.
 *
 * With one bracket type, one number is enough: any ) can close any (. With several types, counters only know how many of each
 * are open. They lose the order, and the bracket opened last is exactly the one that has to close next. A stack keeps that order.
 *
 * Run with:  java BalancedBrackets.java
 */
public class BalancedBrackets {

    static final String OPEN = "([{", CLOSE = ")]}";

    // ---------------------------------------------------------------- one counter: fine for round brackets only

    /** +1 for (, -1 for ). Balanced if it never drops below zero and ends at zero. */
    static boolean oneCounter(String s) {
        int open = 0;
        for (char c : s.toCharArray()) {
            open += c == '(' ? 1 : -1;
            if (open < 0) return false;
        }
        return open == 0;
    }

    // ---------------------------------------------------------------- three counters: they lose the order

    /** One counter per bracket type. Knows how many of each are open, not which one was opened last. */
    static boolean threeCounters(String s) {
        int[] open = new int[3];
        for (char c : s.toCharArray()) {
            int o = OPEN.indexOf(c), k = CLOSE.indexOf(c);
            if (o >= 0) open[o]++;
            else if (--open[k] < 0) return false;
        }
        return open[0] == 0 && open[1] == 0 && open[2] == 0;
    }

    // ---------------------------------------------------------------- the stack: keeps the order

    /** Push every opener. Every closer has to match whatever is on top, the bracket that was opened last. */
    static boolean stack(String s, List<String> log) {
        Deque<Character> open = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (OPEN.indexOf(c) >= 0) {
                open.push(c);
                if (log != null) log.add("push " + c);
                continue;
            }
            if (open.isEmpty()) {
                if (log != null) log.add(c + " arrives with nothing open");
                return false;
            }
            char top = open.pop();
            if (OPEN.indexOf(top) != CLOSE.indexOf(c)) {
                if (log != null) log.add(c + " arrives, top is " + top + ", no match");
                return false;
            }
            if (log != null) log.add(c + " closes " + top);
        }
        return open.isEmpty();
    }

    /** Brute force: keep deleting (), [] and {} until nothing changes. Balanced if nothing is left. */
    static boolean brute(String s) {
        String prev;
        do {
            prev = s;
            s = s.replace("()", "").replace("[]", "").replace("{}", "");
        } while (!s.equals(prev));
        return s.isEmpty();
    }

    // ---------------------------------------------------------------- checks

    static boolean demoOneCounter() {
        String[] cases = {"(())", "(()", "())(", "()()"};
        boolean ok = true;
        for (String s : cases) {
            System.out.printf("  one counter on %-5s -> %-5b brute force -> %b%n", s, oneCounter(s), brute(s));
            ok &= oneCounter(s) == brute(s);
        }
        System.out.println(ok ? "PASS one counter: right on round brackets" : "FAIL one counter");
        return ok;
    }

    /** The example from the post. */
    static boolean demoMixed() {
        String s = "([)]";
        List<String> log = new ArrayList<>();
        boolean counters = threeCounters(s), st = stack(s, log), br = brute(s);
        System.out.println("On " + s + ": three counters say " + counters + ", the stack says " + st + ", brute force says " + br);
        log.forEach(line -> System.out.println("  " + line));
        boolean ok = counters && !st && !br && log.get(log.size() - 1).equals(") arrives, top is [, no match");
        System.out.println(ok ? "PASS ([)]: three counters accept it, the stack rejects it when ) meets [ on top" : "FAIL ([)]");
        return ok;
    }

    static String randomString(Random rnd, String symbols, int maxLen) {
        StringBuilder b = new StringBuilder();
        int n = rnd.nextInt(maxLen + 1);
        for (int i = 0; i < n; i++) b.append(symbols.charAt(rnd.nextInt(symbols.length())));
        return b.toString();
    }

    static boolean randomChecks() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int r = 0; r < runs; r++) {
            String s = randomString(rnd, "()", 10);
            if (oneCounter(s) != brute(s)) {
                System.out.println("FAIL one counter: " + s);
                return false;
            }
        }
        int countersWrong = 0;
        for (int r = 0; r < runs; r++) {
            String s = randomString(rnd, "()[]{}", 10);
            if (stack(s, null) != brute(s)) {
                System.out.println("FAIL stack: " + s);
                return false;
            }
            if (threeCounters(s) != brute(s)) countersWrong++;
        }
        System.out.println("PASS one counter matched a brute force on " + runs + " random strings of ( and ) (seed 42)");
        System.out.println("PASS the stack matched a brute force on " + runs + " random mixed-bracket strings");
        System.out.println("  for comparison, three counters were wrong on " + countersWrong + " of them");
        return countersWrong > 0;
    }

    /** Every 4-character string of brackets: which ones do the counters wrongly accept? */
    static boolean allLengthFour() {
        String sym = "()[]{}";
        List<String> wrong = new ArrayList<>();
        int total = 0;
        for (int a = 0; a < 6; a++)
            for (int b = 0; b < 6; b++)
                for (int c = 0; c < 6; c++)
                    for (int d = 0; d < 6; d++) {
                        String s = "" + sym.charAt(a) + sym.charAt(b) + sym.charAt(c) + sym.charAt(d);
                        total++;
                        if (stack(s, null) != brute(s)) return false;
                        if (threeCounters(s) && !brute(s)) wrong.add(s);
                    }
        System.out.println("All " + total + " four-character bracket strings: three counters wrongly accept " + wrong.size() + ": " + wrong);
        boolean ok = wrong.size() == 6 && wrong.contains("([)]");
        System.out.println(ok ? "PASS every one of them has two types crossing, like ([)]" : "FAIL length four");
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = demoOneCounter();
        System.out.println();
        ok &= demoMixed();
        System.out.println();
        ok &= randomChecks();
        System.out.println();
        ok &= allLengthFour();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
