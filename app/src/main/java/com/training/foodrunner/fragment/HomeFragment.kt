package com.training.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.training.foodrunner.R
import com.training.foodrunner.adapter.HomeRecyclerAdapter
import com.training.foodrunner.database.RestaurantDao
import com.training.foodrunner.database.RestaurantDatabase
import com.training.foodrunner.database.RestaurantEntity
import com.training.foodrunner.model.Restaurant
import com.training.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.home_recycler_single_row.*
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    // lateinit var btnCheckConnectivity: Button
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // btnCheckConnectivity = view.findViewById(R.id.btn_check_connectivity)
        recyclerHome = view.findViewById(R.id.recycler_home)
        progressLayout = view.findViewById(R.id.progress_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        progressLayout.visibility = View.VISIBLE

        /*
        btnCheckConnectivity.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("Okay") { dialogInterface, which ->
                    Toast.makeText(activity as Context, "Clicked Okay", Toast.LENGTH_LONG).show()
                }
                dialog.setNegativeButton("Cancel") { dialogInterface, which ->
                    Toast.makeText(activity as Context, "Clicked Cancel", Toast.LENGTH_LONG).show()
                }
                dialog.create()
                dialog.show()

            } else {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Failed")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Okay") { dialogInterface, which ->
                    Toast.makeText(activity as Context, "Clicked Okay", Toast.LENGTH_LONG).show()
                }

                dialog.setNegativeButton("Cancel") { dialogInterface, which ->
                    Toast.makeText(activity as Context, "Clicked Cancel", Toast.LENGTH_LONG).show()
                }
                dialog.create()
                dialog.show()

            }
        }*/

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    progressLayout.visibility = View.GONE
                    val dataObject = it.getJSONObject("data")
                    val success = dataObject.getBoolean("success")

                    if (success) {
                        val data = dataObject.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantInfoList)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(activity as Context, "Some UnExpected Error Occurred!", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                if (activity != null){
                    Toast.makeText(activity as Context, "Volley Error Occurred!", Toast.LENGTH_SHORT).show()
                }
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers =  HashMap<String, String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = "###############"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
            } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { dialogInterface, which ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { dialogInterface, which ->
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()

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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
