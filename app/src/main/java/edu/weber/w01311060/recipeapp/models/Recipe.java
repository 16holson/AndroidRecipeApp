package edu.weber.w01311060.recipeapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recipe
{
    public Recipe(int idMeal, String strMeal)
    {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
    }
    public Recipe()
    {

    }

    @PrimaryKey(autoGenerate = true)
    private int _id;
    @ColumnInfo
    private int idMeal;
    @ColumnInfo
    private String strMeal;
    @ColumnInfo
    private String category;

    public int get_id()
    {
        return _id;
    }

    public void set_id(int _id)
    {
        this._id = _id;
    }

    public int getIdMeal()
    {
        return idMeal;
    }

    public void setIdMeal(int idMeal)
    {
        this.idMeal = idMeal;
    }

    public String getStrMeal()
    {
        return strMeal;
    }

    public void setStrMeal(String strMeal)
    {
        this.strMeal = strMeal;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }
}
