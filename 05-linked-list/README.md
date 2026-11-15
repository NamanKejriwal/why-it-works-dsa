# 05: Why reversing a linked list needs three pointers

Part of **Why It Works**, a series about why a solution works, when it applies and how it breaks.

Everyone reverses a linked list with `prev`, `curr` and `next`. This folder is about why it takes three.

## The bug

```java
while (curr != null) {
    curr.next = prev;
    prev = curr;
    curr = curr.next;
}
```

It compiles, runs without an error, and turns `[1, 2, 3, 4]` into `[1]`. Node 1 was the only thing that knew where node 2 was. The first line points 1 back at `prev`, so that link is gone, and `curr = curr.next` reads the `null` that was just written. Nodes 2, 3 and 4 are still in memory and still linked to each other, but nothing points to them anymore, so for Java they are garbage.

## Why three

At every step three nodes matter: the one behind you (the arrow should point there now), the one you are flipping, and the one ahead. Once you flip, nothing else remembers the one ahead. `prev`, `curr` and `next` are just names for those three, so you save the one ahead first:

```java
while (curr != null) {
    Node next = curr.next;   // the one ahead, saved before the flip
    curr.next = prev;        // flip
    prev = curr;             // move both forward
    curr = next;
}
```

With only `prev` and `curr`, no order of the three lines works. All 6 orders were tried on `[1, 2, 3, 4]`: they return `[1]` or `[]`, never end, or crash.

A good question before any pointer change: after this line, can I still reach every node I need?

## Run it

Needs Java 17 or newer.

```
java ReverseLinkedList.java
```

It exits with code 1 if any check fails.

## What is checked

- The example from the post: flipping first on `[1, 2, 3, 4]` gives `[1]`, and node 2 still links on to `[2, 3, 4]`.
- Saving `next` first gives `[4, 3, 2, 1]`.
- All 6 orders of the three lines that only use `prev` and `curr`: none of them reverses the list.
- On 20,000 random lists of 0 to 10 nodes (seed 42), the fixed version matches a reversed copy, and flipping first always returns just the first node.

Broken copies of the code were used to make sure these checks can fail: reading `curr.next` after the flip, returning `curr` instead of `prev`, forgetting `prev = curr`, and advancing along the flipped link each fail a check.

## Real output

```
Flip first on [1, 2, 3, 4]: [1]
  node 2 still exists and still links on: [2, 3, 4], but nothing points to it
PASS flip first: you get [1], and 2, 3, 4 are cut off

Save next first on [1, 2, 3, 4]: [4, 3, 2, 1]
PASS save next first: [4, 3, 2, 1]

Every order of the three lines that only use prev and curr, on [1, 2, 3, 4]:
  curr.next = prev; prev = curr; curr = curr.next       ->  [1]
  curr.next = prev; curr = curr.next; prev = curr       ->  []
  prev = curr; curr.next = prev; curr = curr.next       ->  never ends
  prev = curr; curr = curr.next; curr.next = prev       ->  never ends
  curr = curr.next; curr.next = prev; prev = curr       ->  crashes (NullPointerException)
  curr = curr.next; prev = curr; curr.next = prev       ->  never ends
PASS two pointers: none of the 6 orders reverses the list

PASS reverse: matched a reversed copy on 20000 random lists of 0 to 10 nodes (seed 42),
  and flipping first always came back with just the first node

ALL CHECKS PASSED
```

## Limits

- The tests use short lists. They check the reasoning, they don't prove it.
- "Never ends" means the 100-step cap in the test was hit, which a program can't otherwise detect.

LinkedIn post: _add the link here once it's published_
