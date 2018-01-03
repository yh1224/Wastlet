package net.assemble.android.mywallet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.mywallet.R

class LoginActivity : BaseActivity() {
    // Instances injected by Kodein
    private val firebaseAuth: FirebaseAuth by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth.addAuthStateListener {
            Log.d(TAG, "addAuthStateListener: $firebaseAuth")
            if (firebaseAuth.currentUser != null) {
                setResult(RESULT_OK)
                finish()
            }
        }

        requestSignInByGoogle()
    }

    /**
     * Google ログイン要求
     */
    private fun requestSignInByGoogle() {
        // Initialize Google API Client
        val googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { connectionResult ->
                    Log.w(TAG, "Google Play Services error: " + connectionResult.errorMessage)
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
