package com.training.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDetailsDao {

    @Insert
    fun insertRestaurantDetails(restaurantDetailsEntity: RestaurantDetailsEntity)

    @Delete
    fun deleteRestaurantDetails(restaurantDetailsEntity: RestaurantDetailsEntity)

    @Query("SELECT * FROM restaurant_details")
    fun getAllRestaurantDetails(): List<RestaurantDetailsEntity>

    @Query("SELECT * FROM restaurant_details WHERE dish_id = :dishId")
    fun getByDishId(dishId: String): RestaurantDetailsEntity
}