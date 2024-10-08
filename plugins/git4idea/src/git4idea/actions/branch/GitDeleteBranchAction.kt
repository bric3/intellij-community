// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package git4idea.actions.branch

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import git4idea.GitBranch
import git4idea.branch.GitBrancher
import git4idea.i18n.GitBundle
import git4idea.isRemoteBranchProtected
import git4idea.repo.GitRepository

class GitDeleteBranchAction
  : GitSingleBranchAction(GitBundle.messagePointer("branches.action.delete")) {

  override fun isEnabledForRef(ref: GitBranch, repositories: List<GitRepository>) = !isCurrentRefInAnyRepo(ref, repositories)

  override fun updateIfEnabledAndVisible(e: AnActionEvent, project: Project, repositories: List<GitRepository>, branch: GitBranch) {
    if (branch.isRemote) {
      e.presentation.isEnabled = !isRemoteBranchProtected(repositories, branch.name)
    }
  }

  override fun actionPerformed(e: AnActionEvent, project: Project, repositories: List<GitRepository>, branch: GitBranch) {
    val brancher = GitBrancher.getInstance(project)
    if (branch.isRemote) {
      brancher.deleteRemoteBranch(branch.name, repositories)
    }
    else {
      brancher.deleteBranch(branch.name, repositories.filter { branch != it.currentBranch })
    }
  }
}
