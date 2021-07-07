package com.payu.otp_assist_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    var etAmount :EditText?= null
    var etKey :EditText?= null
    var etSalt :EditText?= null
    var etEmail :EditText?= null
    var btnPay :Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etAmount = findViewById(R.id.editTextAmount)
        etKey = findViewById(R.id.editTextMerchantKey)
        etSalt = findViewById(R.id.editTextMerchantSalt)
        etEmail = findViewById(R.id.editTextEmail)
        btnPay = findViewById(R.id.btnPayNow)

    }

    fun navigateToBaseActivity(view: View){
        val intent = Intent(this, ViewPagerActivity::class.java)
        intent.putExtra("key",etKey?.text.toString())
        intent.putExtra("salt",etSalt?.text.toString())
        intent.putExtra("amount",etAmount?.text.toString())
        intent.putExtra("email",etEmail?.text.toString())
        startActivity(intent)
    }
}