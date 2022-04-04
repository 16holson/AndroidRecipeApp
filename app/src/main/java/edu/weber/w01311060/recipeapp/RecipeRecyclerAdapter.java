package edu.weber.w01311060.recipeapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.weber.w01311060.recipeapp.models.Recipe;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder>
{
    private final List<Recipe> values;
    private onRecipeListener mOnRecipeListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe__view, parent, false);
        return new ViewHolder(view, mOnRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Recipe recipe = values.get(position);
        if(recipe != null)
        {
            holder.recipeItem = recipe;
            holder.recipeInfo.setText(recipe.getStrMeal());
            holder.recipeInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mOnRecipeListener.onRecipeClick(recipe);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return values.size();
    }

    public interface onRecipeListener
    {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeRecyclerAdapter(List<Recipe> values, onRecipeListener onRecipeListener)
    {
        this.values = values;
        this.mOnRecipeListener = onRecipeListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public View view;
        public TextView recipeInfo;
        public Recipe recipeItem;
        public onRecipeListener onRecipeListener;
        public ViewHolder(View itemView, onRecipeListener onRecipeListener)
        {
            super(itemView);
            this.view = itemView;
            recipeInfo = view.findViewById(R.id.recipeInfo);
            this.onRecipeListener = onRecipeListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onRecipeListener.onRecipeClick(values.get(getAdapterPosition()));
        }
    }

    public void addItems(List<Recipe> recipes)
    {
        values.clear();
        values.addAll(recipes);
        notifyDataSetChanged();
    }

}