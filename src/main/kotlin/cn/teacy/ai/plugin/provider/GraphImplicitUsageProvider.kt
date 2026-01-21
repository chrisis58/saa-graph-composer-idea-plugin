package cn.teacy.ai.plugin.provider

import cn.teacy.ai.plugin.Annotations
import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiModifierListOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField

class GraphImplicitUsageProvider : ImplicitUsageProvider {

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (element !is PsiModifierListOwner
            || element !is PsiField)
            return false

        return Annotations.ENTRY_POINTS.any { annotation ->
            element.hasAnnotation(annotation)
        }
    }

    override fun isImplicitRead(element: PsiElement) = isImplicitUsage(element)

    override fun isImplicitWrite(element: PsiElement) = isImplicitUsage(element)
}