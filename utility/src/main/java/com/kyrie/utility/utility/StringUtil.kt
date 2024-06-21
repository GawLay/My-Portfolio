package com.kyrie.utility.utility

import android.util.Patterns
import android.webkit.URLUtil
import com.kyrie.utility.constants.PlayStoreUrl
import java.util.regex.Pattern


const val HTTP_LINK_PREFIX = "http"
const val HTML_TEXT_REGEX = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>"

fun String.isHtmlText(): Boolean {
    return Pattern.compile(HTML_TEXT_REGEX).matcher(this).find()
}

fun String.isPlayStoreLink(): Boolean {
    return contains(PlayStoreUrl.MARKET_DETAIL.url) || contains(PlayStoreUrl.MARKET_DEV_DETAIL.url)
}

fun String.isHttpLink(): Boolean {
    return this.startsWith(HTTP_LINK_PREFIX)
}

fun String?.isValidUrl(): Boolean {
    return this?.let { url ->
        try {
            (URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches())
        } catch (e: Exception) {
            false
        }
    } ?: false
}