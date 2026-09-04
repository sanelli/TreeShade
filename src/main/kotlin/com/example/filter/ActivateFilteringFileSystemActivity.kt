package com.example.filter

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Switches the Project tool window to the filtered File System pane on open. */
class ActivateFilteringFileSystemActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        withContext(Dispatchers.EDT) {
            val projectView = ProjectView.getInstance(project)
            if (projectView.getProjectViewPaneById(FilteringFileSystemPane.ID) != null) {
                projectView.changeView(FilteringFileSystemPane.ID)
            }
        }
    }
}
