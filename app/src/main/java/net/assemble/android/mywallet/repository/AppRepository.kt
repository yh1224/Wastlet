package net.assemble.android.mywallet.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * DB処理基底クラス
 */
open class AppRepository(
        private val firebaseAuth: FirebaseAuth,
        private val firebaseFirestore: FirebaseFirestore
) {
    /** ログインユーザID */
    private val userId
        get() = firebaseAuth.currentUser?.uid!!

    /**
     * ユーザの document を取得
     */
    private fun usersCollection() = firebaseFirestore.collection("users")

    /**
     * アイテムの collection を取得
     */
    fun itemsRef() = usersCollection()
            .document(userId).collection("items")
}
