package net.sharplab.translator.core.processor

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class LinkTagPostProcessor : TagPostProcessor{

    override fun postProcess(message: Element) {
        replaceLink(message)
    }

    private fun replaceLink(element: Element) {
        if(element.tagName() == "a"){
            if(element.attr("class") == "bare"){
                val url = element.attr("href")
                val linkText = " %s ".format(url)
                element.replaceWith(TextNode(linkText))
            }
            else {
                val url = element.attr("href")
                val text = element.text()
                val attrs = element.attributes().filterNot { attr -> attr.key == "href" }.filterNot { attr -> attr.key == "rel" && attr.value == "noopener" }
                val attrsText: String = attrs.joinToString(separator = ", ")
                var linkText = when {
                    attrs.isEmpty() -> "link:%s[%s]".format(url, text)
                    else -> "link:%s[%s, %s]".format(url, text, attrsText)
                }

                val prev : Node? = element.previousSibling()
                val next : Node? = element.nextSibling()
                val isPrevExists = prev != null
                val isNextExists = next != null
                val isPrevSpaced= prev is TextNode && prev.text().endsWith(" ")
                val isNextSpaced= next is TextNode && next.text().startsWith(" ")

                if(isPrevExists && !isPrevSpaced){
                    linkText = " $linkText"
                }
                if(isNextExists && !isNextSpaced){
                    linkText = "$linkText "
                }
                element.replaceWith(TextNode(linkText))
            }
        }
        element.children().forEach(this::replaceLink)
    }

    private fun mapAttrKey(key: String): String{
        return when(key){
            "class" -> "role"
            "target" -> "window"
            else -> key
        }
    }

}