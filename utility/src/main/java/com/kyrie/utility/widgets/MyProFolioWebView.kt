package com.kyrie.utility.widgets

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.net.http.SslError
import android.os.CountDownTimer
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kyrie.utility.utility.isHtmlText
import com.kyrie.utility.utility.isHttpLink
import com.kyrie.utility.utility.isPlayStoreLink
import com.kyrie.utility.utility.isValidUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
class MyProFolioWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    companion object {
        private const val INTERNET_CONNECTION_WEBVIEW_ERROR_CD = "net::ERR_INTERNET_DISCONNECTED"
        private const val UNKNOWN_URL_SCHEME_WEBVIEW_ERROR_CD = "net::ERR_UNKNOWN_URL_SCHEME"
        private const val INTERVAL_IN_MILLIS = 1000L
        private const val REQUEST_DENIED = 999
        private const val KEY_CONNECTION_TIMEOUT = "Connection timeout"
        private const val KEY_INVALID_URL = "Invalid Url"
        private const val KEY_JSON_ERROR = "json error"
    }

    /**
     * @return {@code true} if the loading process is handled, otherwise return {@code false}.
     */
    private var onUrlLoading: ((Uri?) -> Boolean)? = null
    private var onPageLoaded: ((WebView?) -> Unit)? = null
    private var onShowFileChooser: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    private var onErrorReceived: ((String, Boolean) -> Unit)? = null
    private var onPlayStoreAppRedirect: ((String) -> Boolean)? = null

    private val _tmnWebViewState = MutableStateFlow(State.START)
    val tmnWebViewState: StateFlow<State> = _tmnWebViewState

    private var timer: CountDownTimer? = null

    private var atLeastOnePageSuccess = false

    enum class State {
        START,
        LOADING,
        LOADED
    }

    init {
        settings.javaScriptEnabled = true
        settings.setSupportMultipleWindows(true)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        webViewClient = TrueMoneyWebViewClient(context)
        webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                // Standard multi windows logic
                val childView = WebView(view!!.context)
                val settings = childView.settings
                settings.javaScriptEnabled = true
                childView.webChromeClient = this
                childView.settings.javaScriptCanOpenWindowsAutomatically = true
                val transport = resultMsg!!.obj as WebViewTransport
                transport.webView = childView
                resultMsg.sendToTarget()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                onShowFileChooser?.invoke(filePathCallback)
                return true
            }
        }
    }

    fun setBuiltInZoomControl() {
        settings.builtInZoomControls = true
    }

    fun setOnUrlLoading(onUrlLoading: (Uri?) -> Boolean) {
        this.onUrlLoading = onUrlLoading
    }

    fun setOnPageLoaded(onPageLoaded: (WebView?) -> Unit) {
        this.onPageLoaded = onPageLoaded
    }

    fun setOnErrorReceived(onErrorReceived: (String, Boolean) -> Unit) {
        this.onErrorReceived = onErrorReceived
    }

    fun setOnShowFileChooser(onShowFileChooser: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.onShowFileChooser = onShowFileChooser
    }

    fun setOnPlayStoreAppLoaded(onPlayStoreAppRedirect: (String) -> Boolean) {
        this.onPlayStoreAppRedirect = onPlayStoreAppRedirect
    }

    fun load(str: String) {
        when {
            str.isHttpLink() -> {
                loadUrl(str)
            }

            str.isHtmlText() -> {
                loadDataWithBaseURL("", str, "text/html", "UTF-8", "")
            }

            else -> {
                val html = "<html> <head><meta name=\"viewport\" content=\"width=device-width, " +
                        "user-scalable=0,initial-scale=1.0\"></head><body style=\"overflow-wrap: break-word; " +
                        "word-wrap: break-word; -ms-word-break: break-all; word-break: break-all; " +
                        "word-break: break-word; -ms-hyphens: auto; -moz-hyphens: auto; " +
                        "-webkit-hyphens: auto; hyphens: auto; \">" + str + "</body></html>"
                loadDataWithBaseURL("", html, "text/html", "UTF-8", "")
            }
        }
    }

    private var processedURL = ""
    override fun loadUrl(url: String) {
        val urlWithoutParam = extractUrlDomain(url)
        if (urlWithoutParam.isBlank() || !urlWithoutParam.isValidUrl()) {
            onErrorReceived?.invoke(KEY_INVALID_URL, shouldNavBackToHTML())
            return
        }

        processedURL = url
        super.loadUrl(url)
    }

    private fun extractUrlDomain(urlAfterReplace: String) = if (urlAfterReplace.contains("?")) {
        urlAfterReplace.split("?")[0]
    } else {
        urlAfterReplace
    }

    override fun destroy() {
        onUrlLoading = null
        onPageLoaded = null
        timer?.cancel()
        super.destroy()
    }

    fun shouldNavBackToHTML(): Boolean {
        return atLeastOnePageSuccess
    }

    inner class TrueMoneyWebViewClient(private val context: Context) : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            // Not override url load when it is redirection
            val host = request?.url?.host
            if (host != null) {
                if (host.isPlayStoreLink()) {
                    return onPlayStoreAppRedirect?.invoke(host)
                        ?: false// Indicates WebView handled the URL
                }
            }
            request?.let {
                val strUrl = it.url.toString()
                return processUrl(strUrl, view)
            }
            return true
        }

        private fun processUrl(strUrl: String, view: WebView?): Boolean {
            try {
                val replaceUri = Uri.parse(strUrl)
                processedURL = strUrl
                return when {
                    onUrlLoading?.invoke(replaceUri) == true -> {
                        true
                    }

                    else -> {
                        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                            _tmnWebViewState.emit(State.LOADING)
                        }
                        false
                    }
                }
            } catch (e: Exception) {
                return false
            }
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            //will enhance this later
            val builder = AlertDialog.Builder(view?.context)
            builder.setMessage("SSL Certificate error. Do you want to continue anyway?")
            builder.setPositiveButton("Continue") { _, _ -> handler?.proceed() }
            builder.setNegativeButton("Cancel") { _, _ -> handler?.cancel() }
            val dialog = builder.create()
            dialog.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                _tmnWebViewState.emit(State.LOADED)
            }
            atLeastOnePageSuccess = true
            onPageLoaded?.invoke(view)
            timer?.cancel()
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (error?.description?.toString() == INTERNET_CONNECTION_WEBVIEW_ERROR_CD ||
                error?.description?.toString() == UNKNOWN_URL_SCHEME_WEBVIEW_ERROR_CD ||
                view?.url == request?.url.toString()
            ) {
                error?.description?.toString()?.let {
                    onErrorReceived?.invoke(it, shouldNavBackToHTML())
                }
            }
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            val url = request?.url.toString()
            if (url == view?.url || url == processedURL) {
                if (errorResponse?.statusCode != REQUEST_DENIED) {
                    onErrorReceived?.invoke(KEY_JSON_ERROR, shouldNavBackToHTML())
                }
            }
        }


        /**
         *  Handle back press of web view
         * */
        fun onBackPress(onHandle: () -> Boolean = { false }): Boolean {
            return if (canGoBack()) {
                goBack()
                true
            } else {
                onHandle.invoke()
            }
        }

        /**
         * call this if you want to limit the duration of web view display time
         * */
        fun startPageSessionCountDown(millisInFuture: Long) {
            timer?.cancel()
            initCountTimer(millisInFuture)
            timer?.start()
        }

        private fun initCountTimer(millisInFuture: Long) {
            timer = object : CountDownTimer(millisInFuture, INTERVAL_IN_MILLIS) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    onErrorReceived?.invoke(KEY_CONNECTION_TIMEOUT, shouldNavBackToHTML())
                }
            }
        }

    }


}