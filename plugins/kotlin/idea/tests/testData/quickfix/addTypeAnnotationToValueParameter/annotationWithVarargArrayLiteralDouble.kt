// "Add type 'Double' to parameter 'value'" "true"
// LANGUAGE_VERSION: 1.2

annotation class CollectionDefault(vararg val value = [1.0, 2.2]<caret>)
// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddTypeAnnotationToValueParameterFix
/* IGNORE_K2 */