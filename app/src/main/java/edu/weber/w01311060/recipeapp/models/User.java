package edu.weber.w01311060.recipeapp.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class User
{
    private FirebaseUser user;
    private List<String> recipeIds;

    public User()
    {

    }

    public User(FirebaseUser user)
    {
        this.user = user;
        recipeIds = new ArrayList<String>();
    }

    public FirebaseUser getUser()
    {
        return user;
    }

    public void setUser(FirebaseUser user)
    {
        this.user = user;
    }

    public List<String> getRecipeIds()
    {
        return recipeIds;
    }

    public void addRecipeId(String recipeId)
    {
        this.recipeIds.add(recipeId);
    }
}
