package com.keremsen.wordmaster.viewmodel


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(private val context: Context) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null
    private val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    private val _currentUser = MutableLiveData(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _signInIntent = MutableLiveData<Intent>()
    val signInIntent: LiveData<Intent> = _signInIntent

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    private fun setupFirestoreListener(uid: String) {
        firestoreListener?.remove() // Eski listener'ı temizle
        firestoreListener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                _userData.value = snapshot?.toObject(User::class.java)
            }
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            firebaseAuth.currentUser?.uid?.let { uid ->
                setupFirestoreListener(uid) // Kullanıcı varsa verileri çek
            }
        }
    }

    fun updateUserLevel(newLevel: Int) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _error.value = "Kullanıcı girişi yok!"
            return
        }
        val updatedUser = _userData.value?.copy(level = newLevel)
        _userData.value = updatedUser

        // Firestore'daki 'users' koleksiyonunda güncelleme yap
        db.collection("users")
            .document(currentUser.uid)
            .update("level", newLevel)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Level güncellendi: $newLevel")
                // Not: Realtime listener otomatik olarak _userData'yı güncelleyecek
            }
            .addOnFailureListener { e ->
                _error.value = "Level güncellenemedi: ${e.message}"
                Log.e("AuthViewModel", "Firestore update error", e)
            }
    }
    /**
     * Level'ı 1 artırır
     */
    fun increaseLevel() {
        val currentLevel = _userData.value?.level ?: 1
        updateUserLevel(currentLevel + 1)
    }

    fun addUserToFirestore(user: User) {
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "User added successfully")
            }
            .addOnFailureListener { e ->
                _error.value = "Kullanıcı eklenemedi: ${e.message}"
                Log.e("AuthViewModel", "Error adding user", e)
            }
    }

    fun prepareGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        _signInIntent.value = googleSignInClient.signInIntent
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                val firebaseUser = auth.currentUser

                if (firebaseUser != null) {
                    _currentUser.value = firebaseUser

                    // Kullanıcı Firestore'da zaten var mı?
                    val userRef = db.collection("users").document(firebaseUser.uid)
                    val snapshot = userRef.get().await()

                    if (!snapshot.exists()) {
                        // SharedPreferences'dan level değerini al
                        val savedLevel = sharedPreferences.getInt("user_level", 1)
                        
                        // Kullanıcı yoksa Firestore'a ekle
                        val user = User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "Bilinmiyor",
                            name = firebaseUser.displayName ?: "Bilinmiyor",
                            createdAt = System.currentTimeMillis().toString(),
                            level = savedLevel,  // SharedPreferences'dan alınan level değerini kullan
                            isAdmin = false
                        )
                        userRef.set(user).await()
                        _userData.value = user  // Yeni kullanıcıyı UI'ye gönder
                    } else {
                        // Kullanıcı varsa verilerini yükle
                        val existingUser = snapshot.toObject(User::class.java)
                        _userData.value = existingUser
                    }

                    _error.value = null  // Hata yoksa temizle
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Giriş başarısız"
            }
        }
    }
    suspend fun signOutFromGoogle(context: Context) {
        try {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut().await()

            // Yerel oturum bilgilerini de temizle
            googleSignInClient.revokeAccess().await()
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Google sign out failed", e)
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            try {
                signOutFromGoogle(context)  // Önce Google oturumunu kapat
                auth.signOut()  // Sonra Firebase oturumunu kapat
                _currentUser.value = null
                _userData.value = null  // UserData'yı da sıfırla
                _error.value = null  // Hataları temizle
            } catch (e: Exception) {
                _error.value = "Çıkış yapılırken hata oluştu: ${e.message}"
                Log.e("AuthViewModel", "Sign out failed", e)
            }
        }
    }

    fun setError(message: String) {
        _error.value = message
    }
}