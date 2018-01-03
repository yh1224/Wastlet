package net.assemble.android.mywallet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
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
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // 認証が完了したらメイン画面へ
                if (firebaseUser.providers?.isEmpty() == false) {
                    Toast.makeText(this, "Welcome, ${firebaseUser.displayName}", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
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
                    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
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
     * 新規サインイン
     */
    private fun signIn(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful)

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Logged in by Google Account.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "linkOrSignIn", task.exception)
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
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
                val acct = result.signInAccount!!
                val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                signIn(credential)
            } else {
                Toast.makeText(this, "Authentication Failed.\n" + result.status, Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        @Suppress("unused")
        private val TAG = LoginActivity::class.java.simpleName

        private const val REQUEST_SIGN_IN_GOOGLE = 1
    }
}
