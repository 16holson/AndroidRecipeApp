package edu.weber.w01311060.recipeapp.models;

import java.util.List;

public class RecipeList
{
    private List<Recipe> recipeList;
    public RecipeList(List<Recipe> recipeList)
    {
        this.recipeList = recipeList;
    }

    public List<Recipe> getRecipeList()
    {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList)
    {
        this.recipeList = recipeList;
    }
}
