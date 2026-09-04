package com.treeshade

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

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

    private val FILE_COMPARATOR = compareBy<VirtualFile>({ !it.isDirectory }, { it.name.lowercase() })

    private val NODE_COMPARATOR = Comparator<AbstractTreeNode<*>> { a, b ->
        val aFile = (a as? ProjectViewNode<*>)?.virtualFile
        val bFile = (b as? ProjectViewNode<*>)?.virtualFile
        when {
            aFile != null && bFile != null -> FILE_COMPARATOR.compare(aFile, bFile)
            else -> (a.name ?: "").lowercase().compareTo((b.name ?: "").lowercase())
        }
    }
}
