package edu.weber.w01311060.recipeapp;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.Recipe;
import edu.weber.w01311060.recipeapp.models.User;

public class RecipeViewModel extends ViewModel
{
    private LiveData<List<Recipe>> recipeList;
    private Recipe recipe;
    private MutableLiveData<User> user = new MutableLiveData<>();

    public LiveData<List<Recipe>> getAllRecipes(Context context)
    {
        recipeList = AppDatabase.getInstance(context).getRecipeDao().getAll();
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

    public void setUser(User user)
    {
        this.user.setValue(user);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();;
        DatabaseReference reference = rootNode.getReference("users");

        reference.child(user.getUid()).setValue(user);
    }
    public LiveData<User> getUser()
    {
        return user;
    }
}
