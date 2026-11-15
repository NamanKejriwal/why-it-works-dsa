import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Why It Works #5: why reversing a linked list needs three pointers.
 *
 * At every step three nodes matter: the one behind you (the arrow should point there now), the one you are flipping, and the
 * one ahead. Once you flip, nothing else remembers the one ahead, so you have to save it first. prev, curr and next are just
 * names for those three.
 *
 * Run with:  java ReverseLinkedList.java
 */
public class ReverseLinkedList {

    static class Node {
        final int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    static Node build(int... values) {
        Node head = null, tail = null;
        for (int v : values) {
            Node n = new Node(v);
            if (head == null) head = n; else tail.next = n;
            tail = n;
        }
        return head;
    }

    /** The values from head onwards. Stops after 100 nodes, so a list that loops back on itself can still be printed. */
    static List<Integer> values(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node c = head; c != null && out.size() < 100; c = c.next) out.add(c.value);
        return out;
    }

    // ---------------------------------------------------------------- the two versions

    /** Flips first. Node 1 was the only thing that knew where node 2 was, so after the first flip the rest is unreachable. */
    static Node reverseFlipFirst(Node head) {
        Node prev = null, curr = head;
        while (curr != null) {
            curr.next = prev;          // the link to the rest of the list is gone here
            prev = curr;
            curr = curr.next;          // reads the null that was just written, so the loop ends
        }
        return prev;
    }

    /** Saves the one ahead before touching anything. */
    static Node reverse(Node head) {
        Node prev = null, curr = head;
        while (curr != null) {
            Node next = curr.next;     // the one ahead, saved before the flip
            curr.next = prev;          // flip
            prev = curr;               // move both forward
            curr = next;
        }
        return prev;
    }

    // ---------------------------------------------------------------- two pointers are not enough

    /**
     * Runs the loop body as some order of the three lines that only use prev and curr:
     * F = "curr.next = prev", P = "prev = curr", A = "curr = curr.next". Returns what you get back, or why it failed.
     */
    static String runOrder(String order, int... values) {
        Node prev = null, curr = build(values);
        try {
            for (int it = 0; curr != null; it++) {
                if (it == 100) return "never ends";
                for (char c : order.toCharArray()) {
                    if (c == 'F') curr.next = prev;
                    else if (c == 'P') prev = curr;
                    else curr = curr.next;
                }
            }
        } catch (NullPointerException e) {
            return "crashes (NullPointerException)";
        }
        List<Integer> got = values(prev);
        return got.size() == 100 ? "a list that loops forever" : got.toString();
    }

    // ---------------------------------------------------------------- checks

    /** The example from the post: flipping first turns [1, 2, 3, 4] into [1], and 2, 3, 4 are still linked to each other. */
    static boolean demoFlipFirst() {
        Node head = build(1, 2, 3, 4);
        Node two = head.next;
        List<Integer> got = values(reverseFlipFirst(head));
        System.out.println("Flip first on [1, 2, 3, 4]: " + got);
        System.out.println("  node 2 still exists and still links on: " + values(two) + ", but nothing points to it");
        boolean ok = got.equals(List.of(1)) && values(two).equals(List.of(2, 3, 4));
        System.out.println(ok ? "PASS flip first: you get [1], and 2, 3, 4 are cut off" : "FAIL flip first");
        return ok;
    }

    static boolean demoFixed() {
        List<Integer> got = values(reverse(build(1, 2, 3, 4)));
        System.out.println("Save next first on [1, 2, 3, 4]: " + got);
        boolean ok = got.equals(List.of(4, 3, 2, 1));
        System.out.println(ok ? "PASS save next first: [4, 3, 2, 1]" : "FAIL save next first");
        return ok;
    }

    static boolean demoAllOrders() {
        System.out.println("Every order of the three lines that only use prev and curr, on [1, 2, 3, 4]:");
        boolean anyWorked = false;
        for (String order : List.of("FPA", "FAP", "PFA", "PAF", "AFP", "APF")) {
            String result = runOrder(order, 1, 2, 3, 4);
            System.out.printf("  %s  ->  %s%n", describe(order), result);
            if (result.equals("[4, 3, 2, 1]")) anyWorked = true;
        }
        System.out.println(!anyWorked ? "PASS two pointers: none of the 6 orders reverses the list" : "FAIL two pointers");
        return !anyWorked;
    }

    static String describe(String order) {
        List<String> parts = new ArrayList<>();
        for (char c : order.toCharArray()) parts.add(c == 'F' ? "curr.next = prev" : c == 'P' ? "prev = curr" : "curr = curr.next");
        return String.format("%-52s", String.join("; ", parts));
    }

    static boolean randomChecks() {
        final int runs = 20_000;
        Random rnd = new Random(42);
        for (int run = 0; run < runs; run++) {
            int[] vals = new int[rnd.nextInt(11)];                 // 0 to 10 nodes, so the empty list and one node are covered
            for (int i = 0; i < vals.length; i++) vals[i] = rnd.nextInt(100);
            List<Integer> expected = new ArrayList<>();
            for (int v : vals) expected.add(v);
            Collections.reverse(expected);
            if (!values(reverse(build(vals))).equals(expected)) {
                System.out.println("FAIL reverse: " + Arrays.toString(vals));
                return false;
            }
            List<Integer> flipFirst = values(reverseFlipFirst(build(vals)));
            List<Integer> onlyHead = vals.length == 0 ? List.of() : List.of(vals[0]);
            if (!flipFirst.equals(onlyHead)) {
                System.out.println("FAIL flip first did something else: " + Arrays.toString(vals));
                return false;
            }
        }
        System.out.println("PASS reverse: matched a reversed copy on " + runs + " random lists of 0 to 10 nodes (seed 42),");
        System.out.println("  and flipping first always came back with just the first node");
        return true;
    }

    public static void main(String[] args) {
        boolean ok = demoFlipFirst();
        System.out.println();
        ok &= demoFixed();
        System.out.println();
        ok &= demoAllOrders();
        System.out.println();
        ok &= randomChecks();
        System.out.println();
        System.out.println(ok ? "ALL CHECKS PASSED" : "SOME CHECKS FAILED");
        if (!ok) System.exit(1);
    }
}
