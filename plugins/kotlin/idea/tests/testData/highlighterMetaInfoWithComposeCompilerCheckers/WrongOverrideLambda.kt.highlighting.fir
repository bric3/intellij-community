// COMPILER_ARGUMENTS: -Xplugin=$TEST_DIR$/compose_fake_plugin.jar
// FILE: main.kt
// ALLOW_ERRORS
import androidx.compose.runtime.Composable

class Impl : () -> Unit {
    @Composable override <!HIGHLIGHTING("severity='ERROR'; descr='[CONFLICTING_OVERLOADS] Conflicting overloads: fun invoke(): Unit fun invoke(): R'")!>fun invoke()<!> {}
}
