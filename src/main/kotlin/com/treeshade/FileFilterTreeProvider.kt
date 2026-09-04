package com.treeshade

import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware

/** Fallback for IDEA-style project views that still consult treeStructureProvider. */
class FileFilterTreeProvider : TreeStructureProvider, DumbAware {

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?,
    ): MutableCollection<AbstractTreeNode<*>> {
        return HideRules.sortNodes(children.filterNot(::isHidden))
    }

    private fun isHidden(node: AbstractTreeNode<*>): Boolean {
        val project = node.project ?: return false
        val file = (node as? ProjectViewNode<*>)?.virtualFile
        if (file != null) {
            return HideRules.isHidden(project, file)
        }
        val name = node.name ?: return false
        return HideRules.isHiddenName(project, name)
    }
}
