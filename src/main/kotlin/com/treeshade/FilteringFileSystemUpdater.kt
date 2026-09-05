package com.treeshade

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.ProjectModelViewUpdater
import com.jetbrains.rider.projectView.views.SolutionViewVisitor
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity
import com.jetbrains.rider.projectView.workspace.getVirtualFileAsContentRoot

/**
 * Rider only refreshes [com.jetbrains.rider.projectView.views.fileSystemExplorer.FileSystemExplorerPane]
 * when files are added or removed. Tree Shade uses a separate pane, so it needs the same
 * [ProjectModelViewUpdater] hooks or the File System tree stays stale until a full refresh.
 */
class FilteringFileSystemUpdater(
    project: Project,
) : ProjectModelViewUpdater(project) {

    private var cachePane: FilteringFileSystemPane? = null

    private fun pane(): FilteringFileSystemPane? {
        cachePane?.let { return it }
        return FilteringFileSystemPane.tryGetInstance(project).also { cachePane = it }
    }

    private fun refreshFile(file: VirtualFile?, withChildren: Boolean) {
        val virtualFile = file ?: return
        pane()?.refresh(SolutionViewVisitor.createForRefresh(virtualFile), false, withChildren)
    }

    override fun update(entity: ProjectModelEntity?) {
        refreshFile(entity?.getVirtualFileAsContentRoot(), withChildren = false)
    }

    override fun update(file: VirtualFile?) {
        refreshFile(file, withChildren = false)
    }

    override fun updateWithChildren(entity: ProjectModelEntity?) {
        refreshFile(entity?.getVirtualFileAsContentRoot(), withChildren = true)
    }

    override fun updateWithChildren(file: VirtualFile?) {
        refreshFile(file, withChildren = true)
    }

    override fun updateAll() {
        pane()?.updateFromRoot()
    }

    override fun updateAllPresentations() {
        pane()?.updatePresentationsFromRoot()
    }
}
