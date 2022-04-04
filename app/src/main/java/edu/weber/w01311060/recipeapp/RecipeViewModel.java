package edu.weber.w01311060.recipeapp;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.Recipe;

public class RecipeViewModel extends ViewModel
{
    private LiveData<List<Recipe>> recipeList;
    private LiveData<Recipe> recipe;

    public LiveData<List<Recipe>> getAllRecipes(Context context)
    {
        if(recipeList == null)
        {
            recipeList = AppDatabase.getInstance(context).getRecipeDao().getAll();
        }
        return recipeList;
    }

    public boolean tableEmpty(Context context)
    {
        recipe = AppDatabase.getInstance(context).getRecipeDao().getLastRecipe();
        if(recipe.getValue() == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
