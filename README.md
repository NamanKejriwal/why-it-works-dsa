# Why It Works

One DSA idea at a time, looked at through three questions: why does it work, when does it apply, and how does it break?

Each folder has a short write-up and one Java file. The Java file checks the technique against a brute-force (or simple reference) solution on 20,000 random inputs, and shows a case where using it wrongly gives the wrong answer.

| # | Idea | Folder |
|---|------|--------|
| 01 | Why two pointers is allowed to skip pairs | [01-two-pointers](01-two-pointers) |
| 02 | Sliding window, Kadane and prefix sums: what each one throws away | [02-subarray-techniques](02-subarray-techniques) |
| 03 | Binary search without a sorted array | [03-binary-search](03-binary-search) |
| 04 | Why balanced brackets needs a stack | [04-balanced-brackets](04-balanced-brackets) |
| 05 | Why reversing a linked list needs three pointers | [05-linked-list](05-linked-list) |
| Extra | Why next greater element needs a stack | [extra-next-greater-element](extra-next-greater-element) |

## Run one

Needs Java 17 or newer. No build step:

```bash
cd 01-two-pointers
java TwoPointers.java
```

Each program prints `ALL CHECKS PASSED` when every check holds.

The series is also posted on [LinkedIn](https://www.linkedin.com/in/naman-kejriwal11/).
