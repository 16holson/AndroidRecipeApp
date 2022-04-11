package edu.weber.w01311060.recipeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.stream.Collectors;

import edu.weber.w01311060.recipeapp.models.Recipe;
import edu.weber.w01311060.recipeapp.models.RecipeDetails;
import edu.weber.w01311060.recipeapp.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDialog extends DialogFragment implements GetRecipeTask.AsyncResponse
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View root;
    private Recipe recipe;
    private String[] instructions;
    private String[] ingredients;
    private ListView instructionList;
    private ListView ingredientList;
    private ArrayAdapter instructionsAdapter;
    private ArrayAdapter ingredientsAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RecipeViewModel vm;
    private User newUser;
    private boolean favorited = false;
    private Toolbar toolbar;

    public RecipeDialog()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDialog newInstance(String param1, String param2)
    {
        RecipeDialog fragment = new RecipeDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        vm = new ViewModelProvider(getActivity())
                .get(RecipeViewModel.class);

        vm.getUser().observe(getViewLifecycleOwner(), new Observer<User>()
        {
            @Override
            public void onChanged(User user)
            {
                newUser = user;
            }
        });
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_recipe_dialog, container, false);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = prefs.edit();
        prefEdit.putBoolean("fav", favorited);
        prefEdit.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        favorited = prefs.getBoolean("fav", false);
        toolbar = root.findViewById(R.id.recipeToolbar);
        toolbar.setTitle(recipe.getStrMeal());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.AppTheme_DialogAnimation);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.recipemenu, menu);
        MenuItem item = menu.findItem(R.id.recipeFavorite);
        if (newUser.getRecipeIds().containsValue(String.valueOf(recipe.getIdMeal())))
        {
            item.setIcon(R.drawable.ic_baseline_favorite_24);
            item.setTitle(R.string.favorited);
        }
        else
        {
            item.setIcon(R.drawable.ic_baseline_favorite_border_24);
            item.setTitle(R.string.favorite);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.recipeFavorite:
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("users");
                    if(item.getTitle() == getString(R.string.favorite))
                    {
                        //favorite recipe
                        item.setIcon(R.drawable.ic_baseline_favorite_24);
                        item.setTitle(R.string.favorited);
                        newUser.addRecipeId(recipe.getStrMeal(),String.valueOf(recipe.getIdMeal()));
                        vm.setUser(newUser);
                        favorited = true;
                        Log.d("Fav", "user: " + newUser.toString());
                        //update database
                    }
                    else
                    {
//                      unfavorite recipe
                        item.setIcon(R.drawable.ic_baseline_favorite_border_24);
                        item.setTitle(R.string.favorite);
                        newUser.getRecipeIds().remove(recipe.getStrMeal());
                        vm.setUser(newUser);
                        favorited = false;
                        Log.d("Fav", "user: " + newUser.toString());
                        //update database
                    }





        }
        return super.onOptionsItemSelected(item);
    }

    public void setRecipe(Recipe recipe)
    {
        this.recipe = recipe;
        GetRecipeTask task = new GetRecipeTask(this);
        task.execute(recipe.getIdMeal());
    }

    public void setAdapters()
    {
        instructionsAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                instructions
        );
        ingredientsAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                ingredients
        );
    }

    @Override
    public void processFinished(RecipeDetails output)
    {
        //set lists change instructions to a list
        //instructions = output.getInstructions();
        instructions = output.getInstructions().split("\\r\\n");
        ingredients = output.getIngredients().toArray(new String[output.getIngredients().size()]);
        ingredientList = root.findViewById(R.id.ingredientList);
        instructionList = root.findViewById(R.id.instructionList);

        setAdapters();

        instructionList.setAdapter(instructionsAdapter);
        ingredientList.setAdapter(ingredientsAdapter);

        TextView instructionTitle = new TextView(getContext());
        TextView ingredientTitle = new TextView(getContext());
        instructionTitle.setText(R.string.instructions);
        instructionTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);
        ingredientTitle.setText(R.string.ingredients);
        ingredientTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);
        instructionList.addHeaderView(instructionTitle);
        ingredientList.addHeaderView(ingredientTitle);


    }
}