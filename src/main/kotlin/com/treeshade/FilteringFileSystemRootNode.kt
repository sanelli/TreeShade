package com.treeshade

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.jetbrains.rider.projectView.views.FileSystemNodeBase
import com.jetbrains.rider.projectView.views.SolutionViewRootNodeBase
import com.jetbrains.rider.projectView.views.fileSystemExplorer.FileSystemExplorerRootNode
import java.util.Comparator

/**
 * Reuses Rider's root discovery, then rebuilds children as [FilteringFileSystemNode]
 * so hidden folders/files are excluded recursively.
 *
 * [SolutionViewTreeModel] sorts with [createComparator] from this root node
 * (not the pane), so folders-first must be defined here.
 */
class FilteringFileSystemRootNode(
    project: Project,
) : SolutionViewRootNodeBase(project) {

    override fun calculateChildren(): MutableList<AbstractTreeNode<*>> {
        val originalChildren = FileSystemExplorerRootNode(project).children
        return HideRules.sortNodes(originalChildren.mapNotNull { child -> wrapChild(child) })
    }

    override fun createComparator(): Comparator<AbstractTreeNode<*>> = FOLDER_FIRST_COMPARATOR

    private fun wrapChild(child: AbstractTreeNode<*>): AbstractTreeNode<*>? {
        val fsNode = child as? FileSystemNodeBase ?: return child
        val virtualFile = fsNode.virtualFile
        if (HideRules.isHidden(project, virtualFile)) return null
        return FilteringFileSystemNode(project, virtualFile, emptyList())
    }

    companion object {
        private val FOLDER_FIRST_COMPARATOR = Comparator<AbstractTreeNode<*>> { a, b ->
            val aDir = HideRules.isDirectoryNode(a)
            val bDir = HideRules.isDirectoryNode(b)
            when {
                aDir != bDir -> if (aDir) -1 else 1
                else -> (a.name ?: "").lowercase().compareTo((b.name ?: "").lowercase())
            }
        }
    }
}
