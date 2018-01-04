package net.assemble.android.mywallet.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import net.assemble.android.mywallet.BuildConfig
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*

class PackageInfoHelper(private val context: Context) {
    /**
     * バージョンを取得
     *
     * @return バージョン (公開用署名でない場合は "SNAPSHOT")
     */
    fun getVersion(): String =
            if (APP_SHA1_FINGERPRINTS.contains(getCertificateSHA1Fingerprint())) {
                BuildConfig.VERSION_NAME
            } else {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val date = SimpleDateFormat("yyyyMMdd", Locale.US).format(packageInfo.lastUpdateTime)
                "${BuildConfig.VERSION_NAME} (SNAPSHOT-$date)"
            }

    /**
     * 証明書フィンガープリントを取得
     *
     * @return 証明書フィンガープリント
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun getCertificateSHA1Fingerprint(): String {
        val packageInfo = context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNATURES)
        val cert = CertificateFactory.getInstance("X509").generateCertificate(
                ByteArrayInputStream(packageInfo.signatures[0].toByteArray())) as X509Certificate
        val md = MessageDigest.getInstance("SHA1")
        return md.digest(cert.encoded).joinToString(":") { "%02X".format(it.toInt() and 0xff) }
    }

    companion object {
        /** 公開用署名フィンガープリント */
        private val APP_SHA1_FINGERPRINTS = listOf(
                "68:E8:AF:A0:A5:69:E5:F5:82:90:A0:85:EF:94:DD:9D:66:87:57:35"
        )
    }
}
