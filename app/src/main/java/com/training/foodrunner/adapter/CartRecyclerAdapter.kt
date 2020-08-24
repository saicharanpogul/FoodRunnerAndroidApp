package com.training.foodrunner.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.training.foodrunner.R
import com.training.foodrunner.activity.OrderConfirmation
import com.training.foodrunner.database.RestaurantDetailsDatabase
import com.training.foodrunner.database.RestaurantDetailsEntity
import com.training.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class CartRecyclerAdapter(
    val context: Context,
    private val btnPlaceOrder: Button?,
    val restaurantId: String?,
    private val foodItemIdArray: ArrayList<String>?
) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    lateinit var sharedPreferences: SharedPreferences

    private val list: AsyncTask<Void, Void, ArrayList<RestaurantDetailsEntity>> =
        DBAsyncTaskGetCartItems(context.applicationContext).execute()
    private val cartDetails: ArrayList<RestaurantDetailsEntity> = list.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_recycler_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartDetails.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val cart = cartDetails[position]
        holder.dishName.text = cart.dishName
        holder.dishCost.text = cart.dishCostForOne

        sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        val totalCost = sharedPreferences.getString("totalCost", "0")
        val text = "Place Order(Total: Rs. $totalCost)"
        if (btnPlaceOrder != null) {
            btnPlaceOrder.text = text
        }

        btnPlaceOrder?.setOnClickListener {


            if (ConnectionManager().checkConnectivity(context)) {

                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val userId = sharedPreferences.getString("userId", "0")
                val jsonParamObject = JSONObject()
                var jsonParamArray = JSONArray()
                jsonParamObject.put("user_id", userId)
                jsonParamObject.put("restaurant_id", restaurantId)
                jsonParamObject.put("total_cost", totalCost)

                if (foodItemIdArray != null) {
                    for (i in foodItemIdArray) {
                        val jsonParam = JSONObject()
                        jsonParam.put("food_item_id", i)
                        jsonParamArray.put(jsonParam)
                    }
                }
                jsonParamObject.put("food", jsonParamArray)

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParamObject, Response.Listener {

                    try {
                        val dataObject = it.getJSONObject("data")
                        val success = dataObject.getBoolean("success")

                        if (success) {
                            val orderConfirmationIntent = Intent(context, OrderConfirmation::class.java)
                            context.startActivity(orderConfirmationIntent)
                        } else {
                            Toast.makeText(context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Some UnExpected Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        context,
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
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Failed")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    context.startActivity(settingsIntent)
                    (context as Activity).finish()
                }
                dialog.setNegativeButton("Exit") { dialogInterface, which ->
                    ActivityCompat.finishAffinity(context as Activity)
                }
                dialog.create()
                dialog.show()
            }

        }

    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.txtDishNameCart)
        val dishCost: TextView = view.findViewById(R.id.txtDishCostCart)
    }

    class DBAsyncTaskGetCartItems(
        val context: Context
    ) : AsyncTask<Void, Void, ArrayList<RestaurantDetailsEntity>>() {

        //Get all items in the cart
        val db = Room.databaseBuilder(
            context,
            RestaurantDetailsDatabase::class.java,
            "restaurant-details-db"
        ).build()

        override fun doInBackground(vararg params: Void?): ArrayList<RestaurantDetailsEntity> {
            val items = db.restaurantDetailsDao().getAllRestaurantDetails()
            println(items)
            db.close()
            return items as ArrayList<RestaurantDetailsEntity>
        }

    }

}
