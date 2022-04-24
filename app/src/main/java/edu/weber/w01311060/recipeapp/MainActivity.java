package edu.weber.w01311060.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.ContextCategory;
import edu.weber.w01311060.recipeapp.models.RecipeList;
import edu.weber.w01311060.recipeapp.models.User;

public class MainActivity extends AppCompatActivity implements LoginFragment.onLoginListener
{
    private FragmentManager fm;
    private User user;
    private BottomNavigationView bottomNavigationView;
    private String[] categories = new String[]{"Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //load database if it is empty
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int count = AppDatabase.getInstance(getApplicationContext()).getRecipeDao().getCount();
                if (count <= 0)
                {
                    loadRecipes();
                }
            }
        }).start();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.groceryList:
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, new GroceryListFragment(), "groceryListFrag")
                                .commit();
                        return true;
                    case R.id.recipes:
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, new RecipeListFragment(), "recipeListFrag")
                                .commit();
                        return true;
                    case R.id.favorites:
                        RecipeListFragment recipeListFragment = RecipeListFragment.newInstance(true);
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, recipeListFragment, "favoritesFrag")
                                .commit();
                        return true;
                    case R.id.account:
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, new AccountFragment(), "accountFrag")
                                .commit();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onLogin(User user)
    {
        this.user = user;
        bottomNavigationView.setSelectedItemId(R.id.recipes);
        fm.beginTransaction()
                .replace(R.id.fragmentContainerView, new RecipeListFragment(), "recipeListFrag")
                .commit();
    }
    private void loadRecipes()
    {
        for(int i = 0; i < categories.length; i++)
        {
            ContextCategory param = new ContextCategory(getApplicationContext(), categories[i]);
            GetRecipeListTask task = new GetRecipeListTask();
            task.execute(param);
        }
    }
}