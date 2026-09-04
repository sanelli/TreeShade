package com.treeshade

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
}
