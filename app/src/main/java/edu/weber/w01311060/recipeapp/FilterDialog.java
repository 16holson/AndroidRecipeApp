package edu.weber.w01311060.recipeapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterDialog extends DialogFragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View root;
    private ChipGroup group;
    private List<String> categoryList;
    private OnFilterListener mCallback;

    public FilterDialog()
    {
        // Required empty public constructor
    }

    public interface OnFilterListener
    {
        public void onFilter(List<String> categories);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        try
        {
            mCallback = (OnFilterListener) getTargetFragment();
        }
        catch (ClassCastException e)
        {
            Log.d("FilterDialog", "ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterDialog newInstance(String param1, String param2)
    {
        FilterDialog fragment = new FilterDialog();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.AppTheme_DialogAnimation);

        Toolbar toolbar = root.findViewById(R.id.filterToolbar);
        toolbar.setTitle("Filter Recipes");
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
    public void onResume()
    {
        super.onResume();
        group = root.findViewById(R.id.categoryGroup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.filtermenu, menu);
        menu.findItem(R.id.recipeSearch).setVisible(false);
        menu.findItem(R.id.filterRecipes).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.saveFilter:
                List<Integer> ids = group.getCheckedChipIds();
                categoryList = new ArrayList<String>();
                for (int i = 0; i < ids.size(); i++)
                {
                    Chip chip = root.findViewById(ids.get(i));
                    categoryList.add(chip.getText().toString());
                }
                mCallback.onFilter(categoryList);
                dismiss();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


}