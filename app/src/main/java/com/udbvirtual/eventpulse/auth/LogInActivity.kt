package com.udbvirtual.eventpulse.auth


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udbvirtual.eventpulse.R
import android.content.Intent
import android.util.Log
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.udbvirtual.eventpulse.management.MyEventsActivity
import com.udbvirtual.eventpulse.management.UpcomingEventsActivity


class LoginActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Usuario ya autenticado, ir a la pantalla principal
            goToMainActivity()
        }

        // Inicializar Firebase Auth
        firebaseAuth = Firebase.auth

        // Configurar el botón de inicio de sesión con correo
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Mostrar mensajes de error si los campos están vacíos
                if (email.isEmpty()) {
                    emailEditText.error = "Por favor, ingrese su correo electrónico."
                }
                if (password.isEmpty()) {
                    passwordEditText.error = "Por favor, ingrese su contraseña."
                }
            } else {
                // Si ambos campos tienen datos, proceder con el inicio de sesión
                signInWithEmail(email, password)
            }
        }

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Añade tu client_id aquí
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el botón de inicio de sesión de Google
        val signInButton: SignInButton = findViewById(R.id.sign_in)
        signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Botón para ir a SignUpActivity
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "signInWithEmail:success, User: ${user?.email}")
                    goToMainActivity()
                } else {
                    // Falló el inicio de sesión, manejar errores específicos
                    val exception = task.exception
                    if (exception != null) {
                        handleSignInError(exception)
                    } else {
                        Log.w(TAG, "Error desconocido al iniciar sesión.")
                    }
                }
            }
    }

    private fun handleSignInError(exception: Exception) {
        val errorMessage: String = when (exception) {
            is FirebaseAuthInvalidUserException -> {
                // Usuario no existe
                "No se encontró una cuenta con este correo. Por favor, regístrese."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Contraseña incorrecta o correo mal formateado
                if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                    "La contraseña es incorrecta. Por favor, inténtelo de nuevo."
                } else if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                    "El formato del correo electrónico es inválido."
                } else {
                    "Este usuario no existe. Por favor, verifique sus datos."
                }
            }
            is FirebaseAuthUserCollisionException -> {
                // Usuario ya registrado
                "Este correo ya está registrado. Por favor, inicie sesión."
            }
            else -> {
                // Otros errores
                "Error al iniciar sesión: ${exception.localizedMessage}"
            }
        }

        Log.w(TAG, "Error de inicio de sesión: $errorMessage")
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    // Manejar el resultado del inicio de sesión de Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Iniciar sesión con el token de Google
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // Autenticar con Firebase usando el ID Token de Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Obtener el userId de FirebaseAuth
                    val userId = firebaseAuth.currentUser?.uid
                    val user = mapOf(
                        "firstName" to (firebaseAuth.currentUser?.displayName ?: "Sin nombre"),
                        "email" to (firebaseAuth.currentUser?.email ?: ""),
                        "role" to "user" // Por defecto, se asigna el rol de usuario
                    )

                    // Guardar o actualizar el usuario en Firestore
                    userId?.let {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(it).get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    // Solo guardar si el usuario no existe
                                    db.collection("users").document(it).set(user)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Usuario guardado correctamente en Firestore.")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error al guardar usuario en Firestore", e)
                                        }
                                } else {
                                    Log.d(TAG, "Usuario ya existe en Firestore.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al verificar usuario en Firestore", e)
                            }
                    }

                    // Redirigir a la pantalla principal
                    goToMainActivity()
                } else {
                    // Manejar errores en la autenticación
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Error al iniciar sesión con Google.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Redirigir a la actividad principal después de iniciar sesión
     private fun goToMainActivity() {
        val intent = Intent(this, UpcomingEventsActivity::class.java)
        startActivity(intent)
        finish() // Termina la actividad de Login
    }
}