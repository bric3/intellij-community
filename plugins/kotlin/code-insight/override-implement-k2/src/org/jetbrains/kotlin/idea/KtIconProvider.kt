// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea

import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiClass
import com.intellij.ui.IconManager
import com.intellij.ui.RowIcon
import com.intellij.util.PlatformIcons
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.*
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaSymbolKind
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaSymbolWithModality
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaSymbolWithVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import javax.swing.Icon

object KtIconProvider {
    private val LOG = Logger.getInstance(KtIconProvider::class.java)

    context(KaSession)
    fun getIcon(ktSymbol: KtSymbol): Icon? {
        // logic copied from org.jetbrains.kotlin.idea.KotlinDescriptorIconProvider
        val declaration = ktSymbol.psi
        return if (declaration?.isValid == true) {
            val isClass = declaration is PsiClass || declaration is KtClass
            val flags = if (isClass) 0 else Iconable.ICON_FLAG_VISIBILITY
            if (declaration is KtDeclaration) {
                // kotlin declaration
                // visibility and abstraction better detect by a descriptor
                getIcon(ktSymbol, flags) ?: declaration.getIcon(flags)
            } else {
                // Use Java icons if it's not Kotlin declaration
                declaration.getIcon(flags)
            }
        } else {
            getIcon(ktSymbol, 0)
        }
    }

    context(KaSession)
    private fun getIcon(symbol: KtSymbol, flags: Int): Icon? {
        var result: Icon = getBaseIcon(symbol) ?: return null

        if (flags and Iconable.ICON_FLAG_VISIBILITY > 0) {
            val rowIcon = RowIcon(2)
            rowIcon.setIcon(result, 0)
            rowIcon.setIcon(getVisibilityIcon(symbol), 1)
            result = rowIcon
        }
        return result
    }

    context(KaSession)
    fun getBaseIcon(symbol: KtSymbol): Icon? {
        val isAbstract = (symbol as? KaSymbolWithModality)?.modality == Modality.ABSTRACT
        return when (symbol) {
            is KtPackageSymbol -> AllIcons.Nodes.Package
            is KaFunctionLikeSymbol -> {
                val isExtension = symbol.isExtension
                val isMember = symbol.symbolKind == KaSymbolKind.CLASS_MEMBER
                when {
                    isExtension && isAbstract -> KotlinIcons.ABSTRACT_EXTENSION_FUNCTION
                    isExtension && !isAbstract -> KotlinIcons.EXTENSION_FUNCTION
                    isMember && isAbstract -> PlatformIcons.ABSTRACT_METHOD_ICON
                    isMember && !isAbstract -> IconManager.getInstance().getPlatformIcon(com.intellij.ui.PlatformIcons.Method)
                    else -> KotlinIcons.FUNCTION
                }
            }
            is KaClassOrObjectSymbol -> {
                when (symbol.classKind) {
                    KaClassKind.CLASS -> when {
                        isAbstract -> KotlinIcons.ABSTRACT_CLASS
                        else -> KotlinIcons.CLASS
                    }
                    KaClassKind.ENUM_CLASS -> KotlinIcons.ENUM
                    KaClassKind.ANNOTATION_CLASS -> KotlinIcons.ANNOTATION
                    KaClassKind.INTERFACE -> KotlinIcons.INTERFACE
                    KaClassKind.ANONYMOUS_OBJECT, KaClassKind.OBJECT, KaClassKind.COMPANION_OBJECT -> KotlinIcons.OBJECT
                }
            }
            is KaValueParameterSymbol -> KotlinIcons.PARAMETER
            is KaLocalVariableSymbol -> when {
                symbol.isVal -> KotlinIcons.VAL
                else -> KotlinIcons.VAR
            }
            is KtPropertySymbol -> when {
                symbol.isVal -> KotlinIcons.FIELD_VAL
                else -> KotlinIcons.FIELD_VAR
            }
            is KaTypeParameterSymbol -> IconManager.getInstance().getPlatformIcon(com.intellij.ui.PlatformIcons.Class)
            is KaTypeAliasSymbol -> KotlinIcons.TYPE_ALIAS

            else -> {
                LOG.warn("No icon for symbol: $symbol")
                null
            }
        }
    }

    context(KaSession)
    private fun getVisibilityIcon(symbol: KtSymbol): Icon? {
        return when ((symbol as? KaSymbolWithVisibility)?.visibility?.normalize()) {
            Visibilities.Public -> PlatformIcons.PUBLIC_ICON
            Visibilities.Protected -> IconManager.getInstance().getPlatformIcon(com.intellij.ui.PlatformIcons.Protected)
            Visibilities.Private, Visibilities.PrivateToThis -> IconManager.getInstance()
                .getPlatformIcon(com.intellij.ui.PlatformIcons.Private)
            Visibilities.Internal -> IconManager.getInstance().getPlatformIcon(com.intellij.ui.PlatformIcons.Local)
            else -> null
        }
    }
}
