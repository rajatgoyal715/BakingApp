package com.rajatgoyal.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rajatgoyal.bakingapp.R;
import com.rajatgoyal.bakingapp.fragment.IngredientFragment;
import com.rajatgoyal.bakingapp.fragment.StepDetailFragment;
import com.rajatgoyal.bakingapp.fragment.StepListFragment;
import com.rajatgoyal.bakingapp.model.Dish;
import com.rajatgoyal.bakingapp.widget.DishWidgetProvider;

public class DishActivity extends AppCompatActivity implements
        StepListFragment.OnIngredientClickListener, StepListFragment.OnStepClickListener {

    public static boolean mTwoPane;
    public static Dish dish;
    public static int STEP_ID;
    public static int DISH_ID;

    private StepDetailFragment stepDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra("item")) {
                int position = getIntent().getIntExtra("item", 0);
                DISH_ID = position;
                dish = MainActivity.dishes.get(position);
            } else {
                DISH_ID = MainActivity.DISH_ID;
                dish = MainActivity.dishes.get(DISH_ID);
            }
        } else {
            dish = savedInstanceState.getParcelable("dish");
        }

        setContentView(R.layout.activity_dish);
        getSupportActionBar().setTitle(dish.getName());

        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;

            FragmentManager fragmentManager = getSupportFragmentManager();

            stepDetailFragment = new StepDetailFragment();
            STEP_ID = 0;
            if (savedInstanceState != null) {
                STEP_ID = savedInstanceState.getInt("STEP_ID");
                if (STEP_ID == -1) {
                    IngredientFragment ingredientFragment = new IngredientFragment();
                    ingredientFragment.setIngredients(dish.getIngredients());

                    fragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, ingredientFragment)
                            .commit();
                } else {
                    stepDetailFragment.setData(dish, STEP_ID, mTwoPane);

                    fragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, stepDetailFragment)
                            .commit();
                }
            } else {
                stepDetailFragment.setData(dish, STEP_ID, mTwoPane);

                fragmentManager.beginTransaction()
                        .add(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("dish", dish);
        if (mTwoPane) {
            outState.putInt("STEP_ID", STEP_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (item.getItemId() == R.id.action_add_to_widget) {
            SharedPreferences sharedPreferences = getSharedPreferences("dish_details", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("dish_id", DISH_ID).commit();
            updateWidget();
            Toast.makeText(this, "Added to Widget", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateWidget() {
        Intent intent = new Intent(this,DishWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), DishWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    @Override
    public void onIngredientsSelected() {
        if (mTwoPane) {

            STEP_ID = -1;
            IngredientFragment ingredientFragment = new IngredientFragment();
            ingredientFragment.setIngredients(dish.getIngredients());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, ingredientFragment)
                    .commit();

        } else {
            Intent intent = new Intent(this, IngredientsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStepSelected(int id) {
        STEP_ID = id;
        if (mTwoPane) {
            stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setData(dish, id, mTwoPane);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            startActivity(intent);
        }
    }
}
