package com.treeshade

import com.intellij.openapi.vfs.VirtualFile

object HideRules {
    private val hiddenFolders = setOf("alire", ".git", "lib", "bin", "obj", "config")
    private val hiddenFiles = setOf(".DS_Store")

    fun isHidden(file: VirtualFile): Boolean {
        val name = file.name
        return if (file.isDirectory) name in hiddenFolders else name in hiddenFiles
    }

    fun isHiddenName(name: String): Boolean =
        name in hiddenFolders || name in hiddenFiles
}
