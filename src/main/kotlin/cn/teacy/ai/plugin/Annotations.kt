package cn.teacy.ai.plugin

object Annotations {
    const val GRAPH_COMPOSER = "cn.teacy.ai.annotation.GraphComposer"
    const val GRAPH_NODE = "cn.teacy.ai.annotation.GraphNode"
    const val CONDITIONAL_EDGE = "cn.teacy.ai.annotation.ConditionalEdge"
    const val GRAPH_KEY = "cn.teacy.ai.annotation.GraphKey"
    const val COMPILE_CONFIG = "cn.teacy.ai.annotation.GraphCompileConfig"

    val ENTRY_POINTS = setOf(
        GRAPH_NODE,
        CONDITIONAL_EDGE,
        GRAPH_KEY,
        COMPILE_CONFIG
    )
}