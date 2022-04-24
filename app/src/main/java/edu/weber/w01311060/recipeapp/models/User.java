package edu.weber.w01311060.recipeapp.models;

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
    private List<Ingredient> groceryList;

    public User()
    {

    }

    public User(String name, String email, String uid)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
        recipeIds = new HashMap<String, String>();
        groceryList = new ArrayList<Ingredient>();
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
    public void addGroceryItem(Ingredient ingredient)
    {
        boolean contains = false;
        for(Ingredient i : groceryList)
        {
            if(i.getName().equals(ingredient.getName()))
            {
                contains = true;
            }
        }
        if(!contains)
        {
            groceryList.add(ingredient);
        }
    }

    public void updateGroceryItem(Ingredient ingredient)
    {
        for (Ingredient i : groceryList)
        {
            if (i.getName().equals(ingredient.getName()))
            {
                i.setActive(ingredient.isActive());
            }
        }
    }
    public List<Ingredient> getGroceryList()
    {
        return groceryList;
    }
    public void removeGroceryItem(String name)
    {
        for (int i = 0; i < groceryList.size(); i++)
        {
            if (groceryList.get(i).getName().equals(name))
            {
                groceryList.remove(i);
            }
        }
    }
    public void setGroceryList(List<Ingredient> groceryList)
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
