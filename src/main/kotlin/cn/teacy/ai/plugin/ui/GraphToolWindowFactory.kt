package cn.teacy.ai.plugin.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class GraphToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val graphPanel = GraphPreviewPanel()

        val content = ContentFactory.getInstance().createContent(graphPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}

