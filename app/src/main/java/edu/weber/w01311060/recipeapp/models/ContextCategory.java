package edu.weber.w01311060.recipeapp.models;

import android.content.Context;

public class ContextCategory
{
    private Context context;
    private String category;
    public ContextCategory(Context context, String category)
    {
        this.context = context;
        this.category = category;
    }

    public Context getContext()
    {
        return context;
    }

    public void setContext(Context context)
    {
        this.context = context;
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
