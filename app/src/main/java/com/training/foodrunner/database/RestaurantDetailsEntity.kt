package com.training.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_details")
data class RestaurantDetailsEntity (
    @PrimaryKey val dish_id: Int,
    @ColumnInfo(name = "dish_name") val dishName: String,
    @ColumnInfo(name = "dish_cost_for_one") val dishCostForOne: String,
    @ColumnInfo(name = "restaurant_id") val restaurantId: String
)