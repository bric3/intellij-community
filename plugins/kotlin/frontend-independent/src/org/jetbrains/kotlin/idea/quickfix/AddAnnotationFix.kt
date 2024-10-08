// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.SmartPsiElementPointer
import org.jetbrains.kotlin.idea.base.codeInsight.ShortenReferencesFacility
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.renderer.render

open class AddAnnotationFix(
    element: KtElement,
    private val annotationClassId: ClassId,
    private val kind: Kind = Kind.Self,
    private val arguments: List<String> = emptyList(),
    private val existingAnnotationEntry: SmartPsiElementPointer<KtAnnotationEntry>? = null
) : KotlinQuickFixAction<KtElement>(element) {
    override fun getText(): String {
        val annotationCall = annotationClassId.shortClassName.render() + renderArgumentsForIntentionName()
        return when (kind) {
            Kind.Self -> KotlinBundle.message("fix.add.annotation.text.self", annotationCall)
            Kind.Constructor -> KotlinBundle.message("fix.add.annotation.text.constructor", annotationCall)
            is Kind.Declaration -> KotlinBundle.message("fix.add.annotation.text.declaration", annotationCall, kind.name ?: "?")
            is Kind.ContainingClass -> KotlinBundle.message("fix.add.annotation.text.containing.class", annotationCall, kind.name ?: "?")
            is Kind.Copy -> KotlinBundle.message("fix.add.annotation.with.arguments.text.copy", annotationCall, kind.source, kind.target)
        }
    }

    protected open fun renderArgumentsForIntentionName(): String {
        return arguments.takeIf { it.isNotEmpty() }?.joinToString(", ", "(", ")") ?: ""
    }

    override fun getFamilyName(): String = KotlinBundle.message("fix.add.annotation.family")

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val annotationEntry = existingAnnotationEntry?.element
        val annotationInnerText = arguments.takeIf { it.isNotEmpty() }?.joinToString(", ")
        if (annotationEntry != null) {
            if (annotationInnerText == null) return
            val psiFactory = KtPsiFactory(project)
            annotationEntry.valueArgumentList?.addArgument(psiFactory.createArgument(annotationInnerText))
                ?: annotationEntry.addAfter(psiFactory.createCallArguments("($annotationInnerText)"), annotationEntry.lastChild)
            ShortenReferencesFacility.getInstance().shorten(annotationEntry)
        } else {
            element.addAnnotation(annotationClassId, annotationInnerText, searchForExistingEntry = false)
        }
    }

    sealed class Kind {
        data object Self : Kind()
        data object Constructor : Kind()
        data class Declaration(val name: String?) : Kind()
        data class ContainingClass(val name: String?) : Kind()
        data class Copy(val source: String, val target: String) : Kind()
    }
}
