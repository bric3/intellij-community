// FIX: Replace 'if' expression with safe access expression
// WITH_STDLIB
// HIGHLIGHT: INFORMATION
fun convert(x: String, y: String) = ""

fun foo(a: String?, b: String) {
    <caret>if (a != null) convert(a, b) else null
}