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

    public LiveData<List<Recipe>> getAllRecipes(Context context)
    {
        if(recipeList == null)
        {
            recipeList = AppDatabase.getInstance(context).getRecipeDao().getAll();
        }
        return recipeList;
    }
}
