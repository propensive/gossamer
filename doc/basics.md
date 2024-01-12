_Gossamer_ provides a collection of useful methods and constructors for working with strings.

All Gossamer terms and types are defined in the `gossamer` package:
```scala
import gossamer.*
```

### `Show` typeclass

A standard `Show` typeclass is provided which will convert values of different types into `String`s.

Many types, such as `Int`s, only have a single reasonable presentation as a `String`, while others,
for example instances of case classes, may be presented in different ways depending on the context.
Gossamer's `Show` typeclass does not prescribe exactly where and when it should be used, but
instances of `Show` should produce strings which meaningfully present a value as a string, usually
for human consumption.

Using [Wisteria](https://github.com/propensive/wisteria), `Show` instances for product types (such
as case classes and tuples) and coproduct types (such as enumerations and sealed traits) will be
automatically derived.

### `Text`, a typesafe `String`

The `Text` type in `anticipation` is provided as an opaque alias of `String`,
duplicating most of the functionality of `String` (and its associated extension
methods), but without the typesafety risks associated with `String`. `Text`
instances may only be combined with other types when a `Show` typeclass
instance exists for that type.

Furthermore, every method of `Text` is guaranteed not to be `null` and declares any exceptions it
may throw.

#### Interpolators

Scala's standard library provides the `s` interpolator which allows elements of any type to be
substituted into a `String`. This presents a typesafety hole, since `toString` must be applied to
each one, without any guarantee that it produces a reasonable presentation of that value as a
`String`.

So Gossamer introduces the `str""` interpolator which only permits types with a corresponding
`Show` typeclass instance to be substituted into a string: other types will result in an error.
The `toString` method will never be called on these substitutions.

#### Long strings

Additionally, a `txt""` interpolator is provided for constructing "long" strings which need to be
split across several lines of code, but where any whitespace (such as indentation and newlines)
should always be read as a single space character, unless it contains two adjacent newlines, in
which case it should be interpreted as a "new paragraph", represented as a single newline (`'\n'`)
character.

This is particularly useful for embedding long messages in code while not breaking the consistency
of indentation. For example:
```scala
import anticipation.Text

val msg: Text = txt"""This is a long message which will not fit into a
                      standard line of code, and needs to be split across
                      several lines.

                      But at least it is aligned nicely within the code."""
```

The `String` `msg` will contain a single `'\n'` character, between `lines.` and `But`.

#### `DebugString` typeclass

In addition to `Show`, Gossamer provides a `DebugString` single-abstract-method typeclass which is
designed to provide `String` representations of values as valid Scala expressions that could be
copied and pasted into code.

Like the `Show` typeclass, product and coproduct instances of `DebugString` are automatically
derived.

### Encodings

Simple extension methods which provide a number of string-based encodings are provided. The
`urlEncode` and `urlDecode` methods will convert to and from (respectively) strings in the
URL encoding scheme. The `punycode` method will convert the string (most commonly, a domain name)
into a ASCII-only representation of the string, encoding any non-ASCII characters as Punycode.

### Safer `String` methods

Safer alternatives to many of the commonly-used methods of `String` are provided. These typically
delegate to existing methods on `String`, but will:
- never return `null`
- never return mutable arrays
- never accept `Any` as a parameter type, or implicitly use `String#toString` to convert
  non-`String` types to `String`s

### Minimum Edit Distance

An implementation of the _Minimum Edit Distance_ or [Levenshtein
distance](https://en.wikipedia.org/wiki/Levenshtein_distance), `lev` is provided as an extension
method on `Text`s. The method takes another `Text` as a parameter, and returns the minimum
number of edits (character additions, deletions or replacements) required to change one string to
the other.

For example, `t"Hello".lev(t"Hallo!")` returns `2`: the replacement of `e` with `a` counts as one
edit, and the addition of `!` counts as the second edit. The algorithm is symmetrical.

### Joining

Scala's standard library provides the `mkString` method on collection types, but this unfortunately
calls `toString` on every element in the collection, without warning. Gossamer provides a `join`
method which may only be applied to values that are already `String`s.

This is further generalized with a `Joinable` typeclass: if an instance exists for other
`String`-like types, they may also be `join`ed like a collection of `String`s, where every
parameter to `join` is of the same type as the elements of the collection.

In addition to the zero-, one- and three-parameter variants of `join` which behave like their
`mkString` equivalents, two- and four-parameter versions are also provided. These allow a different
separator to be used between the penultimate and last elements of the collection.

For example,
```scala
val numbers = List(t"one", t"two", t"three", t"four").join(t", ", t" and ")
```
will evaluate to `"one, two, three and four"`, and,
```scala
val numbers2 = List(t"one", t"two", t"three").join(t"Choose ", t", ", t" or ", t".")
```
results in, `t"Choose one, two or three."`.




