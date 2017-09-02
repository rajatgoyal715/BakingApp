package com.rajatgoyal.bakingapp.ui;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.rajatgoyal.bakingapp.R;
import com.rajatgoyal.bakingapp.adapter.DishesAdapter;
import com.rajatgoyal.bakingapp.model.Dish;
import com.rajatgoyal.bakingapp.model.Ingredient;
import com.rajatgoyal.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DishesAdapter.DishItemClickListener {

    private ProgressBar progressBar;

    private RecyclerView dishesList;
    private DishesAdapter dishesAdapter;
    private GridLayoutManager layoutManager;

    private ArrayList<Dish> dishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        dishesList = (RecyclerView) findViewById(R.id.dishes_list);

        layoutManager = new GridLayoutManager(this, 1);
        dishesList.setLayoutManager(layoutManager);

        dishesAdapter = new DishesAdapter(this);
        dishesList.setAdapter(dishesAdapter);

        if (savedInstanceState == null) {
            loadDishes();
        } else {
            dishes = savedInstanceState.getParcelableArrayList("dishes");
            updateDishes();
        }
    }

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(this, DishActivity.class);

        // id is 1-indexed
        intent.putExtra("dish", dishes.get(id-1));

        startActivity(intent);
    }

    public void loadDishes() {

        showProgressbar();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                getString(R.string.json_url),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            int dishCount = response.length();
                            dishes = new ArrayList<>(dishCount);

                            for (int i = 0; i < dishCount; i++) {

                                JSONObject dishObject = response.getJSONObject(i);
                                int id = dishObject.getInt("id");
                                String name = dishObject.getString("name");
                                int servings = dishObject.getInt("servings");

                                JSONArray ingredientsArray = dishObject.getJSONArray("ingredients");
                                int ingCount = ingredientsArray.length();
                                ArrayList<Ingredient> ingredients = new ArrayList<>(ingCount);

                                for (int j = 0; j < ingCount; j++) {
                                    JSONObject ingObject = ingredientsArray.getJSONObject(j);

                                    double quantity = ingObject.getDouble("quantity");
                                    String measure = ingObject.getString("measure");
                                    String ingredient = ingObject.getString("ingredient");

                                    ingredients.add(new Ingredient(quantity, measure, ingredient));
                                }

                                JSONArray stepsArray = dishObject.getJSONArray("steps");
                                int stepCount = stepsArray.length();
                                ArrayList<Step> steps = new ArrayList<>(stepCount);

                                for (int j = 0; j < stepCount; j++) {
                                    JSONObject stepObject = stepsArray.getJSONObject(j);

                                    int stepId = stepObject.getInt("id");
                                    String shortDescription = stepObject.getString("shortDescription");
                                    String description = stepObject.getString("description");
                                    String videoURL = stepObject.getString("videoURL");
                                    String thumbnailURL = stepObject.getString("thumbnailURL");

                                    steps.add(new Step(stepId, shortDescription, description, videoURL, thumbnailURL));
                                }

                                dishes.add(new Dish(id, name, servings, ingredients, steps));
                            }

                            updateDishes();
                        } catch (JSONException e) {
                            // timber log statement
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        requestQueue.add(arrayRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("dishes", dishes);
    }

    public void updateDishes() {
        dishesAdapter.setDishes(dishes);
        dishesAdapter.notifyDataSetChanged();

        showDishes();
    }

    public void showDishes() {
        dishesList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
        dishesList.setVisibility(View.INVISIBLE);
    }
}
