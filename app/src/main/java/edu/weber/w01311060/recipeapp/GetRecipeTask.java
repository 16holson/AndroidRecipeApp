package edu.weber.w01311060.recipeapp;


import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import edu.weber.w01311060.recipeapp.models.RecipeDetails;

public class GetRecipeTask extends AsyncTask<Integer, Integer, String>
{
    private String json = "";
    private Integer recipeId;
    private RecipeDetails recipeDetails;
    public AsyncResponse mCallBack = null;

    public interface AsyncResponse
    {
        void processFinished(RecipeDetails recipeDetails);
    }

    public GetRecipeTask(AsyncResponse mCallBack)
    {
        this.mCallBack = mCallBack;
    }

    @Override
    protected String doInBackground(Integer... params)
    {
        recipeId = params[0];

        try
        {
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + recipeId.toString());
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

            }
        }
        catch(MalformedURLException e)
        {
            Log.d("Task", "MalformedURLException: " + e);
        }
        catch (IOException e)
        {
            Log.d("Task", "IOException: " + e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        json = json.replace("{\"meals\":[", "");
        json = json.replace("]}", "");
        recipeDetails = jsonParse(json);
        mCallBack.processFinished(recipeDetails);
    }

    private RecipeDetails jsonParse(String rawJson)
    {
        RecipeDetails recipeDetails = new RecipeDetails();

        try
        {
            ArrayList<String> ings = new ArrayList<String>();
            JSONObject jo = new JSONObject(rawJson);


            recipeDetails.setIdMeal(recipeId);
            recipeDetails.setStrMeal(jo.get("strMeal").toString());
            recipeDetails.setCategory(jo.get("strCategory").toString());
            recipeDetails.setArea(jo.get("strArea").toString());
            recipeDetails.setInstructions(jo.get("strInstructions").toString());

            for (int i = 1; i <= 20; i++)
            {
                String ing = jo.get("strIngredient" + i).toString();
                String measure = jo.get("strMeasure" + i).toString();

                if(!ing.equals("") && !ing.equals("null"))
                {
                    ings.add(ing + " - " + measure);
                }
            }
            recipeDetails.setIngredients(ings);
        }
        catch (Exception e)
        {
            Log.d("Task", "Exception parsing json: " + e.getMessage());
        }
        return  recipeDetails;
    }
}
