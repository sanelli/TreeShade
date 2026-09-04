package com.example.filter

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.views.FileSystemNodeBase
import com.jetbrains.rider.projectView.views.SolutionViewRootNodeBase
import com.jetbrains.rider.projectView.views.fileSystemExplorer.FileSystemExplorerRootNode

/**
 * Reuses Rider's root discovery, then rebuilds children as [FilteringFileSystemNode]
 * so hidden folders/files are excluded recursively.
 */
class FilteringFileSystemRootNode(
    project: Project,
) : SolutionViewRootNodeBase(project) {

    override fun calculateChildren(): MutableList<AbstractTreeNode<*>> {
        val originalChildren = FileSystemExplorerRootNode(project).children
        return originalChildren.mapNotNull { child -> wrapChild(child) }.toMutableList()
    }

    private fun wrapChild(child: AbstractTreeNode<*>): AbstractTreeNode<*>? {
        val fsNode = child as? FileSystemNodeBase ?: return child
        val virtualFile = fsNode.virtualFile
        if (HideRules.isHidden(virtualFile)) return null
        return FilteringFileSystemNode(project, virtualFile, emptyList())
    }
}
