package edu.weber.w01311060.recipeapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import edu.weber.w01311060.recipeapp.models.Recipe;


@Dao
public interface RecipeDAO
{
    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM Recipe ORDER BY _id LIMIT 1")
    Recipe getLastRecipe();

    @Query("SELECT * FROM Recipe WHERE idMeal = :id")
    Recipe findRecipeById(int id);

    @Query("SELECT * FROM Recipe WHERE strMeal LIKE :strMeal")
    LiveData<List<Recipe>> findRecipeByName(String strMeal);

    @RawQuery(observedEntities = Recipe.class)
    LiveData<List<Recipe>> filterRecipes(SupportSQLiteQuery query);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Insert
    void insertRecipes(Recipe... recipes);

    @Update
    void modifyRecipe(Recipe recipe);
}
