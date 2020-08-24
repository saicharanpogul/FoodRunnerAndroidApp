package com.training.foodrunner.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.model.CartItems
import com.training.foodrunner.model.OrderHistory
import com.training.foodrunner.util.ConnectionManager
import org.w3c.dom.Text
import java.lang.Exception

class OrderHistoryRecyclerAdapter(
    val context: Context,
    private val ordersList: List<OrderHistory>,
    val btnIgnore: Button,
    val restaurantIdIgnore: String
) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_recycler_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    var orderedItems = ArrayList<CartItems>()
    lateinit var sharedPreferences: SharedPreferences

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {


        sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        val restaurantObject = ordersList[position]

        holder.txtRestaurantName.text = restaurantObject.restaurantName
        holder.txtDate.text = restaurantObject.orderPlacedAt

        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: OrderHistoryItemsRecyclerAdapter

        if (ConnectionManager().checkConnectivity(context)) {

            val userId = sharedPreferences.getString("userId", "0")
            val queue = Volley.newRequestQueue(context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
            try {

                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                        val dataObject = it.getJSONObject("data")
                        val success = dataObject.getBoolean("success")

                        if (success) {

                            val data = dataObject.getJSONArray("data")
                            val restaurantArray = data.getJSONObject(position)
                            orderedItems.clear()
                            val itemsArray = restaurantArray.getJSONArray("food_items")

                            for (i in 0 until itemsArray.length()) {
                                val foodItem = itemsArray.getJSONObject(i)
                                val itemObject = CartItems(
                                    foodItem.getString("food_item_id"),
                                    foodItem.getString("name"),
                                    foodItem.getString("cost"),
                                    "0"
                                )
                                orderedItems.add(itemObject)


                                orderedItemAdapter = OrderHistoryItemsRecyclerAdapter(
                                    context,
                                    orderedItems
                                )

                                holder.recyclerView.adapter = orderedItemAdapter
                                holder.recyclerView.layoutManager = layoutManager
                            }


                        } else {
                            Toast.makeText(context, "Some Error Occurred!", Toast.LENGTH_SHORT)
                                .show()
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
                            headers["token"] = "################"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Some UnExpected Error Occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }

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

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantNameOrderHistory)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerItemsOrderHistory)
    }

}
