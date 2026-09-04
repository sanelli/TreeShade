package com.example.filter

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.IconUtil
import com.jetbrains.rider.projectView.views.FileSystemNodeBase
import com.jetbrains.rider.projectView.views.NestingNode
import com.jetbrains.rider.projectView.views.fileSystemExplorer.FileSystemExplorerCustomization

class FilteringFileSystemNode(
    project: Project,
    file: VirtualFile,
    nestedFiles: List<NestingNode<VirtualFile>>,
) : FileSystemNodeBase(project, file, nestedFiles) {

    override fun createNode(
        virtualFile: VirtualFile,
        nestedFiles: List<NestingNode<VirtualFile>>,
    ): FileSystemNodeBase = FilteringFileSystemNode(project, virtualFile, nestedFiles)

    override fun getVirtualFileChildren(): List<VirtualFile> =
        super.getVirtualFileChildren().filterNot(HideRules::isHidden)

    override fun update(presentation: PresentationData) {
        val virtualFile = file
        if (!virtualFile.isValid) return

        presentation.addText(name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        presentation.setIcon(IconUtil.getIcon(virtualFile, 0, project))

        FileSystemExplorerCustomization.getExtensions(project).forEach { customization ->
            customization.updateNode(presentation, virtualFile, this)
        }
    }
}
