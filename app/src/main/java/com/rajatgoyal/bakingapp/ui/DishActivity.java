package com.rajatgoyal.bakingapp.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rajatgoyal.bakingapp.R;
import com.rajatgoyal.bakingapp.adapter.StepsAdapter;
import com.rajatgoyal.bakingapp.model.Dish;

public class DishActivity extends AppCompatActivity implements StepsAdapter.StepItemClickListener{

    private boolean mTwoPane;

    private static Dish dish;

    private RecyclerView stepsList;
    private StepsAdapter stepsAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        mTwoPane = false;

        if (dish == null) {
            if (savedInstanceState == null) {
                dish = getIntent().getParcelableExtra("dish");
            } else {
                dish = savedInstanceState.getParcelable("dish");
            }
        }

        getSupportActionBar().setTitle(dish.getName());

        stepsList = (RecyclerView) findViewById(R.id.steps_list);

        layoutManager = new LinearLayoutManager(this);
        stepsList.setLayoutManager(layoutManager);

        stepsAdapter = new StepsAdapter(this);
        stepsAdapter.setSteps(dish.getSteps());
        stepsList.setAdapter(stepsAdapter);
    }

    public void openIngredients(View view) {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.putParcelableArrayListExtra("ingredients", dish.getIngredients());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("dish", dish);
    }

    @Override
    public void onClick(int id) {
        if (mTwoPane) {

        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra("dish", dish);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
