// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.execution.junit.kotlin.codeInspection

import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginMode

class K2JUnitMalformedDeclarationInspectionTestV57 : KotlinJUnitMalformedDeclarationInspectionTestV57() {
  override val pluginMode: KotlinPluginMode = KotlinPluginMode.K2
}

class K2JUnitMalformedDeclarationInspectionTestLatest : KotlinJUnitMalformedDeclarationInspectionTestLatest() {
  override val pluginMode: KotlinPluginMode = KotlinPluginMode.K2
}