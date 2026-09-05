package com.treeshade

import com.intellij.openapi.diagnostic.Logger
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
 *
 * Enable traces with `#com.treeshade.FilteringFileSystemUpdater` in
 * **Help | Diagnostic Tools | Debug Log Settings**.
 */
class FilteringFileSystemUpdater(
    project: Project,
) : ProjectModelViewUpdater(project) {

    private var cachePane: FilteringFileSystemPane? = null
    private var loggedMissingPane = false

    private fun pane(): FilteringFileSystemPane? {
        cachePane?.let { return it }
        val found = FilteringFileSystemPane.tryGetInstance(project)
        if (found != null) {
            cachePane = found
            LOG.info("Attached to File System pane")
            loggedMissingPane = false
        } else if (!loggedMissingPane) {
            LOG.warn("File System pane is not registered yet; tree refresh will retry on the next update")
            loggedMissingPane = true
        }
        return found
    }

    private fun refreshFile(file: VirtualFile?, withChildren: Boolean) {
        val virtualFile = file ?: run {
            LOG.debug("Skipping refresh: no virtual file")
            return
        }
        val pane = pane()
        if (pane == null) {
            LOG.debug("Skipping refresh of ${virtualFile.path} (withChildren=$withChildren): pane unavailable")
            return
        }
        LOG.debug("Refreshing ${virtualFile.path} (withChildren=$withChildren)")
        pane.refresh(SolutionViewVisitor.createForRefresh(virtualFile), false, withChildren)
    }

    override fun update(entity: ProjectModelEntity?) {
        LOG.debug("update(entity=${entity?.name})")
        refreshFile(entity?.getVirtualFileAsContentRoot(), withChildren = false)
    }

    override fun update(file: VirtualFile?) {
        LOG.debug("update(file=${file?.path})")
        refreshFile(file, withChildren = false)
    }

    override fun updateWithChildren(entity: ProjectModelEntity?) {
        LOG.debug("updateWithChildren(entity=${entity?.name})")
        refreshFile(entity?.getVirtualFileAsContentRoot(), withChildren = true)
    }

    override fun updateWithChildren(file: VirtualFile?) {
        LOG.debug("updateWithChildren(file=${file?.path})")
        refreshFile(file, withChildren = true)
    }

    override fun updateAll() {
        val pane = pane()
        if (pane == null) {
            LOG.debug("Skipping updateAll: pane unavailable")
            return
        }
        LOG.info("Refreshing File System tree from root")
        pane.updateFromRoot()
    }

    override fun updateAllPresentations() {
        val pane = pane()
        if (pane == null) {
            LOG.debug("Skipping updateAllPresentations: pane unavailable")
            return
        }
        LOG.debug("Refreshing File System presentations from root")
        pane.updatePresentationsFromRoot()
    }

    companion object {
        private val LOG = Logger.getInstance(FilteringFileSystemUpdater::class.java)
    }
}
