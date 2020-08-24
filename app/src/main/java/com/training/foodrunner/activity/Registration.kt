package com.training.foodrunner.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.transition.ChangeBounds
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*


class Registration : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: TextView
    lateinit var etAddress: TextView
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = findViewById(R.id.etNameRegister)
        etMobileNumber = findViewById(R.id.etMobileNumberRegister)
        etEmail = findViewById(R.id.etEmailRegister)
        etAddress = findViewById(R.id.etAddressRegister)
        etPassword = findViewById(R.id.etPasswordRegister)
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister)
        btnRegister = findViewById(R.id.btnRegister)

        val slideDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_from_bottom
        )
        etName.startAnimation(slideDown)
        etMobileNumber.startAnimation(slideDown)
        etEmail.startAnimation(slideDown)
        etAddress.startAnimation(slideDown)
        etPassword.startAnimation(slideDown)
        etConfirmPassword.startAnimation(slideDown)
        btnRegister.startAnimation(slideDown)

        val bounds = ChangeBounds()
        bounds.duration = 600
        window.sharedElementEnterTransition = bounds

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        /*etDob.setOnClickListener{
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val dob = "$dayOfMonth/${monthOfYear+1}/$year"
            etDob.setText(dob)
        }, year, month, day)
        dpd.show()
        }

        val gender = arrayOf("Male", "Female")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gender)
        spGender.adapter = adapter
        spGender.onItemSelectedListener
*/


        btnRegister.setOnClickListener {

            if (etName.text.toString() != ""
                && etEmail.text.toString() != ""
                && etMobileNumber.text.toString() != ""
                && etAddress.text.toString() != ""
                && etPassword.text.toString() != ""
                && etConfirmPassword.text.toString() != ""
            ) {
                if (etMobileNumber.text.toString().length == 10) {
                    if (etPassword.text.toString().length >= 5) {
                        if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                            val queue = Volley.newRequestQueue(this@Registration)
                            val url = "http://13.235.250.119/v2/register/fetch_result"

                            val name = etName.text.toString()
                            val mobileNumber = etMobileNumber.text.toString()
                            val email = etEmail.text.toString()
                            val address = etAddress.text.toString()
                            val password = etPassword.text.toString()
                            val jsonParam = JSONObject()
                            jsonParam.put("name", name)
                            jsonParam.put("mobile_number", mobileNumber)
                            jsonParam.put("password", password)
                            jsonParam.put("address", address)
                            jsonParam.put("email", email)

                            if (ConnectionManager().checkConnectivity(this@Registration)) {
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
                                                    sharedPreferences.edit().putString("name", name)
                                                        .apply()
                                                    sharedPreferences.edit()
                                                        .putString("email", email)
                                                        .apply()
                                                    sharedPreferences.edit()
                                                        .putString("mobileNumber", mobileNumber)
                                                        .apply()
                                                    sharedPreferences.edit()
                                                        .putString("address", address)
                                                        .apply()
                                                    savePreferences(name)
                                                    val registerIntent =
                                                        Intent(this@Registration, Home::class.java)
                                                    startActivity(registerIntent)
                                                } else {
                                                    Toast.makeText(
                                                        this@Registration,
                                                        "Mobile number OR Email Id is already registered",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    this@Registration,
                                                    "Some UnExpected Error Occurred!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        Response.ErrorListener {
                                            Toast.makeText(
                                                this@Registration,
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
                                val dialog = AlertDialog.Builder(this@Registration)
                                dialog.setTitle("Failed")
                                dialog.setMessage("Internet Connection Not Found")
                                dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                    startActivity(settingsIntent)
                                    finish()
                                }
                                dialog.setNegativeButton("Exit") { dialogInterface, which ->
                                    ActivityCompat.finishAffinity(this@Registration)
                                }
                                dialog.create()
                                dialog.show()
                            }
                        } else {
                            Toast.makeText(
                                this@Registration,
                                "Password & Confirm Password doesn't match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Registration,
                            "Password should have at least 5 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@Registration,
                        "Mobile Number should be 10 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@Registration, "Fields shouldn't be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun savePreferences(title: String) {
        sharedPreferences.edit().putBoolean("isLoggedInThroughLogin", true).apply()
        sharedPreferences.edit().putString("username", title).apply()
    }

}