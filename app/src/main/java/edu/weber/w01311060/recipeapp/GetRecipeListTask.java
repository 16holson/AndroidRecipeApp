package edu.weber.w01311060.recipeapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.ContextCategory;
import edu.weber.w01311060.recipeapp.models.Recipe;

public class GetRecipeListTask extends AsyncTask<ContextCategory, Integer, String>
{
    private String json = "";
    private Context context;
    private String category;
    @Override
    protected String doInBackground(ContextCategory... params)
    {
        this.context = params[0].getContext();
        this.category = params[0].getCategory();
        try
        {
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/filter.php?c=" + category);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();

            switch (status)
            {
                case 200:
                case 201:
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    json = reader.readLine();
                    return json;
                default:
                    Log.d("Task", "Incorrect status: " + status);
            }

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            Log.d("Task", "URL Malformed: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.d("Task", "IOException: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        json = json.replace("{\"meals\":", "");
        json = json.replace("]}", "]");
        Recipe[] recipes = jsonParse(json);
        for (int i = 0; i < recipes.length; i++)
        {
            recipes[i].setCategory(category);
        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //add to database
                AppDatabase.getInstance(context)
                        .getRecipeDao()
                        .insertRecipes(recipes);
            }
        }).start();
    }

    private Recipe[] jsonParse(String rawJson)
    {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        Recipe[] recipes = null;

        try
        {
            //json gives an object instead of an array
            recipes = gson.fromJson(rawJson, Recipe[].class);
        }
        catch (Exception e)
        {
            Log.d("Task", "Error converting json: " + e.getMessage());
        }
        return recipes;
    }
}
