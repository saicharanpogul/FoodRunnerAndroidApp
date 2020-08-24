package com.training.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.training.foodrunner.database.RestaurantDetailsDatabase
import com.training.foodrunner.database.RestaurantDetailsEntity
import com.training.foodrunner.model.RestaurantDetails
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONObject

class RestaurantDetails : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerRestaurantDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var isFavImg: ImageView
    lateinit var btnProceedToCart: Button
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    var restaurantDetailsList = arrayListOf<RestaurantDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val restaurantName = intent.getStringExtra("restaurantName")
        val isFav = intent.getBooleanExtra("isFav", false)
        isFavImg = findViewById(R.id.imgFavRestaurantDetails)
        toolbar = findViewById(R.id.tb_restaurant_details)
        proceedToCartLayout = findViewById(R.id.proceedToCartLayout)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        toolbar.title = restaurantName
        setActionBar()


        if (isFav) {
            isFavImg.setImageResource(R.drawable.ic_favorites_solid)
        } else {
            isFavImg.setImageResource(R.drawable.ic_favorites_border)
        }

        recyclerRestaurantDetails = findViewById(R.id.recyclerRestaurantDetails)
        layoutManager = LinearLayoutManager(this@RestaurantDetails)

        val queue = Volley.newRequestQueue(this@RestaurantDetails)
        val restaurantId = intent.getStringExtra("restaurantId")
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"


        if (ConnectionManager().checkConnectivity(this@RestaurantDetails)) {

            val dishIdArray = arrayListOf<String>()
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val dataObject = it.getJSONObject("data")
                        val success = dataObject.getBoolean("success")

                        if (success) {
                            val data = dataObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantDetailsJsonObject = data.getJSONObject(i)
                                val restaurantDetailsObject = RestaurantDetails(
                                    (i + 1).toString(),
                                    restaurantDetailsJsonObject.getString("name"),
                                    restaurantDetailsJsonObject.getString("cost_for_one"),
                                    restaurantDetailsJsonObject.getString("restaurant_id")
                                )
                                dishIdArray.add(restaurantDetailsJsonObject.getString("id"))
                                restaurantDetailsList.add(restaurantDetailsObject)
                                recyclerAdapter = RestaurantDetailsRecyclerAdapter(
                                    this@RestaurantDetails,
                                    restaurantDetailsList,
                                    btnProceedToCart,
                                    restaurantName as String,
                                    dishIdArray
                                )
                                recyclerRestaurantDetails.adapter = recyclerAdapter
                                recyclerRestaurantDetails.layoutManager = layoutManager
                                onBackPressed(
                                    RestaurantDetailsEntity(
                                        restaurantDetailsObject.dishId.toInt(),
                                        restaurantDetailsObject.dishName,
                                        restaurantDetailsObject.dishCostForOne,
                                        restaurantDetailsObject.restaurantId
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                this@RestaurantDetails,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RestaurantDetails,
                            "Some UnExpected Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this@RestaurantDetails,
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
            val dialog = AlertDialog.Builder(this@RestaurantDetails)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { dialogInterface, which ->
                ActivityCompat.finishAffinity(this@RestaurantDetails)
            }
            dialog.create()
            dialog.show()

        }

        /*btnProceedToCart.setOnClickListener{
            Toast.makeText(this@RestaurantDetails, "Clicked Proceed to cart", Toast.LENGTH_SHORT).show()
            val cartIntent = Intent(this@RestaurantDetails, Cart::class.java)
            startActivity(cartIntent)
        }*/

    }

    fun onBackPressed(restaurantDetailsEntity: RestaurantDetailsEntity) {
        DBAsyncTaskRestaurantDetails(applicationContext, restaurantDetailsEntity, 2).execute()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
            finish()
        }
        return true
    }


    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class DBAsyncTaskRestaurantDetails(
        val context: Context,
        private val restaurantDetailsEntity: RestaurantDetailsEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        /*
        Mode -> 1 Check if dish is already added in cart
        Mode -> 2 Delete all item from cart
        */
        private val db = Room.databaseBuilder(
            context,
            RestaurantDetailsDatabase::class.java,
            "restaurant-details-db"
        ).build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    // Mode 1 -> Check if dish is already added in cart
                    val order = db.restaurantDetailsDao().getAllRestaurantDetails()
                    db.close()
                    return order.isNotEmpty()
                }
                2 -> {
                    // Mode -> 2 Delete all item from cart
                    db.restaurantDetailsDao().deleteRestaurantDetails(restaurantDetailsEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
}