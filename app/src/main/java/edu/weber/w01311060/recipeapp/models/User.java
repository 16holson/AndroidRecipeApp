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
    private Map<String, String> recipeIds;
    private int iterator;

    public User()
    {

    }

    public User(String name, String email, String uid)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
        recipeIds = new HashMap<String, String>();
        iterator = 1;
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

    public void addRecipeId(String recipeId)
    {
        this.recipeIds.put(String.valueOf(iterator), recipeId);
        iterator++;
    }
    public void removeRecipeId(String key)
    {
        this.recipeIds.remove(key);
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
