package com.example.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.databinding.RecipesRowLayoutBinding
import com.example.foody.models.FoodRecipe
import com.example.foody.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<com.example.foody.models.Result>()

    class MyViewHolder(private val binding:RecipesRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(result: com.example.foody.models.Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setDate(newData: FoodRecipe){
        val recipeDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipeDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

}