# 01: Why two pointers is allowed to skip pairs

Part of **Why It Works**: one DSA idea at a time, looked at through three questions. Why does it work, when does it apply, and how does it break?

**Problem:** [LeetCode 167, Two Sum II](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/). Given a sorted array, find two numbers that add up to a target.

For `[1, 3, 4, 6, 8, 11]` and target `10` there are 15 possible pairs. Two pointers computes 5 sums and skips the other 10.

## The idea

- **Why it works:** if `a[left] + a[right]` is too big, `a[right]` plus any partner still in range is at least that big, so nothing can pair with `a[right]`. Drop it. If the sum is too small, the same argument drops `a[left]`. Each drop rules out a whole row or column of pairs.
- **When it applies:** you can prove that one end can't be part of any answer, usually because the array is sorted.
- **How it breaks:** unsorted input. On `[9, 7, 1, 2]` with target `8`, the same moves report "no pair", but `7 + 1 = 8`.

## Files

| File | What it is |
|---|---|
| `TwoPointers.java` | The two-pointer solution, a brute-force reference, and the tests below |

## Run it

Needs Java 17 or newer. No build step:

```
java TwoPointers.java
```

It exits with code 0 if every check passes and 1 if any fails.

## What it checks

1. **The example:** 5 sums computed, 10 pairs skipped, 15 in total.
2. **The unsorted case:** two pointers misses a pair that exists.
3. **20,000 random sorted arrays** (fixed seed 42, so reruns are identical). The result must match the brute force, any returned pair must really sum to the target, and the number of sums computed must be at most n − 1.

## Output

```
Example: [1, 3, 4, 6, 8, 11], target 10
  1 + 11 = 12  too big    -> drop 11
  1 + 8 = 9    too small  -> drop 1
  3 + 8 = 11   too big    -> drop 8
  3 + 6 = 9    too small  -> drop 3
  4 + 6 = 10   found
  checked 5, skipped 10, total 15 pairs
PASS example: 5 checked + 10 skipped = 15

  9 + 2 = 11   too big    -> drop 2
  9 + 1 = 10   too big    -> drop 1
  9 + 7 = 16   too big    -> drop 7
Unsorted: [9, 7, 1, 2], target 8
  two pointers found a pair: false, but a pair exists (7 + 1): true
PASS unsorted: two pointers misses the pair, as the post says

PASS random: 20000 random sorted arrays matched the brute force (seed 42)

ALL CHECKS PASSED
```

## Limits

- The random test covers arrays of 0 to 7 values between −10 and 10. That's good at catching logic errors and duplicate-value cases, but it isn't a proof. The proof is the argument above.
- It returns 0-based indices, while LeetCode 167 expects 1-based.

LinkedIn post: https://lnkd.in/p/dxuDxP_P

