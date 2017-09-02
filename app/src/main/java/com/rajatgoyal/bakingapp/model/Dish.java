package com.rajatgoyal.bakingapp.model;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by rajat on 31/8/17.
 */

public class Dish implements Parcelable{

    private int id;
    private String name;
    private int servings;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    public Dish(int id, String name, int servings, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {

        @Override
        public Dish createFromParcel(Parcel source) {
            return new Dish(source);
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }

    };

    private Dish(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
    }
}
