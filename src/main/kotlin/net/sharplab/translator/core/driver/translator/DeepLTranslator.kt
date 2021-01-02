package net.sharplab.translator.core.driver.translator

import org.eclipse.microprofile.rest.client.RestClientBuilder
import org.jboss.resteasy.specimpl.MultivaluedMapImpl
import java.net.URI
import javax.enterprise.context.Dependent
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MultivaluedMap

class DeepLTranslator(private val apiKey: String) : Translator {
    override fun translate(texts: List<String>, srcLang: String, dstLang: String): List<String> {
        if (texts.isEmpty()) {
            return emptyList()
        }
        val map: MultivaluedMap<String, String> = MultivaluedMapImpl()
        map.add("auth_key", apiKey)
        map.add("source_lang", srcLang)
        map.add("target_lang", dstLang)
        map.add("non_splitting_tags", java.lang.String.join(",", INLINE_ELEMENT_NAMES))
        map.add("ignore_tags", java.lang.String.join(",", IGNORE_ELEMENT_NAMES))
        map.add("tag_handling", "xml")
        for (text in texts) {
            map.add("text", text)
        }
        val response: DeepLTranslateAPIResponse
        try {
            val deepLClient = RestClientBuilder.newBuilder().baseUri(URI.create("https://api.deepl.com")).build(DeepLClient::class.java)
            response = deepLClient.translate(map)
        } catch (e: WebApplicationException) {
            val message = String.format("%d error is thrown: %s", e.response.status, e.response.readEntity(String::class.java))
            throw DeepLTranslatorException(message, e)
        }
        return response.translations.map(DeepLTranslateAPIResponse.Translation::text)
    }

    companion object {
        private val IGNORE_ELEMENT_NAMES = java.util.List.of("abbr", "b", "cite", "code", "data", "dfn", "kbd", "rp", "rt", "rtc", "ruby", "samp", "time", "var")

        /**
         * インライン要素のタグリスト
         */
        val INLINE_ELEMENT_NAMES = listOf("a", "abbr", "b", "bdi", "bdo", "br", "cite", "code", "data", "dfn", "em", "i", "kbd", "mark", "q", "rp", "rt", "rtc", "ruby", "s", "samp", "small", "span", "strong", "sub", "sup", "time", "u", "var", "wbr")
        /**
         * 翻訳除外要素のリスト
         */
        val EXCLUDED_ELEMENT_NAMES = listOf("head", "pre", "tt")
    }

}