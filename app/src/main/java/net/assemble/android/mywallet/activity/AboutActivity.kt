package net.assemble.android.mywallet.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import com.github.salomonbrys.kodein.instance
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.helper.PackageInfoHelper

class AboutActivity : BaseActivity() {
    // Instances injected by Kodein
    private val packageInfoHelper: PackageInfoHelper by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.about_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        // バージョン
        val version = findViewById<TextView>(R.id.version)!!
        version.text = getString(R.string.version, packageInfoHelper.getVersion())

        // ライセンス
        val webView = findViewById<WebView>(R.id.webView)!!
        webView.webViewClient = object : WebViewClient() {
            @Suppress("OverridingDeprecatedMember")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                startActivity(Intent(Intent.ACTION_VIEW, request.url))

                return true
            }
        }
        webView.loadUrl(LICENSES_URL)
        webView.setBackgroundColor(Color.TRANSPARENT)
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true

        // Feedback
        val feedbackButton = findViewById<Button>(R.id.support)
        feedbackButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    companion object {
        @Suppress("unused")
        private val TAG = AboutActivity::class.java.simpleName

        private const val LICENSES_URL = "file:///android_asset/licenses.html"
    }
}
