package com.treeshade

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.views.FileSystemNodeBase

object HideRules {
    fun isHidden(project: Project, file: VirtualFile): Boolean {
        val settings = TreeShadeSettings.getInstance(project)
        val name = file.name
        return if (file.isDirectory) name in settings.hiddenFolders else name in settings.hiddenFiles
    }

    fun isHiddenName(project: Project, name: String): Boolean {
        val settings = TreeShadeSettings.getInstance(project)
        return name in settings.hiddenFolders || name in settings.hiddenFiles
    }

    fun sortFiles(files: Collection<VirtualFile>): List<VirtualFile> =
        files.sortedWith(FILE_COMPARATOR)

    fun sortNodes(nodes: Collection<AbstractTreeNode<*>>): MutableList<AbstractTreeNode<*>> =
        nodes.sortedWith(NODE_COMPARATOR).toMutableList()

    fun sortNodesInPlace(nodes: MutableList<AbstractTreeNode<*>>) {
        nodes.sortWith(NODE_COMPARATOR)
    }

    /** Folders first, then files; each group alphabetical (case-insensitive). */
    private val FILE_COMPARATOR = compareBy<VirtualFile>({ !it.isDirectory }, { it.name.lowercase() })

    private val NODE_COMPARATOR = Comparator<AbstractTreeNode<*>> { a, b ->
        val aDir = isDirectoryNode(a)
        val bDir = isDirectoryNode(b)
        when {
            aDir != bDir -> if (aDir) -1 else 1
            else -> nodeName(a).compareTo(nodeName(b))
        }
    }

    private fun isDirectoryNode(node: AbstractTreeNode<*>): Boolean {
        virtualFileOf(node)?.let { return it.isDirectory }
        return !node.isAlwaysLeaf
    }

    private fun virtualFileOf(node: AbstractTreeNode<*>): VirtualFile? =
        (node as? ProjectViewNode<*>)?.virtualFile
            ?: (node as? FileSystemNodeBase)?.virtualFile
            ?: node.value as? VirtualFile

    private fun nodeName(node: AbstractTreeNode<*>): String =
        virtualFileOf(node)?.name?.lowercase()
            ?: (node.name ?: "").lowercase()
}
