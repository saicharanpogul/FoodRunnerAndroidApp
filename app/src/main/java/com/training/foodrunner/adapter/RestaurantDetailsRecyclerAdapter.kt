package com.training.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.training.foodrunner.R
import com.training.foodrunner.activity.Cart
import com.training.foodrunner.activity.Home
import com.training.foodrunner.database.RestaurantDetailsDao
import com.training.foodrunner.database.RestaurantDetailsDatabase
import com.training.foodrunner.database.RestaurantDetailsEntity
import com.training.foodrunner.model.RestaurantDetails
import kotlinx.android.synthetic.main.activity_restaurant_details.view.*

class RestaurantDetailsRecyclerAdapter(
    val context: Context,
    private val restaurantDetailsList: ArrayList<RestaurantDetails>,
    private val btnProceedToCart: Button,
    private val restaurantName: String,
    private val dishIdArray: ArrayList<String>
) : RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.RestaurantDetailsViewHolder>() {
    var itemCounter: Int = 0
    var totalCost: Int = 0
    lateinit var sharedPreferences: SharedPreferences
    private var dishIdList = mutableListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reatsurant_details_recycler_single_row, parent, false)

        return RestaurantDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantDetailsList.size
    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {

        sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        val restaurantDetails = restaurantDetailsList[position]
        holder.txtDishId.text = restaurantDetails.dishId
        holder.txtDishName.text = restaurantDetails.dishName
        holder.txtDishCostForOne.text = restaurantDetails.dishCostForOne

        val checkCart = DBAsyncTaskRestaurantDetails(
            context.applicationContext, RestaurantDetailsEntity(
                restaurantDetails.dishId.toInt(),
                restaurantDetails.dishName,
                restaurantDetails.dishCostForOne,
                restaurantDetails.restaurantId
            ), 1
        ).execute()
        var isAdded = checkCart.get()

        holder.btnAdd.setOnClickListener {
            if (!isAdded) {
                val async = DBAsyncTaskRestaurantDetails(
                    context.applicationContext, RestaurantDetailsEntity(
                        restaurantDetails.dishId.toInt(),
                        restaurantDetails.dishName,
                        restaurantDetails.dishCostForOne,
                        restaurantDetails.restaurantId
                    ), 2
                ).execute()
                holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.yellow))
                holder.btnAdd.text = context.getText(R.string.remove)
                isAdded = true
                itemCounter++
                totalCost += restaurantDetails.dishCostForOne.toInt()
                dishIdList.add(dishIdArray[position])
            } else {
                val async = DBAsyncTaskRestaurantDetails(
                    context.applicationContext, RestaurantDetailsEntity(
                        restaurantDetails.dishId.toInt(),
                        restaurantDetails.dishName,
                        restaurantDetails.dishCostForOne,
                        restaurantDetails.restaurantId
                    ), 3
                ).execute()
                holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
                holder.btnAdd.text = context.getText(R.string.add)
                isAdded = false
                itemCounter--
                totalCost -= restaurantDetails.dishCostForOne.toInt()
                dishIdList.remove(dishIdArray[position])
            }

            if (itemCounter > 0) {
                btnProceedToCart.visibility = View.VISIBLE
            } else {
                btnProceedToCart.visibility = View.GONE
            }

            sharedPreferences.edit().putString("totalCost", totalCost.toString()).apply()
        }

        btnProceedToCart.setOnClickListener{
            val cartIntent = Intent(context, Cart::class.java)
            cartIntent.putExtra("restaurantName", restaurantName)
            cartIntent.putExtra("restaurantId", restaurantDetails.restaurantId)
            cartIntent.putStringArrayListExtra("dishIdArray", ArrayList(dishIdList))
            context.startActivity(cartIntent)
        }

        if (holder.txtDishName.text.toString().length > 20) {
            holder.txtDishName.textSize = 15F
        }

    }


    class RestaurantDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishId: TextView = view.findViewById(R.id.txtDishId)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishCostForOne: TextView = view.findViewById(R.id.txtDishCostForOne)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    class DBAsyncTaskRestaurantDetails(
        val context: Context,
        private val restaurantDetailsEntity: RestaurantDetailsEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {


        private val db = Room.databaseBuilder(
            context,
            RestaurantDetailsDatabase::class.java,
            "restaurant-details-db"
        ).build()
        /*
        Mode 1 -> Check if dish is already added in cart
        Mode 2 -> Save the order into DB(cart)
        Mode 3 -> Remove the dish from the cart
        */

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    //Mode 1 -> Check if dish is already added in cart
                    val restaurantDetails: RestaurantDetailsEntity? = db.restaurantDetailsDao()
                        .getByDishId(restaurantDetailsEntity.dish_id.toString())
                    db.close()
                    return restaurantDetails != null
                }
                2 -> {
                    //Mode 2 -> Save the order into DB(cart)
                    db.restaurantDetailsDao().insertRestaurantDetails(restaurantDetailsEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Mode 3 -> Remove the dish from the cart
                    db.restaurantDetailsDao().deleteRestaurantDetails(restaurantDetailsEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}