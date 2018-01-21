package net.assemble.android.mywallet.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.mywallet.R
import timber.log.Timber

class LoginActivity : BaseActivity() {
    // Instances injected by Kodein
    private val firebaseAuth: FirebaseAuth by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)

        firebaseAuth.addAuthStateListener {
            if (firebaseAuth.currentUser != null) {
                setResult(RESULT_OK)
                finish()
            }
        }

        findViewById<Button>(R.id.sign_in_google).setOnClickListener {
            requestSignInByGoogle()
        }
    }

    /**
     * Google ログイン要求
     */
    private fun requestSignInByGoogle() {
        // Initialize Google API Client
        val googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { connectionResult ->
                    Timber.w("Google Play Services error: " + connectionResult.errorMessage)
                }
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build())
                .build()
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), REQUEST_SIGN_IN_GOOGLE)
    }

    /**
     * ログイン処理完了
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGN_IN_GOOGLE) {
            // 認証結果
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential)
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    companion object {
        @Suppress("unused")
        private val TAG = LoginActivity::class.java.simpleName

        private const val REQUEST_SIGN_IN_GOOGLE = 1
    }
}
