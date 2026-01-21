package cn.teacy.ai.plugin.provider

import cn.teacy.ai.plugin.Annotations
import cn.teacy.ai.plugin.ui.GraphPreviewPanel
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier

class GraphLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element !is PsiIdentifier) return

        val psiClass = element.parent as? PsiClass ?: return

        if (!psiClass.hasAnnotation(Annotations.GRAPH_COMPOSER)) {
            return
        }

        val builder = NavigationGutterIconBuilder.create(AllIcons.Actions.Preview)
            .setTooltipText("Preview this Graph")
            .setTarget(null)
            .setNamer { "Graph Preview" }

        val markerInfo = builder.createLineMarkerInfo(element) { _, _ ->
            openPreviewWindow(psiClass)
        }

        result.add(markerInfo)
    }

    private fun openPreviewWindow(psiClass: PsiClass) {
        val project = psiClass.project
        val toolWindowManager = ToolWindowManager.getInstance(project)

        val toolWindow = toolWindowManager.getToolWindow("Saa Graph Preview") ?: return

        toolWindow.activate {
            val content = toolWindow.contentManager.getContent(0)
            val component = content?.component

            if (component is GraphPreviewPanel) {
                component.renderGraph(psiClass)
            }
        }
    }
}