package cn.teacy.ai.plugin.ui

import cn.teacy.ai.plugin.GraphParser
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.psi.PsiClass
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.UIUtil
import java.awt.Color

class GraphPreviewPanel() : SimpleToolWindowPanel(true, true) {

    private val browser = JBCefBrowser()

    private var lastPicClass : PsiClass? = null

    init {
        val initialHtml = getHtmlContent("graph TD\nReady[Select a @GraphComposer class to preview...]")
        browser.loadHTML(initialHtml)
        setContent(browser.component)

        toolbar = createToolBar().component
    }

    private fun createToolBar(): ActionToolbar {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(object : AnAction("Refresh Graph", "Refresh current Graph", AllIcons.Actions.Refresh) {
            override fun actionPerformed(e: AnActionEvent) {
                if (lastPicClass != null) {
                    renderGraph(lastPicClass!!)
                }
            }
        })
        val toolbar = ActionManager.getInstance().createActionToolbar("SaaGraphToolbar", actionGroup, true)
        toolbar.targetComponent = browser.component
        return toolbar
    }

    fun renderGraph(psiClass: PsiClass) {
        lastPicClass = psiClass
        val mermaidCode = GraphParser.parse(psiClass)
        updateHtml(mermaidCode)
    }

    private fun updateHtml(mermaidCode: String) {
        browser.loadHTML(
            getHtmlContent(mermaidCode)
        )
    }

    private fun getHtmlContent(mermaidCode: String): String {
        val isDark = UIUtil.isUnderDarcula()

        val mermaidTheme = if (isDark) "dark" else "default"

        val bgColor = colorToHex(UIUtil.getPanelBackground())
        val textColor = if (isDark) "#bbbbbb" else "#333333"

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { background-color: $bgColor; color: $textColor; margin: 0; padding: 10px; font-family: sans-serif; }
                    ::-webkit-scrollbar { display: none; }
                </style>
                <script type="module">
                    import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                    mermaid.initialize({ startOnLoad: true, theme: '$mermaidTheme' });
                </script>
            </head>
            <body>
                <pre class="mermaid">$mermaidCode</pre>
            </body>
            </html>
        """.trimIndent()
    }

    private fun colorToHex(color: Color): String {
        return "#%02x%02x%02x".format(color.red, color.green, color.blue)
    }
}