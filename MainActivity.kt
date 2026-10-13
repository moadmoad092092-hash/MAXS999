package com.appify.webtoapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var errorPanel: LinearLayout
    private lateinit var errorText: TextView
    private var siteUrl = "https://example.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = readConfig()
        siteUrl = config.optString("siteUrl", siteUrl)
        val appName = config.optString("appName", "APPIFY")
        val themeColor = parseColor(config.optString("themeColor", "#19C37D"))
        title = appName
        setContentView(buildScreen(appName, themeColor))
        configureWebView()
        loadSite()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })
    }

    private fun buildScreen(appName: String, themeColor: Int): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val toolbar = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(18, 12, 18, 12)
            setBackgroundColor(Color.rgb(11, 18, 15))
        }
        val icon = TextView(this).apply {
            text = "A"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(11, 18, 15))
            setBackgroundColor(themeColor)
        }
        toolbar.addView(icon, LinearLayout.LayoutParams(42, 42))
        val label = TextView(this).apply {
            text = appName
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(12, 0, 0, 0)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        toolbar.addView(label, LinearLayout.LayoutParams(0, 48, 1f))
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
            progressTintList = android.content.res.ColorStateList.valueOf(themeColor)
        }
        webView = WebView(this)
        errorPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
            visibility = View.GONE
        }
        errorText = TextView(this).apply {
            text = getString(com.appify.webtoapp.R.string.load_error)
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(11, 18, 15))
        }
        val retry = Button(this).apply {
            text = getString(com.appify.webtoapp.R.string.retry)
            setTextColor(Color.WHITE)
            setBackgroundColor(themeColor)
            setOnClickListener { loadSite() }
        }
        errorPanel.addView(errorText, LinearLayout.LayoutParams(-1, -2))
        errorPanel.addView(retry, LinearLayout.LayoutParams(-2, -2).apply { topMargin = 20 })
        root.addView(toolbar, LinearLayout.LayoutParams(-1, 68))
        root.addView(progress, LinearLayout.LayoutParams(-1, 4))
        root.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(errorPanel, LinearLayout.LayoutParams(-1, 0, 1f))
        return root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            allowFileAccess = false
            allowContentAccess = false
            builtInZoomControls = false
            displayZoomControls = false
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return request.url.scheme != "https"
            }
            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                errorPanel.visibility = View.GONE
                webView.visibility = View.VISIBLE
                progress.visibility = View.VISIBLE
            }
            override fun onPageFinished(view: WebView, url: String) { progress.visibility = View.GONE }
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) showError(if (hasInternet()) getString(R.string.load_error) else getString(R.string.offline_message))
            }
            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                if (request.isForMainFrame && errorResponse.statusCode >= 400) showError("تعذر فتح الصفحة (HTTP ${errorResponse.statusCode}).")
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
    }

    private fun loadSite() {
        val normalized = try { URL(siteUrl).toURI().toURL().toString() } catch (_: Exception) { "" }
        if (!normalized.startsWith("https://")) { showError("الرابط غير صالح. يجب أن يبدأ بـ https://"); return }
        errorPanel.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.loadUrl(normalized)
    }

    private fun showError(message: String) {
        progress.visibility = View.GONE
        webView.visibility = View.GONE
        errorText.text = message
        errorPanel.visibility = View.VISIBLE
    }

    private fun hasInternet(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return false
        val network = cm.activeNetwork ?: return false
        return cm.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun readConfig(): JSONObject = try {
        assets.open("app-config.json").bufferedReader().use { JSONObject(it.readText()) }
    } catch (_: Exception) { JSONObject() }

    private fun parseColor(value: String): Int = try { Color.parseColor(value) } catch (_: Exception) { Color.rgb(25, 195, 125) }
}
