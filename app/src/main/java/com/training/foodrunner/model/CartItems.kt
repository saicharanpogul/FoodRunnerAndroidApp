package com.training.foodrunner.model

data class CartItems(
    val dishId: String,
    val dishName: String,
    val dishCostForOne: String,
    val restaurantId: String
)