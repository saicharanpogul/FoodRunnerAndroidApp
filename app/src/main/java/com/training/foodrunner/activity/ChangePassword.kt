package com.training.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ChangePassword : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmNewPassword: EditText
    lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etOtp = findViewById(R.id.etOtp)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        val mobileNumber = intent.getStringExtra("mobileNumber")

        btnChangePassword.setOnClickListener {

            if (etOtp.text.toString().length == 4) {

                if (etNewPassword.text.toString().length >= 4) {

                    if (etNewPassword.text.toString() == etConfirmNewPassword.text.toString()) {

                        val queue = Volley.newRequestQueue(this@ChangePassword)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                        val otp = etOtp.text.toString()
                        val password = etNewPassword.text.toString()

                        val jsonParam = JSONObject()
                        jsonParam.put("mobile_number", mobileNumber)
                        jsonParam.put("password", password)
                        jsonParam.put("otp", otp)

                        println(mobileNumber)

                        if (ConnectionManager().checkConnectivity(this@ChangePassword)) {

                            val jsonObjectRequest = object :
                                JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonParam,
                                    Response.Listener {

                                        try {

                                            val dataObject = it.getJSONObject("data")
                                            val success = dataObject.getBoolean("success")

                                            if (success) {
                                                val message = dataObject.getString("successMessage")
                                                val dialog =
                                                    AlertDialog.Builder(this@ChangePassword)
                                                dialog.setTitle("Success")
                                                dialog.setMessage(message)
                                                dialog.setPositiveButton("Login") { dialogInterface, which ->
                                                    val loginIntent =
                                                        Intent(
                                                            this@ChangePassword,
                                                            LoginActivity::class.java
                                                        )
                                                    startActivity(loginIntent)
                                                }
                                                dialog.setNegativeButton("Exit") { dialogInterface, which ->
                                                    ActivityCompat.finishAffinity(this@ChangePassword)
                                                }
                                                dialog.create()
                                                dialog.show()
                                            } else {
                                                val error = dataObject.getString("errorMessage")
                                                Toast.makeText(
                                                    this@ChangePassword,
                                                    error,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@ChangePassword,
                                                "Some UnExpected Error Occurred!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    },
                                    Response.ErrorListener {
                                        Toast.makeText(
                                            this@ChangePassword,
                                            "Volley Error Occurred!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["content-type"] = "application/json"
                                    headers["token"] = "############"
                                    return headers
                                }
                            }
                            queue.add(jsonObjectRequest)
                        } else {
                            val dialog = AlertDialog.Builder(this@ChangePassword)
                            dialog.setTitle("Failed")
                            dialog.setMessage("Internet Connection Not Found")
                            dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                startActivity(settingsIntent)
                                finish()
                            }
                            dialog.setNegativeButton("Exit") { dialogInterface, which ->
                                ActivityCompat.finishAffinity(this@ChangePassword)
                            }
                            dialog.create()
                            dialog.show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePassword,
                            "New Password & Confirm Password doesn't match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ChangePassword,
                        "Password should have at least 5 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@ChangePassword, "OTP should be of 4 digits", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }
}
