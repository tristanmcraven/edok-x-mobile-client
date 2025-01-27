package com.tristanmcraven.edokx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tristanmcraven.edok.utility.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogIn: Button
    private lateinit var buttonSignUp: Button
    private lateinit var buttonContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogIn = findViewById(R.id.buttonLogIn)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonContinue = findViewById(R.id.buttonContinue)

        buttonLogIn.setOnClickListener {
            val login = editTextLogin.text.toString()
            val pass = editTextPassword.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val user = ApiClient.IUser.login(login, pass)
                withContext(Dispatchers.Main)
                {
                    if (user == null)
                        Toast.makeText(this@AuthActivity, "Неправильное имя пользователя или пароль!", Toast.LENGTH_SHORT).show()
                    else {
                        val intent = Intent(this@AuthActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}