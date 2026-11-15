# 04: Why next greater element needs a stack

Part of **Why It Works**, a series about why a solution works, when it applies and how it breaks.

Next greater element: for each number, find the first number to its right that is bigger. Most of us learn "use a monotonic stack" as a rule. This folder is about why it has to be a stack.

## Why a stack

Walk left to right and keep the numbers that are still waiting for something bigger. From bottom to top they never go up, because a bigger number would already have answered the smaller ones.

So when a new number comes in, the newest waiting number is the smallest one, and it's the first one the new number can answer. On `[5, 3, 1, 4]`, the 4 answers 1 first, then 3, and stops at 5. Newest first is last in, first out, which is a stack.

A queue checks the oldest waiting number first. On the same input it looks at 5, can't answer it, and stops, so 3 and 1 never get their answer.

## Why the while inside the for is O(n)

Every index is pushed once and popped at most once, so the whole run has at most n pops, however they are spread out. On 100,000 numbers where 99,999 go down and the last one is bigger, there are 99,999 pops, and every one of them happens in the last step.

## How it breaks

Use `>=` instead of `>` and equal numbers answer each other. On `[2, 2, 3]` the second 2 pops the first, so the first 2 gets 2 instead of 3. `>=` is right for "next greater or equal", wrong for "next greater".

## Run it

Needs Java 17 or newer.

```
java NextGreaterElement.java
```

It exits with code 1 if any check fails.

## What is checked

- The example from the post: on `[5, 3, 1, 4]`, 4 answers 1 before 3, and the result is `[-1, 4, 4, -1]`.
- The queue version on the same input gives `[-1, -1, -1, -1]`.
- On `[2, 2, 3]`, `>` gives `[3, 3, -1]` and `>=` gives `[2, 3, -1]`.
- On 20,000 random arrays (0 to 10 numbers, values 0 to 4, so duplicates are everywhere) the stack version matches a brute force, the waiting numbers never go up from bottom to top, and there are never more pops than numbers. Seed 42.
- The pop count on 100,000 numbers.

Broken copies of the code were used to make sure these checks can fail: using `>=`, using a queue instead of a stack, not filling the answers with -1, and storing the wrong value each fail a check.

## Real output

```
Next greater element of [5, 3, 1, 4]:
  waiting, bottom to top: [5]
  waiting, bottom to top: [5, 3]
  waiting, bottom to top: [5, 3, 1]
  4 answers 1
  4 answers 3
  waiting, bottom to top: [5, 4]
  result: [-1, 4, 4, -1]
PASS example: 4 answers 1 first, then 3 (newest first), and 5 keeps waiting

Same loop with a queue (oldest first): [-1, -1, -1, -1], correct: [-1, 4, 4, -1]
PASS queue: checking the oldest first gets it wrong

On [2, 2, 3]:  with >  [3, 3, -1]   with >=  [2, 3, -1]
PASS equal values: > gives [3, 3, -1], >= gives [2, 3, -1]

PASS stack version: matched a brute force on 20000 random arrays with lots of duplicates (seed 42),
  the waiting numbers never went up from bottom to top, and there were never more pops than elements
  for comparison, >= was wrong on 12555 of them and the queue version on 10080

100,000 elements (99,999 going down, then one bigger): 99,999 pops in total, 0 of them before the last step
PASS pops: never more than one pop per element, however they're spread out

ALL CHECKS PASSED
```

## Limits

- The tests use small arrays. They check the reasoning, they don't prove it.
- -1 means "nothing bigger to the right". If -1 can be a real value in your input, use a different marker.

LinkedIn post: _add the link here once it's published_
