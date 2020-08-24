package com.training.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.training.foodrunner.R
import com.training.foodrunner.database.RestaurantDatabase
import com.training.foodrunner.database.RestaurantEntity

class FavouriteRecyclerAdapter(val context: Context, private val restaurantList: List<RestaurantEntity>):
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_recycler_single_row, parent, false)
        return  FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.txtFavRestaurantName.text = restaurant.restaurantName
        holder.txtFavRestaurantCostForOne.text = restaurant.restaurantCostForOne
        holder.txtFavRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_food).into(holder.imgFavRestaurantImage)

        holder.llFavContent.setOnClickListener{
            Toast.makeText(context, "Clicked ${holder.txtFavRestaurantName.text}", Toast.LENGTH_SHORT).show()
        }

        holder.imgFavRestaurantFavImage.setOnClickListener{
            val asyncRemove = DBAsyncTaskRemove(context.applicationContext, RestaurantEntity(
                restaurant.restaurant_id,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCostForOne,
                restaurant.restaurantImage
            )).execute()
            Toast.makeText(context, "${holder.txtFavRestaurantName.text} removed from favourites", Toast.LENGTH_SHORT).show()
        }
    }

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val llFavContent = view.findViewById<LinearLayout>(R.id.llFavContent)!!
        val txtFavRestaurantName = view.findViewById<TextView>(R.id.txtFavRestaurantName)!!
        val txtFavRestaurantCostForOne = view.findViewById<TextView>(R.id.txtFavRestaurantCostForOne)!!
        val txtFavRestaurantRating = view.findViewById<TextView>(R.id.txtFavRestaurantRating)!!
        val imgFavRestaurantFavImage = view.findViewById<ImageView>(R.id.imgFavRestaurantFavImage)!!
        val imgFavRestaurantImage = view.findViewById<ImageView>(R.id.imgFavRestaurantImage)!!
    }

    class DBAsyncTaskRemove(val context: Context, private val restaurantEntity: RestaurantEntity): AsyncTask<Void, Void, Boolean>() {

        //Remove the restaurant from the favourites

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            //Remove the restaurant from the favourites
            db.restaurantDao().deleteRestaurant(restaurantEntity)
            db.close()
            return true
        }
    }

}
