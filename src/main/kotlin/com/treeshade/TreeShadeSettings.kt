package com.treeshade

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "TreeShadeSettings", storages = [Storage("treeshade.xml")])
class TreeShadeSettings : PersistentStateComponent<TreeShadeSettings.State> {

    var hiddenFolders: MutableList<String> = DEFAULT_FOLDERS.toMutableList()
    var hiddenFiles: MutableList<String> = DEFAULT_FILES.toMutableList()

    data class State(
        var hiddenFolders: MutableList<String> = mutableListOf(),
        var hiddenFiles: MutableList<String> = mutableListOf(),
    )

    override fun getState(): State = State(
        hiddenFolders = hiddenFolders.toMutableList(),
        hiddenFiles = hiddenFiles.toMutableList(),
    )

    override fun loadState(state: State) {
        hiddenFolders = state.hiddenFolders.toMutableList()
        hiddenFiles = state.hiddenFiles.toMutableList()
    }

    companion object {
        val DEFAULT_FOLDERS = listOf("alire", ".git", "lib", "bin", "obj", "config")
        val DEFAULT_FILES = listOf(".DS_Store")

        fun getInstance(project: Project): TreeShadeSettings = project.service()
    }
}
