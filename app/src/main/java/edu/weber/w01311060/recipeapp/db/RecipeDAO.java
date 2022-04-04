package edu.weber.w01311060.recipeapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.weber.w01311060.recipeapp.models.Recipe;


@Dao
public interface RecipeDAO
{
    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM Recipe WHERE idMeal = :id")
    Recipe findRecipeById(int id);

    @Query("SELECT * FROM Recipe WHERE strMeal = :strMeal")
    Recipe findRecipeByName(int strMeal);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Insert
    void insertRecipes(Recipe... recipes);

    @Update
    void modifyRecipe(Recipe recipe);
}
