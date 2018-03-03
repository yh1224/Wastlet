package net.assemble.android.mywallet.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.Toast
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.assemble.android.common.extensions.toObservable
import net.assemble.android.common.fragment.AlertDialogFragment
import net.assemble.android.mywallet.R
import timber.log.Timber

class LoginActivity : BaseActivity() {
    // Instances injected by Kodein
    private val firebaseAuth: FirebaseAuth by instance()

    /** Disposable container for RxJava */
    private val disposables = CompositeDisposable()

    /** Google API Client */
    private lateinit var googleApiClient: GoogleApiClient

    /** Firebase AuthStateListener */
    private val authStateListener = FirebaseAuth.AuthStateListener {
        if (firebaseAuth.currentUser != null) {
            Toast.makeText(this, getString(R.string.welcome_user, firebaseAuth.currentUser?.displayName), Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)

        firebaseAuth.addAuthStateListener(authStateListener)

        // Initialize Google API Client
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { connectionResult ->
                    Timber.e("Google Play services error: ${connectionResult.errorMessage}")
                }
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build())
                .build()

        findViewById<Button>(R.id.sign_in_google).setOnClickListener {
            requestSignInByGoogle()
        }
    }

    /**
     * Google ログイン要求
     */
    private fun requestSignInByGoogle() {
        // Initialize Google API Client
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
                        .toObservable()
                        .subscribe({}, { t ->
                            if (t is FirebaseNetworkException) {
                                Timber.e("signInWithCredential failed: $t")
                                showErrorMessage(getString(R.string.error_login_failed, t.message))

                            }
                        })
                        .addTo(disposables)
            } else {
                if (result.status.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    finish()
                } else {
                    Timber.e("Google sign-in failed: ${result.status}")
                    showErrorMessage(getString(R.string.error_login_failed, GoogleSignInStatusCodes.getStatusCodeString(result.status.statusCode)))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    /**
     * エラーメッセージを表示
     *
     * @param message メッセージ
     */
    private fun showErrorMessage(message: String) {
        AlertDialogFragment.newInstance(
                getString(R.string.login),
                message,
                getString(R.string.ok)
        ).show(supportFragmentManager, ALERT_DIALOG_TAG)
    }

    companion object {
        private const val REQUEST_SIGN_IN_GOOGLE = 1

        private const val ALERT_DIALOG_TAG = "alert"
    }
}
