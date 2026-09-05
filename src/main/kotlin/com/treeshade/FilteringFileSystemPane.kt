package com.treeshade

import com.intellij.icons.AllIcons
import com.intellij.ide.SelectInContext
import com.intellij.ide.SelectInTarget
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.project.Project
import com.jetbrains.rider.projectView.views.SolutionViewPaneBase
import com.jetbrains.rider.projectView.views.impl.SolutionViewSelectInTargetBase
import javax.swing.Icon

/**
 * Rider's built-in File System explorer ignores [com.intellij.ide.projectView.TreeStructureProvider].
 * This pane mirrors that explorer while filtering via [FilteringFileSystemNode].
 *
 * Folders-first sorting is provided by [FilteringFileSystemRootNode.createComparator],
 * which [com.jetbrains.rider.projectView.views.impl.SolutionViewTreeModel] uses.
 */
class FilteringFileSystemPane(
    project: Project,
) : SolutionViewPaneBase(project, FilteringFileSystemRootNode(project)) {

    override fun getId(): String = ID

    override fun getTitle(): String = TITLE

    override fun getIcon(): Icon = AllIcons.Nodes.Folder

    override fun getWeight(): Int = 0

    override fun isInitiallyVisible(): Boolean = true

    override fun createSelectInTarget(): SelectInTarget =
        object : SolutionViewSelectInTargetBase(project) {
            override fun selectIn(context: SelectInContext, requestFocus: Boolean) {
                select(context, null, requestFocus)
            }

            override fun getMinorViewId(): String = ID

            override fun getWeight(): Float = companionWeight.toFloat()

            override fun toString(): String = TITLE
        }

    companion object {
        const val ID = "FilteredFileSystemExplorer"
        const val TITLE = "File System"
        private val companionWeight = 0

        fun tryGetInstance(project: Project): FilteringFileSystemPane? {
            if (project.isDisposed) return null
            return ProjectView.getInstance(project).getProjectViewPaneById(ID) as? FilteringFileSystemPane
        }
    }
}
