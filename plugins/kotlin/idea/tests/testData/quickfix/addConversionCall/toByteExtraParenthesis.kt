// "Convert expression to 'Byte'" "true"

fun takeByte(x: Byte) {}

fun foo() {
    takeByte(1 + (1 + 1)<caret>)
}
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddConversionCallFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.NumberConversionFix