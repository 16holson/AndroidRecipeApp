package edu.weber.w01311060.recipeapp.models;

public class Ingredient implements Comparable<Ingredient>
{
    private String name;
    private String active;

    public Ingredient()
    {

    }

    public Ingredient(String name)
    {
        this.name = name;
        active = "false";
    }
    public Ingredient(String name, String active)
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

    public String isActive()
    {
        return active;
    }

    public void setActive(String active)
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
        return Boolean.compare(Boolean.valueOf(this.active), Boolean.valueOf(i.active));
    }
}
