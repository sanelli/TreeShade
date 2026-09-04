package com.treeshade

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import javax.swing.JComponent
import javax.swing.JPanel

class TreeShadeConfigurable(
    private val project: Project,
) : Configurable {

    private var panel: JPanel? = null
    private var foldersArea: JBTextArea? = null
    private var filesArea: JBTextArea? = null

    override fun getDisplayName(): String = "TreeShade"

    override fun createComponent(): JComponent {
        val folders = JBTextArea(8, 40).also {
            it.border = JBUI.Borders.empty(4)
            foldersArea = it
        }
        val files = JBTextArea(6, 40).also {
            it.border = JBUI.Borders.empty(4)
            filesArea = it
        }

        panel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("Hidden folders (one name per line):"))
            .addComponent(JBScrollPane(folders))
            .addComponent(JBLabel("Hidden files (one name per line):"))
            .addComponent(JBScrollPane(files))
            .addComponentFillVertically(JPanel(), 0)
            .panel

        reset()
        return panel!!
    }

    override fun isModified(): Boolean {
        val settings = TreeShadeSettings.getInstance(project)
        return parseLines(foldersArea) != settings.hiddenFolders ||
            parseLines(filesArea) != settings.hiddenFiles
    }

    override fun apply() {
        val settings = TreeShadeSettings.getInstance(project)
        settings.hiddenFolders = parseLines(foldersArea).toMutableList()
        settings.hiddenFiles = parseLines(filesArea).toMutableList()
        ProjectView.getInstance(project).refresh()
    }

    override fun reset() {
        val settings = TreeShadeSettings.getInstance(project)
        foldersArea?.text = settings.hiddenFolders.joinToString("\n")
        filesArea?.text = settings.hiddenFiles.joinToString("\n")
    }

    override fun disposeUIResources() {
        panel = null
        foldersArea = null
        filesArea = null
    }

    private fun parseLines(area: JBTextArea?): List<String> {
        if (area == null) return emptyList()
        return area.text
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()
    }
}
