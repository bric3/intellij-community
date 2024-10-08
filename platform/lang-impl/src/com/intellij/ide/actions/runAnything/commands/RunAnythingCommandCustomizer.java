// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.actions.runAnything.commands;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.vfs.VirtualFile;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;

/**
 * This class customizes 'Run Anything' command line and its data context.
 * E.g. it's possible to wrap command into a shell or/and patch environment variables.
 */
public abstract class RunAnythingCommandCustomizer {
  public static final ExtensionPointName<RunAnythingCommandCustomizer> EP_NAME =
    ExtensionPointName.create("com.intellij.runAnything.commandCustomizer");

  /**
   * Customizes command line to be executed
   *
   * @param workDirectory the working directory the command will be executed in
   * @param dataContext   {@link DataContext} to fetch module, project etc.
   * @param commandLine   command line to be customized
   * @return patched command line
   */
  protected @NotNull GeneralCommandLine customizeCommandLine(@NotNull VirtualFile workDirectory,
                                                    @NotNull DataContext dataContext,
                                                    @NotNull GeneralCommandLine commandLine) {
    return commandLine;
  }

  /**
   * Customizes data context command line to be executed on
   *
   * @param dataContext original {@link DataContext}
   * @return customized {@link DataContext}
   */
  protected @NotNull DataContext customizeDataContext(@NotNull DataContext dataContext) {
    return dataContext;
  }

  public static @NotNull GeneralCommandLine customizeCommandLine(@NotNull DataContext dataContext,
                                                                 @NotNull VirtualFile workDirectory,
                                                                 @NotNull GeneralCommandLine commandLine) {
    return StreamEx.of(EP_NAME.getExtensions())
                   .foldLeft(commandLine, (cmdLine, customizer) -> customizer.customizeCommandLine(workDirectory, dataContext, cmdLine));
  }

  public static @NotNull DataContext customizeContext(@NotNull DataContext dataContext) {
    return StreamEx.of(EP_NAME.getExtensions()).foldLeft(dataContext, (context, customizer) -> customizer.customizeDataContext(context));
  }
}