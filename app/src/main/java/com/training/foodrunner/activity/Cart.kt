package com.training.foodrunner.activity

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.training.foodrunner.R
import com.training.foodrunner.adapter.CartRecyclerAdapter
import com.training.foodrunner.database.RestaurantDetailsDatabase
import com.training.foodrunner.database.RestaurantDetailsEntity
import com.training.foodrunner.model.CartItems
import com.training.foodrunner.model.RestaurantDetails

class Cart : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var txtRestaurantName: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.tb_cart)
        toolbar.title = "My Cart"
        setActionBar()
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        recyclerCart = findViewById(R.id.recyclerCart)

        val restaurantName = intent.getStringExtra("restaurantName")
        txtRestaurantName = findViewById(R.id.txtRestaurantNameCart)
        txtRestaurantName.text = restaurantName

        val restaurantId = intent.getStringExtra("restaurantId")

        val foodItemId = intent.getStringArrayListExtra("dishIdArray") as ArrayList<String>

        recyclerAdapter = CartRecyclerAdapter(
            this@Cart,
            btnPlaceOrder,
            restaurantId as String,
            foodItemId
        )
        layoutManager = LinearLayoutManager(this@Cart)

        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return true
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }



}