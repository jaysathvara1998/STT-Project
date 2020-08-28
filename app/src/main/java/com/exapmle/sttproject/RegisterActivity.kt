package com.exapmle.sttproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.etPassword
import kotlinx.android.synthetic.main.activity_login.etUserName
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener {
            if (validate()) {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Registration Success", Toast.LENGTH_LONG).show()
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validate(): Boolean {
        if (etName.text.toString().isEmpty()) {
            etName.error = "Enter Name"
            return false
        }

        if (etUserName.text.toString().isEmpty()) {
            etUserName.error = "Enter UserName"
            return false
        }

        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Enter Password"
            return false
        }

        return true
    }
}