package edu.weber.w01311060.recipeapp;


import android.os.AsyncTask;

public class GetRecipeTask extends AsyncTask<String, Integer, String>
{
    private String json = "";
    private String recipeId;
    @Override
    protected String doInBackground(String... params)
    {
        recipeId = params[0];



        return null;
    }
}
