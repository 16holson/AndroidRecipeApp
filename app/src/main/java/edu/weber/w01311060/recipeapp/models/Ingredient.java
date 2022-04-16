package edu.weber.w01311060.recipeapp.models;

public class Ingredient implements Comparable<Ingredient>
{
    private String name;
    private boolean active;

    public Ingredient(String name)
    {
        this.name = name;
        active = false;
    }
    public Ingredient(String name, boolean active)
    {
        this.name = name;
        this.active = active;
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

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int compareTo(Ingredient i)
    {
        return Boolean.compare(this.active, i.active);
    }
}
