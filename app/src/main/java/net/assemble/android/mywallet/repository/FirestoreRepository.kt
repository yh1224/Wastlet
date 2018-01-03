package net.assemble.android.mywallet.repository

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository(
        private val firebaseFirestore: FirebaseFirestore
) : FirestoreRepositoryInterface
