package com.example.foody.data

import com.example.foody.data.database.entities.RecipeEntity
import com.example.foody.data.database.RecipesDao
import com.example.foody.data.database.entities.FavoritesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
){

    fun readRecipes(): Flow<List<RecipeEntity>>{
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipe(): Flow<List<FavoritesEntity>>{
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipeEntity){
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipes(favoriteRecipe: FavoritesEntity){
        recipesDao.insertFavoriteRecipe(favoriteRecipe)
    }

    suspend fun deleteFavoriteRecipe(favoriteRecipe: FavoritesEntity){
        recipesDao.deleteFavoriteRecipe(favoriteRecipe)
    }

    suspend fun deleteAllFavoriteRecipes(){
        recipesDao.deleteAllFavoritesRecipes()
    }
}