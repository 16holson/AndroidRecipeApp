package edu.weber.w01311060.recipeapp.models;

public class Ingredient
{
    private String name;
    private boolean active;

    public Ingredient(String name)
    {
        this.name = name;
        active = false;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
}
