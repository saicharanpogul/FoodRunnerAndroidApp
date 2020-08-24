package com.training.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrunner.R
import com.training.foodrunner.model.CartItems
import kotlinx.android.synthetic.main.cart_recycler_single_row.view.*

class OrderHistoryItemsRecyclerAdapter(
    val context: Context,
    private val itemsArray: ArrayList<CartItems>
) : RecyclerView.Adapter<OrderHistoryItemsRecyclerAdapter.OrderItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_recycler_single_row, parent, false)
        return  OrderItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  itemsArray.size
    }

    override fun onBindViewHolder(holder: OrderItemsViewHolder, position: Int) {
        val itemsList = itemsArray[position]
        holder.dishName.text = itemsList.dishName
        holder.dishCost.text = itemsList.dishCostForOne
    }

    class OrderItemsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.txtDishNameCart)
        val dishCost: TextView = view.findViewById(R.id.txtDishCostCart)
    }

}