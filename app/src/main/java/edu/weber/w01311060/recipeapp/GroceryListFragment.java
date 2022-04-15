package edu.weber.w01311060.recipeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.wifi.aware.AwareResources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.weber.w01311060.recipeapp.models.Ingredient;
import edu.weber.w01311060.recipeapp.models.Recipe;
import edu.weber.w01311060.recipeapp.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroceryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroceryListFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View root;
    private ListView listView;
    private User newUser;
    private RecipeViewModel vm;
    private FloatingActionButton addBtn;
    private Button allBtn;
    private Button remainBtn;
    private ArrayAdapter<Ingredient> arrayAdapter;

    private Ingredient[] ingredients;

    public GroceryListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroceryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroceryListFragment newInstance(String param1, String param2)
    {
        GroceryListFragment fragment = new GroceryListFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        root = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        vm = new ViewModelProvider(getActivity())
                .get(RecipeViewModel.class);

        listView = root.findViewById(R.id.groceryListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                CheckedTextView v = (CheckedTextView) view;
                boolean isChecked = v.isChecked();
                Ingredient ingredient = (Ingredient) listView.getItemAtPosition(i);
                ingredient.setActive(isChecked);
            }
        });

        vm.getUser().observe(getViewLifecycleOwner(), new Observer<User>()
        {
            @Override
            public void onChanged(User user)
            {
                newUser = user;
                ingredients = new Ingredient[newUser.getGroceryList().size()];
                initListViewData();
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Toolbar toolbar = root.findViewById(R.id.groceryToolbar);
        toolbar.setTitle("Grocery List");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        addBtn = root.findViewById(R.id.addGroceryItem);
        allBtn = root.findViewById(R.id.allItems);
        remainBtn = root.findViewById(R.id.remainingItems);
        addBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextInputLayout inputLayout = new TextInputLayout(getActivity());
                EditText input = new EditText(getActivity());
                input.setBackground(null);
                inputLayout.setPadding(getResources().getDimensionPixelOffset(R.dimen.dp_16), 0, getResources().getDimensionPixelOffset(R.dimen.dp_16), 0);
                inputLayout.setHint("Ingredient");
                inputLayout.addView(input);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Item")
                        .setMessage("Please enter an ingredient to add")
                        .setView(inputLayout)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        })
                                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                newUser.addGroceryItem(input.getText().toString(), "1");
                                vm.setUser(newUser);
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        remainBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ArrayList<Ingredient> arrayList = new ArrayList<Ingredient>(Arrays.asList(ingredients));
                Collections.sort(arrayList);
                //set checked on new listview based on arraylist
                arrayAdapter = new ArrayAdapter<Ingredient>(getActivity(), android.R.layout.simple_list_item_multiple_choice, arrayList);
                listView.setAdapter(arrayAdapter);
                remainBtn.setBackgroundColor(getResources().getColor(R.color.green));
                allBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        allBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                remainBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                allBtn.setBackgroundColor(getResources().getColor(R.color.green));
            }
        });

    }

    public void initListViewData()
    {
        //get saved ingredients from firebase, check if there is nothing
        int j = 0;
        for (Map.Entry<String, String> entry : newUser.getGroceryList().entrySet())
        {
            Ingredient ingredient = new Ingredient(entry.getKey());
            ingredients[j] = ingredient;
            j++;
        }

        arrayAdapter = new ArrayAdapter<Ingredient>(getActivity(), android.R.layout.simple_list_item_multiple_choice, ingredients);

        listView.setAdapter(arrayAdapter);

        for (int i = 0; i < ingredients.length; i++)
        {
            listView.setItemChecked(i, ingredients[i].isActive());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.grocerymenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.sync:
                //pull up dialog asking for email of other person
                return true;
            case R.id.delete:
                //pull up dialog asking if they want to delete selected items
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Selected?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //delete selected
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        Log.d("Grocery", "Delete");
                        for (int j = 0; j < listView.getAdapter().getCount(); j++)
                        {
                            Log.d("Grocery", "listViewItem: " + listView.getItemAtPosition(j) + " checked: " + checked.get(j));
                            if(checked.get(j))
                            {
                                Log.d("Grocery", "Item deleted: " + listView.getItemAtPosition(j).toString());
                                newUser.removeGroceryItem(listView.getItemAtPosition(j).toString());
                            }
                        }
                        vm.setUser(newUser);
                        initListViewData();
                    }
                });
                builder.show();
        }

        return super.onOptionsItemSelected(item);
    }
}