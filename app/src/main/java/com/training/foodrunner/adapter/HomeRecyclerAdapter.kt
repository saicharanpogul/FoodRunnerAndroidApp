package com.training.foodrunner.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.training.foodrunner.R
import com.training.foodrunner.activity.RestaurantDetails
import com.training.foodrunner.database.RestaurantDatabase
import com.training.foodrunner.database.RestaurantEntity
import com.training.foodrunner.fragment.FavoritesFragment
import com.training.foodrunner.model.Restaurant
import com.training.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.home_recycler_single_row.view.*
import org.json.JSONException

class HomeRecyclerAdapter(val context: Context, private val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recycler_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        holder.txtRestaurantCostForOne.text = restaurant.restaurantCostForOne
        Picasso.get().load(restaurant.restaurantImage).into(holder.imgRestaurantImage);

        val restaurantDetailsIntent = Intent(context, RestaurantDetails::class.java)

        val checkFav = DBAsyncTask(
            context.applicationContext, RestaurantEntity(
                restaurant.restaurantId.toInt(),
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCostForOne,
                restaurant.restaurantImage
            ), 1
        ).execute()
        var isFav = checkFav.get()

        if (isFav) {
            holder.imgFav.setImageResource(R.drawable.ic_favorites_solid)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_favorites_border)
        }

        holder.imgFav.setOnClickListener {

            if (isFav) {
                val async = DBAsyncTask(
                    context.applicationContext, RestaurantEntity(
                        restaurant.restaurantId.toInt(),
                        restaurant.restaurantName,
                        restaurant.restaurantRating,
                        restaurant.restaurantCostForOne,
                        restaurant.restaurantImage
                    ), 3
                ).execute()
                Toast.makeText(
                    context,
                    "${holder.txtRestaurantName.text} removed to favourites",
                    Toast.LENGTH_SHORT
                ).show()
                holder.imgFav.setImageResource(R.drawable.ic_favorites_border)
                isFav = false
                restaurantDetailsIntent.putExtra("isFav", isFav)
            } else {
                val async = DBAsyncTask(
                    context.applicationContext, RestaurantEntity(
                        restaurant.restaurantId.toInt(),
                        restaurant.restaurantName,
                        restaurant.restaurantRating,
                        restaurant.restaurantCostForOne,
                        restaurant.restaurantImage
                    ), 2
                ).execute()
                Toast.makeText(
                    context,
                    "${holder.txtRestaurantName.text} added to favourites",
                    Toast.LENGTH_SHORT
                ).show()
                holder.imgFav.setImageResource(R.drawable.ic_favorites_solid)
                isFav = true
                restaurantDetailsIntent.putExtra("isFav", isFav)
            }
        }

        holder.llContent.setOnClickListener {
            restaurantDetailsIntent.putExtra("restaurantId", restaurant.restaurantId)
            restaurantDetailsIntent.putExtra("restaurantName", restaurant.restaurantName)
            restaurantDetailsIntent.putExtra("isFav", isFav)
            context.startActivity(restaurantDetailsIntent)
        }

    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txt_restaurant_name)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txt_restaurant_rating)
        val txtRestaurantCostForOne: TextView = view.findViewById(R.id.txt_cost_for_one)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.img_restaurant)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFav: ImageView = view.findViewById(R.id.img_favourite)
    }


    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        /*
        Mode 1 -> Check DB if the restaurant is in favourites or not
        Mode 2 -> Save the restaurant into DB as favourites
        Mode 3 -> Remove the restaurant from the favourites
        */

        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    //Check DB if the restaurant is in favourites or not
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    //Save the restaurant into DB as favourites
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the restaurant from the favourites
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }
    }
}
