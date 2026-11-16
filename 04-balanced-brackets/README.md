# 04: Why balanced brackets needs a stack

Part of **Why It Works**, a series about why a solution works, when it applies and how it breaks.

Three counters say `([)]` is balanced. It isn't. This folder is about why a counter is enough for one bracket type, and why mixed brackets need a stack.

## One type: a counter is enough

With only `(` and `)`, keep one number: +1 for `(`, -1 for `)`. If it never drops below zero and ends at zero, the string is balanced. One number is enough because any `)` can close any `(`.

## Several types: counters lose the order

Give each type its own counter and `([)]` passes all three checks: each type opens once, closes once, nothing goes negative. But the `)` is closing the `(` while the `[` opened after it is still open.

The counters know how many of each are open. They don't know which one was opened last, and that's exactly the one that has to close next.

## The stack keeps the order

Push every opener. Every closer has to match whatever is on top. On `([)]` the stack holds `(` then `[`, the `)` arrives with `[` on top, and it fails right there.

Browsers have the same problem with HTML: the parser keeps what the spec calls a "stack of open elements", so it knows which tag a closing tag belongs to.

So a stack is the right tool when the order of what's still open matters. If only the count matters, a plain number does the job.

## Run it

Needs Java 17 or newer.

```
java BalancedBrackets.java
```

It exits with code 1 if any check fails.

## What is checked

- One counter is right on round brackets: a few examples, and 20,000 random strings of `(` and `)` against a brute force. Seed 42.
- The example from the post: three counters accept `([)]`, the stack rejects it when `)` meets `[` on top, and the brute force agrees with the stack.
- The stack matches a brute force on 20,000 random mixed-bracket strings. Three counters get some of them wrong.
- Every one of the 1,296 four-character bracket strings: the stack is always right, and the counters wrongly accept exactly 6, all with two types crossing like `([)]`.

The brute force keeps deleting `()`, `[]` and `{}` until nothing changes, and calls the string balanced if nothing is left.

Broken copies of the code were used to make sure these checks can fail: a stack that ignores the bracket type, a stack that forgets leftover openers, a stack with no empty check, and a counter that allows going negative each fail a check or crash.

## Real output

```
  one counter on (())  -> true  brute force -> true
  one counter on (()   -> false brute force -> false
  one counter on ())(  -> false brute force -> false
  one counter on ()()  -> true  brute force -> true
PASS one counter: right on round brackets

On ([)]: three counters say true, the stack says false, brute force says false
  push (
  push [
  ) arrives, top is [, no match
PASS ([)]: three counters accept it, the stack rejects it when ) meets [ on top

PASS one counter matched a brute force on 20000 random strings of ( and ) (seed 42)
PASS the stack matched a brute force on 20000 random mixed-bracket strings
  for comparison, three counters were wrong on 20 of them

All 1296 four-character bracket strings: three counters wrongly accept 6: [([)], ({)}, [(]), [{]}, {(}), {[}]]
PASS every one of them has two types crossing, like ([)]

ALL CHECKS PASSED
```

## Limits

- The random strings are short (up to 10 characters). They check the reasoning, they don't prove it.
- Characters other than brackets aren't handled.

LinkedIn post: _add the link here once it's published_
