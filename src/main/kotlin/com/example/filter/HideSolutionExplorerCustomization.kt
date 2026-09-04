package com.example.filter

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.views.solutionExplorer.SolutionExplorerCustomization
import com.jetbrains.rider.projectView.views.solutionExplorer.SolutionExplorerViewSettings
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity

class HideSolutionExplorerCustomization(
    project: Project,
) : SolutionExplorerCustomization(project) {

    override fun modifyChildren(
        entity: ProjectModelEntity,
        settings: SolutionExplorerViewSettings,
        children: MutableList<AbstractTreeNode<*>>,
    ) {
        children.removeAll { node ->
            val name = node.name ?: return@removeAll false
            HideRules.isHiddenName(name)
        }
    }

    override fun modifyChildren(
        virtualFile: VirtualFile,
        settings: SolutionExplorerViewSettings,
        children: MutableList<AbstractTreeNode<*>>,
    ) {
        children.removeAll { node ->
            val name = node.name ?: return@removeAll false
            HideRules.isHiddenName(name)
        }
    }
}
