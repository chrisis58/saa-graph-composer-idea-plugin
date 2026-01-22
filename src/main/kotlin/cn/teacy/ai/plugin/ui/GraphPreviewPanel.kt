package cn.teacy.ai.plugin.ui

import cn.teacy.ai.plugin.GraphParser
import cn.teacy.ai.plugin.GraphPreviewTemplate
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.psi.PsiClass
import com.intellij.ui.ColorUtil
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.UIUtil
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.nio.charset.StandardCharsets
import java.util.Base64

class GraphPreviewPanel() : SimpleToolWindowPanel(true, true) {

    private val browser = JBCefBrowser()
    private var lastPicClass: PsiClass? = null
    private var lastMermaidCode: String? = null

    private var isPageLoaded = false
    private var lastIsDark: Boolean? = null

    init {
        updateHtml("graph TD\nReady[Select a @GraphComposer class to preview...]")
        setContent(browser.component)

        toolbar = createToolBar().component
    }

    private fun createToolBar(): ActionToolbar {
        val actionGroup = DefaultActionGroup().apply {
            add(object : AnAction("Refresh Graph", "Refresh current Graph", AllIcons.Actions.Refresh) {
                override fun actionPerformed(e: AnActionEvent) {
                    if (lastPicClass != null) {
                        renderGraph(lastPicClass!!)
                    }
                }
            })

            add(object : AnAction("Copy Source Code", "Copy current Mermaid source code to clipboard", AllIcons.Actions.Copy) {
                override fun actionPerformed(e: AnActionEvent) {
                    if (lastPicClass != null) {
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        val selection = StringSelection(lastMermaidCode!!)
                        clipboard.setContents(selection, selection)
                    }
                }

                override fun update(e: AnActionEvent) {
                    val hasContent = lastPicClass != null && !lastMermaidCode.isNullOrEmpty()
                    e.presentation.isEnabled = hasContent
                }

                override fun getActionUpdateThread(): ActionUpdateThread {
                    return ActionUpdateThread.BGT
                }
            })
        }

        val toolbar = ActionManager.getInstance().createActionToolbar("SaaGraphToolbar", actionGroup, true).apply {
            targetComponent = browser.component
        }
        return toolbar
    }

    fun renderGraph(psiClass: PsiClass) {
        lastPicClass = psiClass
        val mermaidCode = GraphParser.parse(psiClass)
        updateHtml(mermaidCode)
    }

    private fun updateHtml(mermaidCode: String) {
        val currentIsDark = UIUtil.isUnderDarcula()
        lastMermaidCode = mermaidCode

        if (isPageLoaded && lastIsDark == currentIsDark) {
            updateGraphViaJS(mermaidCode)
            return
        }

        lastIsDark = currentIsDark
        browser.loadHTML(getHtmlContent(mermaidCode))
        isPageLoaded = true
    }

    private fun updateGraphViaJS(mermaidCode: String) {
        val base64Code = encodeToBase64(mermaidCode)
        val js = "window.renderMermaid('$base64Code')"
        browser.cefBrowser.executeJavaScript(js, browser.cefBrowser.url, 0)
    }

    private fun encodeToBase64(text: String): String {
        return Base64.getEncoder().encodeToString(text.toByteArray(StandardCharsets.UTF_8))
    }

    private fun getHtmlContent(mermaidCode: String): String {
        val isDark = UIUtil.isUnderDarcula()
        val mermaidTheme = if (isDark) "dark" else "default"
        val scheme = EditorColorsManager.getInstance().globalScheme
        val bgColor = ColorUtil.toHex(scheme.defaultBackground)
        val textColor = if (isDark) "#bbbbbb" else "#333333"

        val tooltipBg = if (isDark) "#3c3f41" else "#ffffff"
        val tooltipBorder = if (isDark) "#616161" else "#bbb"
        val tooltipColor = if (isDark) "#f1f1f1" else "#333"

        val initialBase64 = encodeToBase64(mermaidCode)

        return GraphPreviewTemplate.render(
            bgColor,
            textColor,
            tooltipBg,
            tooltipColor,
            tooltipBorder,
            mermaidTheme,
            initialBase64
        )
    }

}