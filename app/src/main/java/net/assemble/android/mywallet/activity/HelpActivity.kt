package net.assemble.android.mywallet.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.webkit.WebView
import android.webkit.WebViewClient
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.mywallet.R

class HelpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.help_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        val webView = findViewById<WebView>(R.id.webView)!!
        webView.webViewClient = WebViewClient()
        webView.loadUrl(URL)
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    companion object {
        private const val URL = "https://yh1224.gitbooks.io/mywallet/"
    }
}
