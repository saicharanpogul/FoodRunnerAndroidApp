package com.training.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.transition.ChangeBounds
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    lateinit var mobileET: EditText
    lateinit var passwordET: EditText
    lateinit var loginBtn: Button
    lateinit var forgetPasswordTxt: TextView
    lateinit var signUpTxt: TextView
    lateinit var sharedPreferences: SharedPreferences
    /*val validMobileNumber = "9765701828"
    val validPassword = arrayOf("thor", "hulk", "steve", "tony")*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.enterTransition = null

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedInThroughLogin", false)

        if (isLoggedIn) {
            val home = Intent(this@LoginActivity, Home::class.java)
            startActivity(home)
            finish()
        }

        mobileET = findViewById<EditText>(R.id.et_mobile_number)
        passwordET = findViewById<EditText>(R.id.et_password)
        loginBtn = findViewById<Button>(R.id.btn_login)
        forgetPasswordTxt = findViewById<TextView>(R.id.txt_forget_password)
        signUpTxt = findViewById<TextView>(R.id.txt_register)

        val slideDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_from_bottom
        )
        mobileET.startAnimation(slideDown)
        passwordET.startAnimation(slideDown)
        loginBtn.startAnimation(slideDown)
        forgetPasswordTxt.startAnimation(slideDown)
        signUpTxt.startAnimation(slideDown)

        val bounds = ChangeBounds()
        bounds.duration = 700
        window.sharedElementEnterTransition = bounds


        loginBtn.setOnClickListener {


            if (mobileET.text.toString().length == 10 && passwordET.text.toString().length >= 4) {
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                val mobileNumber = mobileET.text.toString()
                val password = passwordET.text.toString()

                val jsonParam = JSONObject()
                jsonParam.put("mobile_number", mobileNumber)
                jsonParam.put("password", password)

                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParam, Response.Listener {

                            try {
                                val dataObject = it.getJSONObject("data")
                                val success = dataObject.getBoolean("success")

                                if (success) {
                                    val userId = dataObject.getJSONObject("data").getString("user_id")
                                    val name = dataObject.getJSONObject("data").getString("name")
                                    val email = dataObject.getJSONObject("data").getString("email")
                                    val mobileNo =
                                        dataObject.getJSONObject("data").getString("mobile_number")
                                    val address =
                                        dataObject.getJSONObject("data").getString("address")
                                    sharedPreferences.edit().putString("userId", userId).apply()
                                    sharedPreferences.edit().putString("name", name).apply()
                                    sharedPreferences.edit().putString("email", email).apply()
                                    sharedPreferences.edit().putString("mobileNumber", mobileNo)
                                        .apply()
                                    sharedPreferences.edit().putString("address", address).apply()
                                    val loginIntent = Intent(this@LoginActivity, Home::class.java)
                                    savePreferences(name)
                                    startActivity(loginIntent)
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Invalid login credentials",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some UnExpected Error Occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@LoginActivity,
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
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Failed")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { dialogInterface, which ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Mobile number should be 10 digits and password at least 4 characters", Toast.LENGTH_SHORT).show()
            }


            /*val mobileNumber = mobileET.text.toString()
            val password = passwordET.text.toString()

            var nameOfUser: String
            val loginIntent = Intent(this@LoginActivity, Home::class.java)
            loginIntent.putExtra("data", "login")
            if (validMobileNumber == mobileNumber) {
                when (password) {
                    validPassword[0] -> {
                        nameOfUser = "Thor"
                        savePreferences(nameOfUser)
                        startActivity(loginIntent)
                    }
                    validPassword[1] -> {
                        nameOfUser = "Hulk"
                        savePreferences(nameOfUser)
                        startActivity(loginIntent)
                    }
                    validPassword[2] -> {
                        nameOfUser = "Steve"
                        savePreferences(nameOfUser)
                        startActivity(loginIntent)
                    }
                    validPassword[3] -> {
                        nameOfUser = "Tony"
                        savePreferences(nameOfUser)
                        startActivity(loginIntent)
                    }
                }
            } else {
            Toast.makeText(this, "Invalid Credentials $mobileNumber, $password", Toast.LENGTH_SHORT).show()
            }*/
        }


        forgetPasswordTxt.setOnClickListener {
            val forgetPassword = Intent(this@LoginActivity, ForgetPassword::class.java)
            startActivity(forgetPassword)
        }
        signUpTxt.setOnClickListener {
            val registration = Intent(this@LoginActivity, Registration::class.java)
            startActivity(registration)
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        super.onBackPressed()
    }

    private fun savePreferences(title: String) {
        sharedPreferences.edit().putBoolean("isLoggedInThroughLogin", true).apply()
        sharedPreferences.edit().putString("username", title).apply()
    }
}