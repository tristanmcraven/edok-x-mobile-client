package com.tristanmcraven.edokx

import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.thread

class RegActivity : AppCompatActivity() {

    private lateinit var editTextLastName: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
    }

    private fun initViews() {
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextName = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            if (isSomethingEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val success = sendEmail(editTextEmail.text.toString(), editTextName.text.toString(), editTextLastName.text.toString())
                if (success) {
                    Toast.makeText(this@RegActivity, "Успешная регистрация!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegActivity, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this@RegActivity, "FAIL!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isSomethingEmpty() = editTextLastName.text.isNullOrBlank() ||
            editTextName.text.isNullOrBlank() ||
            editTextPhone.text.isNullOrBlank() ||
            editTextEmail.text.isNullOrBlank() ||
            editTextLogin.text.isNullOrBlank() ||
            editTextPassword.text.isNullOrBlank()

    suspend fun sendEmail(toEmail: String, name: String, lastName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val username = "jamalsaydayev@yandex.ru"
            val password = "mcgzdifgelojoiik"

            val properties = Properties().apply {
                put("mail.smtp.host", "smtp.yandex.com")
                put("mail.smtp.socketFactory.port", "465")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.auth", "true")
                put("mail.smtp.port", "465")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })


            return@withContext try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(username, "Сервис Edok"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                    subject = "Подтверждение регистрации"
                    setText("Здравствуйте, $lastName $name! Вы зарегистрировались в Edok и теперь можете оформлять заказы через мобильное приложение.")
                }
                Transport.send(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}