package com.kyrie.utility.utility

import android.content.Context
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.core.text.HtmlCompat
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.Node


@Suppress("SpellCheckingInspection")
fun Context.setMarkdown(text: String, targetView: TextView) {
    val markwon: Markwon = Markwon.create(this)
    val node: Node = markwon.parse(text)
    val markdown = markwon.render(node)
    markwon.setParsedMarkdown(targetView, markdown)
}

fun TextView.setHtmlText(contentString: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        HtmlCompat.fromHtml(contentString, HtmlCompat.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(contentString)
    }
}

fun Context.setMarkdownWithBullet(text: String, targetView: TextView) {
    val markwon: Markwon = Markwon.builder(this).apply {
        usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) {
                builder.bulletWidth(6) // pixels
            }
        })
    }.build()
    val node: Node = markwon.parse(text)
    val markdown = markwon.render(node)
    markwon.setParsedMarkdown(targetView, markdown)

}

fun String?.containsMarkdown(): Boolean {
    // Regular expressions for common Markdown patterns
    if (isNullOrEmpty()) return false
    val markdownPatterns = listOf(
        Regex("""^#{1,6}\s"""),            // Headers
        Regex("""\*\*.*\*\*"""),           // Bold
        Regex("""\*.*\*"""),               // Italic
        Regex("""\[(.*?)\]\((.*?)\)"""),   // Links
        Regex("""\*\s+"""),                // Unordered lists
        Regex("""\d+\.\s+""")              // Ordered lists
    )
    return markdownPatterns.any { it.containsMatchIn(this) }
}