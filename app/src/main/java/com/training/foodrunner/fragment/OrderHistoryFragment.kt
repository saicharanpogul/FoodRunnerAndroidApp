package com.training.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.training.foodrunner.model.OrderHistory
import com.training.foodrunner.util.ConnectionManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var noHistoryLayout: RelativeLayout
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnOrderHistory: Button
    var orderHistoryList = ArrayList<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory)
        noHistoryLayout = view.findViewById(R.id.noHistoryLayout)

        sharedPreferences = this.activity?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)!!
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)

        layoutManager = LinearLayoutManager(activity as Context)

        if (orderHistoryList.isNotEmpty()) {
            noHistoryLayout.visibility = View.GONE
        }

        val userId = sharedPreferences.getString("userId", "0") as String


        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

            try {
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    val dataObject = it.getJSONObject("data")
                    val success = dataObject.getBoolean("success")

                    if (success) {
                        val data = dataObject.getJSONArray("data")

                        for (i in 0 until data.length()) {
                            val restaurantObject = data.getJSONObject(i)
                            val restaurantWishOrder = OrderHistory(
                                restaurantObject.getString("order_id"),
                                restaurantObject.getString("restaurant_name"),
                                restaurantObject.getString("total_cost"),
                                restaurantObject.getString("order_placed_at").substring(0, 8)
                            )
                            orderHistoryList.add(restaurantWishOrder)

                            recyclerAdapter = OrderHistoryRecyclerAdapter(activity as Context, orderHistoryList, btnOrderHistory, "nil")
                            recyclerOrderHistory.adapter = recyclerAdapter
                            recyclerOrderHistory.layoutManager = layoutManager
                        }

                    } else {
                        Toast.makeText(context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
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
                startActivity(settingsIntent)
                activity!!.finish()
            }
            dialog.setNegativeButton("Exit") { dialogInterface, which ->
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }

        if (orderHistoryList.size > 0) {
            noHistoryLayout.visibility = View.VISIBLE
        } else {
            noHistoryLayout.visibility = View.GONE
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
