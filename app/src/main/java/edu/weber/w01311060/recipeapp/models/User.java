package edu.weber.w01311060.recipeapp.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User
{
    private String name;
    private String email;
    private String uid;
    private String sync;
    private Map<String, String> recipeIds;
    private Map<String, Boolean> groceryList;

    public User()
    {

    }

    public User(String name, String email, String uid)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
        recipeIds = new HashMap<String, String>();
        groceryList = new HashMap<String, Boolean>();
        sync = null;

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUuid(String uuid)
    {
        this.uid = uuid;
    }

    public Map<String, String> getRecipeIds()
    {
        return recipeIds;
    }

    public void addRecipeId(String recipeName, String recipeId)
    {
        this.recipeIds.put(recipeName, recipeId);
    }
    public void addGroceryItem(String name, boolean checked)
    {
        if (!groceryList.containsKey(name))
        {
            this.groceryList.put(name, checked);
        }

    }
    public void updateGroceryItem(String name, boolean checked)
    {
        if (groceryList.containsKey(name))
        {
            this.groceryList.put(name, checked);
        }
    }
    public Map<String, Boolean> getGroceryList()
    {
        return groceryList;
    }
    public void removeGroceryItem(String key)
    {
        this.groceryList.remove(key);
    }
    public void setGroceryList(Map<String, Boolean> groceryList)
    {
        this.groceryList = groceryList;
    }
    public void removeRecipeId(String key)
    {
        this.recipeIds.remove(key);
    }

    public void setRecipeIds(Map<String, String> recipeIds)
    {
        this.recipeIds = recipeIds;
    }

    public String getSync()
    {
        return sync;
    }

    public void setSync(String sync)
    {
        this.sync = sync;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", recipeIds=" + recipeIds +
                '}';
    }
}
