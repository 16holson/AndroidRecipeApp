package edu.weber.w01311060.recipeapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.Recipe;
import edu.weber.w01311060.recipeapp.models.User;

public class RecipeViewModel extends ViewModel
{
    private LiveData<List<Recipe>> recipeList;
    private LiveData<List<Recipe>> favRecipeList;
    private Recipe recipe;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private LiveData<List<Recipe>> newList;
    private boolean isFavorite = false;

    public LiveData<List<Recipe>> getAllRecipes(Context context)
    {
        recipeList = AppDatabase.getInstance(context).getRecipeDao().getAll();
        return recipeList;
    }

    public LiveData<List<Recipe>> searchRecipes(String query, Context context)
    {
        if (isFavorite)
        {
            if (user.getValue().getRecipeIds() != null)
            {

                StringBuilder builder = new StringBuilder(" SELECT * FROM Recipe WHERE strMeal LIKE '" + query + "' AND idMeal IN (");

                for (int i = 0; i < user.getValue().getRecipeIds().size(); i++)
                {
                    builder.append("?, ");
                }
                builder.deleteCharAt(builder.length()-1);
                builder.deleteCharAt(builder.length()-1);
                builder.append(")");
                SimpleSQLiteQuery newQuery = new SimpleSQLiteQuery(builder.toString(), user.getValue().getRecipeIds().values().toArray());

                favRecipeList = AppDatabase.getInstance(context).getRecipeDao().filterRecipes(newQuery);
            }
            return favRecipeList;
        }
        else
        {
            recipeList = AppDatabase.getInstance(context).getRecipeDao().findRecipeByName(query);
            return recipeList;
        }

    }
    public LiveData<List<Recipe>> filterRecipes(SupportSQLiteQuery query, Context context)
    {
        if (isFavorite)
        {
            favRecipeList = AppDatabase.getInstance(context).getRecipeDao().filterRecipes(query);
            return favRecipeList;
        }
        else
        {
            recipeList = AppDatabase.getInstance(context).getRecipeDao().filterRecipes(query);
            return recipeList;
        }
    }

    public LiveData<List<Recipe>> filterRecipeList(List<String> ids)
    {
        for (Recipe recipe : recipeList.getValue())
        {
            if (ids.contains(String.valueOf(recipe.getIdMeal())))
            {
                newList.getValue().add(recipe);
            }
        }
        return newList;
    }
    public LiveData<List<Recipe>> searchRecipeList(String name)
    {
        if (newList == null)
        {
            newList = recipeList;
        }
        newList.getValue().clear();
        for (Recipe recipe : recipeList.getValue())
        {
            if (name.matches("(.*)" + recipe.getStrMeal() + "(.*)"))
            {
                newList.getValue().add(recipe);
                Log.d("Search", "Found recipe");
            }
        }
        return newList;
    }


    public boolean tableEmpty(Context context)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                recipe = AppDatabase.getInstance(context)
                        .getRecipeDao()
                        .getLastRecipe();
            }
        }).start();

        if(recipe == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setUser(User newUser)
    {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();;
        DatabaseReference reference = rootNode.getReference("users");
        if(newUser.getSync() != null)
        {
            reference.child(newUser.getSync().toString()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.exists())
                    {
                        Map<String, Boolean> map = (Map<String, Boolean>) snapshot.child("groceryList").getValue();
                        if (map != null)
                        {
                            if(snapshot.child("sync").exists())
                            {
                                if(newUser.getSync().equals(snapshot.child("email").getValue()) && snapshot.child("sync").getValue().equals(newUser.getEmail()))
                                {
                                    //already synced
                                    user.setValue(newUser);
                                    reference.child(newUser.getEmail()).setValue(newUser);
                                    reference.child(newUser.getSync()).child("groceryList").setValue(newUser.getGroceryList());
                                }

                            }
                            else
                            {
                                //new sync
                                reference.child(newUser.getSync()).child("sync").setValue(newUser.getEmail());
                                newUser.setGroceryList(map);
                            }


                        }
                        user.setValue(newUser);

                        reference.child(newUser.getEmail()).setValue(newUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
        else
        {
            user.setValue(newUser);

            reference.child(newUser.getEmail()).setValue(newUser);
        }
    }
    public LiveData<User> getUser()
    {
        return user;
    }
    public void setFavorite(boolean favorite)
    {
        this.isFavorite = favorite;
    }
}
