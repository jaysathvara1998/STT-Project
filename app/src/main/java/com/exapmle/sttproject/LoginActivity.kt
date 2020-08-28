package com.exapmle.sttproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            if (validate()) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validate(): Boolean {
        if (etUserName.text.toString().isEmpty()) {
            etUserName.error = "Enter Username"
            return false
        }

        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Enter Password"
            return false
        }

        if (!etUserName.text.toString().equals("test", false) && !etPassword.text.toString()
                .equals("test", false)
        ) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}