package cn.teacy.ai.plugin

object GraphPreviewTemplate {

    private val template: String by lazy {
        GraphPreviewTemplate::class.java.getResource("/template/graph-preview.html")?.readText()
            ?: error("Failed to load graph preview template")
    }

    fun render(
        bgColor: String,
        textColor: String,
        tooltipBg: String,
        tooltipColor: String,
        tooltipBorder: String,
        mermaidTheme: String,
        initialBase64: String
    ): String {
        return template
            .replace("{{ bgColor }}", bgColor)
            .replace("{{ textColor }}", textColor)
            .replace("{{ tooltipBg }}", tooltipBg)
            .replace("{{ tooltipColor }}", tooltipColor)
            .replace("{{ tooltipBorder }}", tooltipBorder)
            .replace("{{ mermaidTheme }}", mermaidTheme)
            .replace("{{ initialBase64 }}", initialBase64)
    }

}