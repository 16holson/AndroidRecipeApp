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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    private ArrayAdapter<Ingredient> arrayAdapter;
    private FirebaseListAdapter<Ingredient> adapter;

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

        vm.getUser().observe(getViewLifecycleOwner(), new Observer<User>()
        {
            @Override
            public void onChanged(User user)
            {
                newUser = user;

            }
        });


        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initListViewData();

        Toolbar toolbar = root.findViewById(R.id.groceryToolbar);
        toolbar.setTitle(R.string.grocerylist);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        addBtn = root.findViewById(R.id.addGroceryItem);
        addBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextInputLayout inputLayout = new TextInputLayout(getActivity());
                EditText input = new EditText(getActivity());
                input.setBackground(null);
                inputLayout.setPadding(getResources().getDimensionPixelOffset(R.dimen.dp_16), 0, getResources().getDimensionPixelOffset(R.dimen.dp_16), 0);
                inputLayout.setHelperText(getString(R.string.ingredient));
                inputLayout.addView(input);

                //Add ingredient to Grocery List
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.additem)
                        .setMessage(R.string.enteringredient)
                        .setView(inputLayout)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
//                                newUser.addGroceryItem(input.getText().toString(), false);
                                newUser.addGroceryItem(new Ingredient(input.getText().toString(), "false"));
                                vm.setUser(newUser);
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

    }

    public void initListViewData()
    {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();;
        DatabaseReference reference = rootNode.getReference("users");
        Query query = reference.child(newUser.getEmail()).child("groceryList");
        FirebaseListOptions<Ingredient> options = new FirebaseListOptions.Builder<Ingredient>()
                .setQuery(query, Ingredient.class)
                .setLayout(R.layout.groceryitem)
                .setLifecycleOwner(getActivity())
                .build();

        //Adapter for Grocery List, auto updates when Firebase Database changes
        adapter = new FirebaseListAdapter<Ingredient>(options)
        {
            @Override
            protected void populateView(@NonNull View v, @NonNull Ingredient model, int position)
            {
                TextView text = v.findViewById(R.id.groceryItem);
                CheckBox box = v.findViewById(R.id.groceryBox);
                text.setText(model.getName());
                box.setChecked(Boolean.valueOf(model.isActive()));

                v.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        CheckBox checkbox = view.findViewById(R.id.groceryBox);
                        if(checkbox.isChecked())
                        {
                            checkbox.setChecked(false);
                        }
                        else
                        {
                            checkbox.setChecked(true);
                        }
                        Ingredient newIngredient = new Ingredient(model.getName(), String.valueOf(checkbox.isChecked()));
                        newUser.updateGroceryItem(newIngredient);
                        vm.setUser(newUser);
                    }
                });
            }
        };
        listView.setAdapter(adapter);

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
                TextInputLayout inputLayout = new TextInputLayout(getActivity());
                EditText input = new EditText(getActivity());
                input.setBackground(null);
                inputLayout.setPadding(getResources().getDimensionPixelOffset(R.dimen.dp_16), 0, getResources().getDimensionPixelOffset(R.dimen.dp_16), 0);
                inputLayout.setHelperText(getString(R.string.email));
                inputLayout.addView(input);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                if(newUser.getSync() == null)
                {
                    builder2.setTitle(R.string.synclists)
                            .setMessage(R.string.enteremail)
                            .setView(inputLayout)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton(R.string.sync, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    //set user.sync to uid of wanted user
                                    newUser.setSync(input.getText().toString().replace(".", "").replace("#", "").replace("$", "").trim());
                                    vm.setUser(newUser);
                                    dialogInterface.dismiss();
                                    Toast.makeText(getActivity(), R.string.synced, Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
                else
                {
                    builder2.setTitle(R.string.desynclist)
                            .setMessage(R.string.confirmdesync)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton(R.string.desync, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();;
                                    DatabaseReference reference = rootNode.getReference("users");
                                    reference.child(newUser.getSync()).child("sync").setValue(null);
                                    newUser.setSync(null);
                                    vm.setUser(newUser);
                                    dialogInterface.dismiss();
                                    Toast.makeText(getActivity(), R.string.desynced, Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }

                return true;
            case R.id.delete:
                //pull up dialog asking if they want to delete selected items
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.deleteselected);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //delete selected
                        for (int k = 0; k < listView.getCount(); k++)
                        {
                            Ingredient item = (Ingredient) listView.getItemAtPosition(k);
                            if (Boolean.valueOf(item.isActive()))
                            {
                                newUser.removeGroceryItem(item.getName());

                            }
                        }
                        vm.setUser(newUser);
                    }
                });
                builder.show();
        }

        return super.onOptionsItemSelected(item);
    }
}