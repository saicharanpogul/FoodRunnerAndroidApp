package com.training.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgetPassword : AppCompatActivity() {

    lateinit var appLogo: ImageView
    lateinit var appName: TextView
    lateinit var text: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        window.enterTransition = null


        appLogo = findViewById(R.id.img_app_logo)
        appName = findViewById(R.id.txt_app_name)
        text = findViewById(R.id.txt_new_pass_change)
        etMobileNumber = findViewById(R.id.et_mobile_number_for_forget_password)
        etEmail = findViewById(R.id.et_email_forget)
        btnNext = findViewById(R.id.btn_next)

        val slideDown = AnimationUtils.loadAnimation(applicationContext,
            R.anim.slide_from_bottom
        )
        appLogo.startAnimation(slideDown)
        appName.startAnimation(slideDown)
        text.startAnimation(slideDown)
        etMobileNumber.startAnimation(slideDown)
        etEmail.startAnimation(slideDown)
        btnNext.startAnimation(slideDown)


        btnNext.setOnClickListener{

            val queue = Volley.newRequestQueue(this@ForgetPassword)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val mobileNumber = etMobileNumber.text.toString()
            val email = etEmail.text.toString()

            val jsonParam = JSONObject()
            jsonParam.put("mobile_number", mobileNumber)
            jsonParam.put("email", email)

            if (ConnectionManager().checkConnectivity(this@ForgetPassword)) {

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParam, Response.Listener {

                    try {

                        val dataObject = it.getJSONObject("data")
                        val success = dataObject.getBoolean("success")

                        if (success) {
                            val changePasswordIntent = Intent(this@ForgetPassword, ChangePassword::class.java)
                            changePasswordIntent.putExtra("mobileNumber", mobileNumber)
                            startActivity(changePasswordIntent)
                        } else {
                            Toast.makeText(this@ForgetPassword, "No user found! or Invalid data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ForgetPassword,
                            "Some UnExpected Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this@ForgetPassword,
                        "Volley Error Occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = "0347b8d3845796"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } else {
                val dialog = AlertDialog.Builder(this@ForgetPassword)
                dialog.setTitle("Failed")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { dialogInterface, which ->
                    ActivityCompat.finishAffinity(this@ForgetPassword)
                }
                dialog.create()
                dialog.show()
            }

        }


    }

}