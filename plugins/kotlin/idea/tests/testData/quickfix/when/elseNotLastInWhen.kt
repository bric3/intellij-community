// "Move else branch to the end" "true"
fun test() {
    val a = 12
    when (a) {
        1 -> { /* some code */ }
        el<caret>se -> { /* other code */ }
        2 -> { /* some more code */ }
    }
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.MoveWhenElseBranchFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.MoveWhenElseBranchFix