package edu.weber.w01311060.recipeapp;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.weber.w01311060.recipeapp.db.AppDatabase;
import edu.weber.w01311060.recipeapp.models.ContextCategory;
import edu.weber.w01311060.recipeapp.models.Recipe;
import edu.weber.w01311060.recipeapp.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeListFragment extends Fragment implements RecipeRecyclerAdapter.onRecipeListener, FilterDialog.OnFilterListener
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private boolean isFavorite;
    private View root;
    private RecyclerView rv;
    private Button recipeBtn;
    private RecipeViewModel vm;
    private RecipeRecyclerAdapter adapter;
    private User newUser;
    private LiveData<List<Recipe>> recipes;
    private String[] categories = new String[]{"Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat"};

    public RecipeListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeListFragment newInstance(boolean favorite)
    {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, favorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            isFavorite = getArguments().getBoolean(ARG_PARAM1);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        vm = new ViewModelProvider(getActivity())
                .get(RecipeViewModel.class);
        adapter = new RecipeRecyclerAdapter(new ArrayList<Recipe>(), this);

        return root = inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Toolbar toolbar = root.findViewById(R.id.recipeListToolbar);
        toolbar.setTitle("Recipes");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recipeBtn = root.findViewById(R.id.loadBtn);
        recipeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                loadRecipes();
            }
        });

        rv = root.findViewById(R.id.recipeRecycleView);


        if(rv instanceof RecyclerView)
        {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(adapter);
            rv.setHasFixedSize(false);
        }

        if(isFavorite)
        {
            vm.getUser().observe(getViewLifecycleOwner(), new Observer<User>()
            {
                @Override
                public void onChanged(User user)
                {
                    newUser = user;
                    if (!newUser.getRecipeIds().equals(null))
                    {
                        onFavorite();
                    }
                }
            });
        }
        else
        {
            vm.setFavorite(false);
            vm.getAllRecipes(getContext())
                    .observe(this, new Observer<List<Recipe>>()
                    {
                        @Override
                        public void onChanged(List<Recipe> recipes)
                        {
                            if(recipes != null)
                            {
                                adapter.clear();
                                adapter.addItems(recipes);
                            }
                        }
                    });
        }

        //create database if needed
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                recipes = AppDatabase.getInstance(getContext())
                        .getRecipeDao()
                        .getAll();
                if (recipes.getValue() == null)
                {
                    Log.d("Recipe", "recipes is null");
                }
                else
                {
                    Log.d("Recipe", "recipes is not null");
                }
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.recipelist, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.recipeSearch).getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                searchRecipes(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                searchRecipes(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.filterRecipes:
                FilterDialog fd = new FilterDialog();
                fd.show(getParentFragmentManager(), "FilterDialog");
                fd.setTargetFragment(RecipeListFragment.this, 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeClick(Recipe recipe)
    {
        RecipeDialog dialog = new RecipeDialog();
        dialog.show(getParentFragmentManager(), "RecipeDialog");
        dialog.setRecipe(recipe);
    }

    private void loadRecipes()
    {
        Log.d("Task", "loadRecipes");
        for(int i = 0; i < categories.length; i++)
        {
            ContextCategory param = new ContextCategory(getContext(), categories[i]);
            GetRecipeListTask task = new GetRecipeListTask();
            task.execute(param);
        }
    }
    private void searchRecipes(String query)
    {
        query = "%" + query + "%";

        vm.searchRecipes(query, getContext())
                .observe(this, new Observer<List<Recipe>>()
                {
                    @Override
                    public void onChanged(List<Recipe> recipes)
                    {
                        if(recipes != null)
                        {
                            adapter.clear();
                            adapter.addItems(recipes);
                        }
                    }
                });
    }

    @Override
    public void onFilter(List<String> categories)
    {
        //search recipes

        StringBuilder builder = new StringBuilder(" SELECT * FROM Recipe ");
        if (categories.size() > 0 && !isFavorite)
        {
            builder.append(" WHERE category IN (");
        }
        else
        {
            builder.append(" WHERE idMeal IN (");
            for (Map.Entry<String, String> entry : newUser.getRecipeIds().entrySet())
            {
                builder.append(entry.getValue());
                builder.append(" ,");
            }
            builder.deleteCharAt(builder.length()-1);
            builder.deleteCharAt(builder.length()-1);
            builder.append(") AND category IN (");
        }
        for (int i = 0; i < categories.size(); i++)
        {
            if (i != categories.size() - 1)
            {
                builder.append("?, ");
            }
            else
            {
                builder.append("?)");
            }
        }
        Log.d("Filter", builder.toString());
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(builder.toString(), categories.toArray());

        vm.filterRecipes(query, getContext())
                .observe(this, new Observer<List<Recipe>>()
                {
                    @Override
                    public void onChanged(List<Recipe> recipes)
                    {
                        if(recipes != null)
                        {
                            adapter.clear();
                            adapter.addItems(recipes);
                        }
                    }
                });

    }
    public void onFavorite()
    {
        StringBuilder builder = new StringBuilder(" SELECT * FROM Recipe ");

        if (!newUser.getRecipeIds().isEmpty())
        {
            builder.append(" WHERE idMeal IN (");
        }
        else
        {
            builder.append("WHERE idMeal = 'zzzzz'");
        }
        for(int i = 0; i < newUser.getRecipeIds().size(); i++)
        {
            if (i != newUser.getRecipeIds().size() - 1)
            {
                builder.append("?, ");
            }
            else
            {
                builder.append("?)");
            }
        }
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(builder.toString(), newUser.getRecipeIds().values().toArray());
        vm.setFavorite(true);
        vm.filterRecipes(query, getContext())
                .observe(this, new Observer<List<Recipe>>()
                {
                    @Override
                    public void onChanged(List<Recipe> recipes)
                    {
                        if(recipes != null)
                        {
                            adapter.clear();
                            adapter.addItems(recipes);
                        }
                    }
                });
    }
}