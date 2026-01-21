package cn.teacy.ai.plugin

class MermaidGraphBuilder {

    private data class NodeData(val id: String, val label: String, val description: String?)
    private data class EdgeData(val from: String, val to: String, val style: EdgeStyle)
    private data class ClickData(val nodeId: String, val tooltip: String)

    private val nodes = mutableSetOf<NodeData>()
    private val edges = mutableSetOf<EdgeData>()
    private val clicks = mutableSetOf<ClickData>()

    private val routingNodes = mutableSetOf<String>()

    companion object {
        const val SYS_START = "__START__"
        const val SYS_END = "__END__"
        const val SYS_HEADER = "__HEADER__"
        const val SYS_DYNAMIC_NOTE = "__DYNAMIC_NOTE__"
    }

    enum class EdgeStyle {
        SOLID,
        DASHED,
        INVISIBLE
    }

    private var graphTitle: String? = null
    private var graphDescription: String? = null
    private var hasDynamicNote: Boolean = false
    private var isEmpty = false

    fun addGraphTitle(title: String) {
        this.graphTitle = title
    }

    fun addGraphDescription(description: String) {
        this.graphDescription = description
    }

    fun setEmptyState() {
        isEmpty = true
    }

    fun addDynamicBuildNote() {
        this.hasDynamicNote = true
    }

    fun addNode(id: String, label: String? = null, description: String? = null) {
        val safeLabel = (label ?: id).replace("\"", "'")
        nodes.add(NodeData(id, safeLabel, description))
        if (!description.isNullOrBlank()) {
            clicks.add(ClickData(id, description.replace("\"", "'")))
        }
    }

    fun addEdge(from: String, to: String) {
        if (from.isNotBlank() && to.isNotBlank() && to != "null") {
            edges.add(EdgeData(from, to, EdgeStyle.SOLID))
        }
    }

    fun addConditionalEdge(from: String, to: String) {
        if (from.isNotBlank() && to.isNotBlank()) {
            edges.add(EdgeData(from, to, EdgeStyle.DASHED))
        }
        routingNodes.add(from)
    }

    fun linkStartTo(targetNodeId: String) {
        edges.add(EdgeData(SYS_START, targetNodeId, EdgeStyle.SOLID))
    }

    fun build(): String {
        if (isEmpty) {
            return """
                graph TD
                    Empty[No graph elements found]
                    style Empty fill:#f9f,stroke:#333,stroke-width:2px
            """.trimIndent()
        }

        buildMetaNodeChain()

        val sb = StringBuilder()

        if (!graphTitle.isNullOrBlank()) {
            sb.append("---\n")
            val safeTitle = graphTitle!!.replace("\"", "\\\"")
            sb.append("title: \"$safeTitle\"\n")
            sb.append("---\n")
        }

        sb.append("graph TD\n")

        sb.append("    %% --- Styles ---\n")
        sb.append("    classDef StartEndStyle fill:#2d3436,stroke:#333,stroke-width:2px,color:#fff;\n")
        sb.append("    classDef HeaderStyle fill:#00000000,stroke-width:0px,color:#666,font-style:italic,font-size:14px;\n")
        sb.append("    classDef DynamicStyle fill:#fff3cd,stroke:#ffecb5,stroke-width:1px,color:#856404,stroke-dasharray: 5 5;\n")
        sb.append("\n")

        sb.append("    %% --- System Nodes ---\n")
        sb.append("    $SYS_START((START)):::StartEndStyle\n")
        sb.append("    $SYS_END((END)):::StartEndStyle\n")

        if (nodes.any { it.id == SYS_HEADER }) {
            sb.append("    $SYS_HEADER:::HeaderStyle\n")
        }
        if (nodes.any { it.id == SYS_DYNAMIC_NOTE }) {
            sb.append("    $SYS_DYNAMIC_NOTE:::DynamicStyle\n")
        }
        sb.append("\n")

        sb.append("    %% --- Meta Nodes ---\n")
        nodes.filter { isSystemNode(it.id) && it.id != SYS_START && it.id != SYS_END }.sortedBy { it.id }.forEach { node ->
            sb.append("    ${node.id}[\"${node.label}\"]\n")
        }

        sb.append("    %% --- Business Nodes ---\n")
        nodes.filter { !isSystemNode(it.id) }.sortedBy { it.id }.forEach { node ->
            if (routingNodes.contains(node.id)) {
                sb.append("    ${node.id}{\"${node.label}\"}\n")
            } else {
                sb.append("    ${node.id}[\"${node.label}\"]\n")
            }
        }
        sb.append("\n")

        sb.append("    %% --- Edges ---\n")
        edges.sortedWith(compareBy({ it.style != EdgeStyle.INVISIBLE }, { it.from }, { it.to })).forEach { edge ->
            when (edge.style) {
                EdgeStyle.DASHED -> sb.append("    ${edge.from} -.-> ${edge.to}\n")
                EdgeStyle.SOLID -> sb.append("    ${edge.from} --> ${edge.to}\n")
                EdgeStyle.INVISIBLE -> sb.append("    ${edge.from} ~~~ ${edge.to}\n")
            }
        }
        sb.append("\n")

        if (clicks.isNotEmpty()) {
            sb.append("    %% --- Interactions ---\n")
            clicks.sortedBy { it.nodeId }.forEach { click ->
                sb.append("    click ${click.nodeId} href \"#\" \"${click.tooltip}\"\n")
            }
        }

        return sb.toString()
    }

    /**
     * (Header) ~~~ (DynamicNote) ~~~ (Start)
     */
    private fun buildMetaNodeChain() {
        var previousNodeId: String? = null

        if (!graphDescription.isNullOrBlank()) {
            val htmlDesc = graphDescription!!.chunked(40).joinToString("<br/>")
            nodes.add(NodeData(SYS_HEADER, htmlDesc, null))

            previousNodeId = SYS_HEADER
        }

        if (hasDynamicNote) {
            val label = "⚠️ Dynamic Builder Detected<br/>(Part of the graph is built dynamically in code)"
            nodes.add(NodeData(SYS_DYNAMIC_NOTE, label, null))

            if (previousNodeId != null) {
                edges.add(EdgeData(previousNodeId, SYS_DYNAMIC_NOTE, EdgeStyle.INVISIBLE))
            }
            previousNodeId = SYS_DYNAMIC_NOTE
        }

        if (previousNodeId != null) {
            edges.add(EdgeData(previousNodeId, SYS_START, EdgeStyle.INVISIBLE))
        }

    }

    private fun isSystemNode(id: String): Boolean {
        return id == SYS_START || id == SYS_END || id == SYS_HEADER || id == SYS_DYNAMIC_NOTE
    }
}