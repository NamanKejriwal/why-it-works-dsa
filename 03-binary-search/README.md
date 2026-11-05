# 03: Binary search without a sorted array

Part of **Why It Works**, a series about why a solution works, when it applies and how it breaks.

Binary search doesn't really need a sorted array. It needs a check that tells you which side to throw away. A sorted array just happens to give you one (`a[mid] < target`).

The check also has to flip only once: no, no, ..., yes, yes. If it can flip back, the search still finishes and returns a wrong answer without any warning.

## Three checks

| Problem | Check | Flips only once? | Binary search |
|---|---|---|---|
| Find a peak in `[1, 5, 2, 8, 3, 9, 4]` (LeetCode 162) | `a[mid] < a[mid + 1]` tells you which side still holds a peak | not a yes/no search, but a peak never leaves the range | finds 5 in three checks |
| Smallest amount you can pay exactly with coins of 3 and 5 | "can I pay x?" | no: 1 to 7 goes no, no, yes, no, yes, yes, no | says 5, the answer is 3 |
| Koko Eating Bananas (LeetCode 875) | "can she finish at speed k?" | yes, eating faster never hurts | matches a brute force |

## Why the peak search works

If `a[mid] < a[mid + 1]`, the numbers climb to the right. Walking right, they either keep climbing until the end of the array or drop somewhere, and either way there is a peak on the right. Otherwise a peak is at `mid` or to its left by the same reasoning. So a peak never leaves `[lo, hi]`, the range shrinks every step, and when `lo == hi` that index is a peak.

It finds *a* peak, not the biggest one. In the example it returns 5, not 9, which is fine because the problem asks for any peak.

## Run it

Needs Java 17 or newer.

```
java BinarySearchWithoutSorting.java
```

It exits with code 1 if any check fails.

## What is checked

- The peak example from the post: three checks, and it lands on 5.
- On 20,000 random unsorted arrays (1 to 12 elements, neighbours never equal), a peak stays inside the range after every step and the result is always a peak. Seed 42.
- The coins example from the post: the check goes no, no, yes, no, yes, yes, no, binary search says 5, the real answer is 3.
- The coins check on other ranges: searching `[1, hi]` for `hi` from 3 to 40, binary search is wrong on 14 of 38.
- Koko on 20,000 random inputs: the check flips only once, and binary search matches a brute force. Seed 42.

Broken copies of the code were used to make sure these checks can fail: flipping the peak comparison, dropping `mid` from the left side, and an off-by-one in the yes/no search each fail a check, and keeping `mid` on the right side makes the search hang.

## Real output

```
Find a peak in [1, 5, 2, 8, 3, 9, 4]:
  mid=3  8 > 3  ->  go left, range [0, 3]
  mid=1  5 > 2  ->  go left, range [0, 1]
  mid=0  1 < 5  ->  go right, range [1, 1]
  result: index 1, value 5, is a peak: true
PASS peak example: three checks land on 5

PASS peak search: on 20000 random unsorted arrays a peak stayed inside the range after every step, and the result was always a peak (seed 42)

Can I pay x with coins of 3 and 5?  1 no  2 no  3 yes  4 no  5 yes  6 yes  7 no
Binary search for the first yes in [1, 7]:
  mid=4  no  ->  lo = 5
  mid=6  yes  ->  hi = 6
  mid=5  yes  ->  hi = 5
  binary search says 5, the real answer is 3, the check flips only once: false
PASS coins example: binary search says 5, the answer is 3

PASS coins on other ranges: searching [1, hi] for hi = 3..40, binary search was wrong on 14 of 38

PASS Koko: the check flipped only once and binary search matched a brute force on 20000 random inputs (seed 42)

ALL CHECKS PASSED
```

## Limits

- The peak search assumes neighbours are never equal, as LeetCode 162 does. With equal neighbours a "peak" may not exist in the strict sense.
- The ends count as peaks if they are bigger than their one neighbour, which is also LeetCode 162's rule.
- The tests use small inputs. They check the reasoning, they do not prove it.
- 14 of 38 depends on which ranges you search. It shows the wrong answer is not a one-off, not how often it would show up in your own code.

LinkedIn post: _add the link here once it's published_
