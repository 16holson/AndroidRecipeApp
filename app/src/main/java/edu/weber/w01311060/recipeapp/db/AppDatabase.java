package edu.weber.w01311060.recipeapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import edu.weber.w01311060.recipeapp.models.Recipe;

@Database(entities = {Recipe.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context)
    {
        if(instance != null)
        {
            return instance;
        }
        instance = Room.databaseBuilder(context, AppDatabase.class, "recipe-database").build();
        return instance;
    }

    public abstract RecipeDAO getRecipeDao();
}
