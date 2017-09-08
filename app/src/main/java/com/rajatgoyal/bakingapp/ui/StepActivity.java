package com.rajatgoyal.bakingapp.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.rajatgoyal.bakingapp.R;
import com.rajatgoyal.bakingapp.fragment.StepDetailFragment;
import com.rajatgoyal.bakingapp.model.Dish;
import com.rajatgoyal.bakingapp.model.Step;

import org.w3c.dom.Text;

import butterknife.BindView;

public class StepActivity extends AppCompatActivity {

    public static final String TAG = StepActivity.class.getSimpleName();

    public static Dish dish;
    public static Step step;
    public static int STEP_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            dish = DishActivity.dish;
            STEP_ID = DishActivity.STEP_ID;
        } else {
            dish = savedInstanceState.getParcelable("dish");
            STEP_ID = savedInstanceState.getInt("id");
        }

        step = dish.getSteps().get(STEP_ID);

        setContentView(R.layout.activity_step);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setData(dish, STEP_ID, false);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("dish", dish);
        outState.putInt("id", STEP_ID);
    }

    public void previousStep(View view) {
        STEP_ID = STEP_ID - 1;
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setData(dish, STEP_ID, false);
        stepDetailFragment.playerPosition = 0;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

    public void nextStep(View view) {
        STEP_ID = STEP_ID + 1;
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setData(dish, STEP_ID, false);
        stepDetailFragment.playerPosition = 0;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
