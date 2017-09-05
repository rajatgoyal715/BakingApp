package com.rajatgoyal.bakingapp.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.rajatgoyal.bakingapp.model.Dish;
import com.rajatgoyal.bakingapp.model.Step;
import com.rajatgoyal.bakingapp.ui.StepActivity;

/**
 * Created by rajat on 2/9/17.
 */

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    private static Dish dish;
    private static Step step;
    private static int STEP_ID;
    private static boolean mTwoPane;

    private TextView description;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    Button previousButton, nextButton;

    private boolean videoAvailable;

    private static long playerPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        if(savedInstanceState != null) {
            playerPosition = savedInstanceState.getLong("playerPosition", 0);
        }

        previousButton = (Button) rootView.findViewById(R.id.previous_button);
        nextButton = (Button) rootView.findViewById(R.id.next_button);
        if (mTwoPane) {
            hideButtons();
        } else {
            initializeButtons();
        }

        description = (TextView) rootView.findViewById(R.id.descripton_text_view);
        description.setText(step.getDescription());

        String mediaUrl = "";
        videoAvailable = true;

        if (!step.getVideoUrl().equals("")) {
            mediaUrl = step.getVideoUrl();
        } else {
            if (!step.getThumbnailUrl().equals("")) {
                mediaUrl = step.getThumbnailUrl();
            } else {
                videoAvailable = false;
            }
        }

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        if (videoAvailable) {
            initializeMediaSession();
            initializePlayer(mediaUrl);
        } else {
            hidePlayer();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane) {
            hideSystemUI();
            hideButtons();
            description.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void hideButtons() {
        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void hideSystemUI() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setData(Dish dish1, int STEP_ID1, boolean mTwoPane1) {
        dish = dish1;
        STEP_ID = STEP_ID1;
        step = dish.getSteps().get(STEP_ID);
        mTwoPane = mTwoPane1;
    }

    public void initializeButtons() {
        int stepCount = dish.getSteps().size();

        if (stepCount == STEP_ID + 1) {
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            nextButton.setEnabled(true);
            nextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        if (STEP_ID == 0) {
            previousButton.setEnabled(false);
            previousButton.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            previousButton.setEnabled(true);
            previousButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), StepActivity.TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }


        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getContext(), 0, new Intent(getContext(), StepActivity.class), 0);

        builder.setContentTitle(step.getShortDescription())
                .setContentText(step.getDescription())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.serve)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    private void initializePlayer(String mediaUrl) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mediaUrl), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);

            //restart from the same position after rotation
            mExoPlayer.seekTo(playerPosition);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        mNotificationManager.cancelAll();
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void hidePlayer() {
        mPlayerView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("dish", dish);
        outState.putInt("id", STEP_ID);

        if (videoAvailable && mExoPlayer != null) {
            playerPosition = mExoPlayer.getCurrentPosition();
            outState.putLong("playerPosition", playerPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoAvailable) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
