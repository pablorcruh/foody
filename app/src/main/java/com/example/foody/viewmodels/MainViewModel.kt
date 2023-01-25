package com.example.foody.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.foody.data.Repository
import com.example.foody.data.database.RecipeEntity
import com.example.foody.models.FoodRecipe
import com.example.foody.ui.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    /** ROOM DATABASE*/
    val readRecipes: LiveData<List<RecipeEntity>> = repository.local.readDatabase().asLiveData()
    private fun insertRecipes(recipesEntity: RecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }


    /** RETROTFIT*/
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipe(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipeSafeCall(searchQuery)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>){
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }

            }catch(e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not Found")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun searchRecipeSafeCall(searchQuery: Map<String, String>){
        searchRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)

            }catch (e: Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipe not Found")
            }
        }else{
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection")
        }

    }


    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipeEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>?{
        when{
            response.message().toString().contains("timeout") ->{
                return NetworkResult.Error("Network Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not Found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else ->{
                return NetworkResult.Error(response.message())
            }
        }
    }


    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}