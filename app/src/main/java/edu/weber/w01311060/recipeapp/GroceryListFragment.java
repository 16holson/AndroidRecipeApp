package edu.weber.w01311060.recipeapp;

import android.net.wifi.aware.AwareResources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.List;

import edu.weber.w01311060.recipeapp.models.Ingredient;

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
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_grocery_list, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

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
        initListViewData();

    }

    public void initListViewData()
    {
        //get saved ingredients from firebase, check if there is nothing
        Ingredient[] ingredients = new Ingredient[]{new Ingredient("Eggs"), new Ingredient("Milk"), new Ingredient("Potato"), new Ingredient("Lemon")};

        ArrayAdapter<Ingredient> arrayAdapter = new ArrayAdapter<Ingredient>(getActivity(), android.R.layout.simple_list_item_multiple_choice, ingredients);

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
        }

        return super.onOptionsItemSelected(item);
    }
}