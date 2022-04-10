package edu.weber.w01311060.recipeapp;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.Recipe;

public class RecipeViewModel extends ViewModel
{
    private LiveData<List<Recipe>> recipeList;
    private Recipe recipe;

    public LiveData<List<Recipe>> getAllRecipes(Context context)
    {
        if(recipeList == null)
        {
            recipeList = AppDatabase.getInstance(context).getRecipeDao().getAll();
        }
        return recipeList;
    }

    public LiveData<List<Recipe>> searchRecipes(String query, Context context)
    {
        recipeList = AppDatabase.getInstance(context).getRecipeDao().findRecipeByName(query);
        return recipeList;
    }
    public LiveData<List<Recipe>> filterRecipes(SupportSQLiteQuery query, Context context)
    {
        recipeList = AppDatabase.getInstance(context).getRecipeDao().filterRecipes(query);
        return recipeList;
    }


    public boolean tableEmpty(Context context)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                recipe = AppDatabase.getInstance(context)
                        .getRecipeDao()
                        .getLastRecipe();
            }
        }).start();

        if(recipe == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
