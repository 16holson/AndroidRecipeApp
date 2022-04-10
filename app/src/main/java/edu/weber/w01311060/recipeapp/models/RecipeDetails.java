package edu.weber.w01311060.recipeapp.models;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeDetails
{
    public RecipeDetails(int idMeal, String category, String area, String instructions, String strMeal, ArrayList<String> ingredients)
    {
        this.idMeal = idMeal;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.strMeal = strMeal;
        this.ingredients = ingredients;
    }

    public RecipeDetails()
    {

    }

    private int idMeal;
    private String category;
    private String area;
    private String instructions;
    private String strMeal;
    private ArrayList<String> ingredients; //ingredient/measurement

    public int getIdMeal()
    {
        return idMeal;
    }

    public void setIdMeal(int idMeal)
    {
        this.idMeal = idMeal;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getInstructions()
    {
        return instructions;
    }

    public void setInstructions(String instructions)
    {
        this.instructions = instructions;
    }

    public String getStrMeal()
    {
        return strMeal;
    }

    public void setStrMeal(String strMeal)
    {
        this.strMeal = strMeal;
    }

    public ArrayList<String> getIngredients()
    {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients)
    {
        this.ingredients = ingredients;
    }

    @Override
    public String toString()
    {
        return "RecipeDetails{" +
                "idMeal=" + idMeal +
                ", category='" + category + '\'' +
                ", area='" + area + '\'' +
                ", instructions='" + instructions + '\'' +
                ", strMeal='" + strMeal + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}
