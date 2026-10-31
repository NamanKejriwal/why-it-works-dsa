# 02: Sliding window, Kadane and prefix sums

Part of **Why It Works**: one DSA idea at a time, looked at through three questions. Why does it work, when does it apply, and how does it break?

All three go through a subarray problem in one pass, so they're easy to mix up. What separates them is what each one throws away.

## The idea

| | What it throws away | Safe when | Memory |
|---|---|---|---|
| **Sliding window** | the left number, once the sum is too big | there are no negatives | O(1) |
| **Kadane** | the running sum, once it goes negative | the question is the biggest sum | O(1) |
| **Prefix sums** | nothing, every running total goes in a `HashMap` | any numbers | O(n) |

The same array, `[2, -5, 3, 1]`, shows all three:

- **Subarrays that sum to 1:** the window gives 0, prefix sums give 2 (`[1]` and the whole array). The window dropped the 2 before the -5 arrived.
- **Biggest subarray sum:** Kadane gives 4 (`[3, 1]`). After the -5 its running sum is -3, so it drops it and restarts at 3.

Which one to use depends on two things: what is being asked, and whether the numbers can be negative.

| Question | Technique |
|---|---|
| Biggest sum, any numbers | Kadane |
| Exactly k, any numbers | Prefix sums |
| Longest under a limit, no negatives | Sliding window |

## Files

| File | What it is |
|---|---|
| `SubarrayTechniques.java` | All three techniques, a brute-force reference for each, and the tests below |

## Run it

Needs Java 17 or newer. No build step:

```
java SubarrayTechniques.java
```

It exits with code 0 if every check passes and 1 if any fails.

## What it checks

1. **The example above:** window 0, prefix sums 2, Kadane 4.
2. **An all-negative array:** Kadane returns the largest element.
3. **20,000 random arrays with negatives** (fixed seed 42): Kadane must match the brute force for the biggest sum.
4. **20,000 random arrays with negatives** (same seed): prefix sums must match the brute force for counting subarrays that sum to `k`. It also reports how often the window is wrong on the same arrays.
5. **20,000 random arrays with no negatives** (same seed): the window must match the brute force for the longest subarray with sum at most `k`.
6. **One array with a negative** (`[5, -4, 3]`, `k = 4`): the window returns 2, the answer is 3.

## Output

```
Array [2, -5, 3, 1]
Kadane, biggest subarray sum:
  2   running 2   best 2
  -5  running -3  best 2
  3   running 3   best 3   (dropped the negative total, restarted here)
  1   running 4   best 4
Subarrays that sum to 1: window 0, prefix sums 2, brute force 2
Biggest sum: Kadane 4, brute force 4
PASS example: window 0, prefix sums 2, Kadane 4

All negative: [-3, -1, -2], Kadane -1, brute force -1
PASS all-negative: Kadane returns the largest element

PASS kadane: matched the brute force on 20000 random arrays with negatives (seed 42)

PASS prefix sums: matched the brute force on 20000 random arrays with negatives (seed 42)
  for comparison, the window was wrong on 6136 of those 20000

PASS window: matched the brute force on 20000 random arrays with no negatives (seed 42)

Longest with sum <= 4 on [5, -4, 3]: window 2, brute force 3
PASS negative: the window misses the whole array (5 - 4 + 3 = 4)

ALL CHECKS PASSED
```

## Limits

- The random tests use arrays of up to 8 values with small numbers. They catch logic errors, but they aren't a proof. The reasoning above is the proof.
- Each technique is tested on the question it fits. For example, the window is tested on "longest with sum at most k", not on "count subarrays that sum to k", where it also breaks on zeros.
- Kadane here needs a non-empty array.
- `HashMap<Integer, Integer>` boxes every value. That's fine for clarity, not tuned for speed.

LinkedIn post: _add the link here once it's published_
