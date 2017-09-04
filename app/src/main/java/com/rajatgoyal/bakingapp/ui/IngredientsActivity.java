package com.rajatgoyal.bakingapp.ui;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.rajatgoyal.bakingapp.R;
import com.rajatgoyal.bakingapp.adapter.IngredientsAdapter;
import com.rajatgoyal.bakingapp.model.Ingredient;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity {

    private RecyclerView ingredientsList;
    private IngredientsAdapter ingredientsAdapter;
    private LinearLayoutManager layoutManager;

    private ArrayList<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        ingredientsList = (RecyclerView) findViewById(R.id.ingredients_list);

        layoutManager = new LinearLayoutManager(this);
        ingredientsList.setLayoutManager(layoutManager);

        ingredientsAdapter = new IngredientsAdapter();
        ingredientsAdapter.setIngredients(ingredients);
        ingredientsList.setAdapter(ingredientsAdapter);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            ingredients = bundle.getParcelableArrayList("ingredients");
            updateIngredientsList();
        } else {
            ingredients = savedInstanceState.getParcelableArrayList("ingredients");
            updateIngredientsList();
        }

    }

    public void updateIngredientsList() {
        ingredientsAdapter.setIngredients(ingredients);
        ingredientsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ingredients", ingredients);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
