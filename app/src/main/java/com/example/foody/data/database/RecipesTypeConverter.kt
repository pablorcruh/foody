package com.example.foody.data.database

import androidx.room.TypeConverter
import com.example.foody.models.FoodRecipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String{
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe{
        var listType = object: TypeToken<FoodRecipe>(){}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultToString(result: com.example.foody.models.Result): String{
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): com.example.foody.models.Result{
        val listType = object: TypeToken<com.example.foody.models.Result>(){}.type
        return gson.fromJson(data, listType)
    }


}