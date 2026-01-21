package cn.teacy.ai.plugin

import com.intellij.openapi.project.Project
import com.intellij.psi.*

object GraphParser {

    /**
     * Resolve the Mermaid graph definition from the given PsiClass
     */
    fun parse(psiClass: PsiClass): String {
        val builder = MermaidGraphBuilder()

        handleGraphComposer(builder, psiClass)

        var hasContent = false

        for (field in psiClass.fields) {
            if (field.hasAnnotation(Annotations.GRAPH_NODE)) {
                val annotation = field.getAnnotation(Annotations.GRAPH_NODE)!!
                handleGraphNode(builder, field, annotation)
                hasContent = true
            }
            else if (field.hasAnnotation(Annotations.CONDITIONAL_EDGE)) {
                val annotation = field.getAnnotation(Annotations.CONDITIONAL_EDGE)!!
                handleConditionalEdge(builder, annotation)
                hasContent = true
            }
        }

        if (isDynamicBuilder(psiClass)) {
            builder.addDynamicBuildNote()
            hasContent = true
        }

        if (!hasContent) {
            builder.setEmptyState()
        }

        return builder.build()
    }

    private fun handleGraphComposer(builder: MermaidGraphBuilder, psiClass: PsiClass) {
        if (!psiClass.hasAnnotation(Annotations.GRAPH_COMPOSER)) {
            builder.addGraphTitle(psiClass.name ?: "Graph Flow")
            return
        }

        val annotation = psiClass.getAnnotation(Annotations.GRAPH_COMPOSER)!!

        val id = getAnnotationValue(annotation, "id")
        val description = getAnnotationValue(annotation, "description")

        val title = if (!id.isNullOrBlank()) id else (psiClass.name ?: "Graph Flow")
        builder.addGraphTitle(title)

        if (!description.isNullOrBlank()) {
            builder.addGraphDescription(description)
        }
    }

    private fun handleGraphNode(builder: MermaidGraphBuilder, field: PsiField, annotation: PsiAnnotation) {
        val idAttr = getAnnotationValue(annotation, "id")
        val nodeId = if (idAttr.isNullOrBlank()) field.name else idAttr

        val desc = getAnnotationValue(annotation, "description")

        val label = nodeId

        builder.addNode(id = nodeId, label = label, description = desc)

        val isStart = getAnnotationValue(annotation, "isStart")
        if (isStart == "true") {
            builder.linkStartTo(nodeId)
        }

        val nextIds = getAnnotationStringArray(annotation, "next")

        for (nextId in nextIds) {
            if (nextId.isNotBlank() && nextId != "null") {
                builder.addEdge(nodeId, nextId)
            }
        }
    }

    private fun handleConditionalEdge(builder: MermaidGraphBuilder, annotation: PsiAnnotation) {
        val sourceId = getAnnotationValue(annotation, "source")
        if (sourceId.isNullOrBlank()) return

        val mappings = getAnnotationStringArray(annotation, "mappings")
        if (mappings.isNotEmpty()) {
            for (i in mappings.indices step 2) {
                if (i + 1 < mappings.size) {
                    val targetNode = mappings[i + 1]

                    builder.addConditionalEdge(sourceId, targetNode)
                }
            }
        }

        val routes = getAnnotationStringArray(annotation, "routes")
        if (routes.isNotEmpty()) {
            for (routeTarget in routes) {
                builder.addConditionalEdge(sourceId, routeTarget)
            }
        }
    }


    private fun isDynamicBuilder(psiClass: PsiClass): Boolean {
        return psiClass.implementsListTypes.any { type ->
            type.className == "GraphBuildLifecycle"
        }
    }

    private fun getAnnotationValue(annotation: PsiAnnotation, attributeName: String): String? {
        val valueAttr = annotation.findAttributeValue(attributeName) ?: return null
        return computeConstant(valueAttr, annotation.project)
    }

    private fun getAnnotationStringArray(annotation: PsiAnnotation, attributeName: String): List<String> {
        val valueAttr = annotation.findAttributeValue(attributeName) ?: return emptyList()

        if (valueAttr is PsiArrayInitializerMemberValue) {
            return valueAttr.initializers.mapNotNull { expression ->
                computeConstant(expression, annotation.project)
            }
        }

        val singleVal = computeConstant(valueAttr, annotation.project)
        return if (singleVal != null) listOf(singleVal) else emptyList()
    }

    private fun computeConstant(expression: PsiElement, project: Project): String? {
        val constantHelper = JavaPsiFacade.getInstance(project).constantEvaluationHelper
        val result = constantHelper.computeConstantExpression(expression)
        return result?.toString()
    }
}