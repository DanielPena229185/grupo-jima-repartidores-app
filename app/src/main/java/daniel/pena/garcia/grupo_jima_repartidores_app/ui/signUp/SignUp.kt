package daniel.pena.garcia.grupo_jima_repartidores_app.ui.signUp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import daniel.pena.garcia.grupo_jima_repartidores_app.R

class SignUp : AppCompatActivity() {

    lateinit var auth: FirebaseAuth;
    lateinit var btn_sign_up: Button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.auth = Firebase.auth;
        this.btn_sign_up = findViewById(R.id.btn_first_sign_up);

        this.btn_sign_up.setOnClickListener {
            signUp();
        }
    }

    private fun signUp(){
        val etEmail = findViewById<EditText>(R.id.et_signup_email);
        val etPassword = findViewById<EditText>(R.id.et_signup_password);
        val email = etEmail.text.toString();
        val password = etPassword.text.toString();
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d("Successful", "createUserWithEmail:success")
                    print(task.result.user);
                }else{
                    // If sign in fails, display a message to the user.
                    Log.w("Error", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                baseContext,
                "Ningún campo debe estar vacío",
                Toast.LENGTH_SHORT).show();
        }
    }
}