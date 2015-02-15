package com.dummybutton;

import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.HashMap;
import java.util.Random;

import static com.dummybutton.Media.funnySoundMedia;
import static com.dummybutton.Media.pickupLinesMedia;

public class MainActivity extends ActionBarActivity implements OnItemSelectedListener,
        OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener {

    private final StateListDrawable states = new StateListDrawable();
    private MediaPlayer mediaPlayer;
    private Spinner spinner;

    private ArrayAdapter<String> pickupLinesAdapter;
    private ArrayAdapter<String> funnySoundAdapter;

    private HashMap<String, Integer> pickupLines;
    private String[] pickupLinesItemTitles;
    private HashMap<String, Integer> funnySounds;
    private String[] funnySoundTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        // Pickup lines
        pickupLinesItemTitles = getResources().getStringArray(R.array.pickupLinesItemTitles);
        pickupLines = new HashMap<>();
        for (int i = 0; i < pickupLinesItemTitles.length; i++) {
            pickupLines.put(
                    pickupLinesItemTitles[i], pickupLinesMedia[i]);
        }

        // Funny sounds
        funnySoundTitles = getResources().getStringArray(R.array.funnySoundTitles);
        funnySounds = new HashMap<>();
        for (int i = 0; i < funnySoundTitles.length; i++) {
            funnySounds.put(
                    funnySoundTitles[i], funnySoundMedia[i]);
        }

        pickupLinesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pickupLinesItemTitles);
        funnySoundAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, funnySoundTitles);
        pickupLinesAdapter.setDropDownViewResource(R.layout.list_item);
        funnySoundAdapter.setDropDownViewResource(R.layout.list_item);

        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(pickupLinesAdapter);
        spinner.setOnItemSelectedListener(this);

        // Buttons
        Button bigRed = (Button) findViewById(R.id.bigRedButton);
        RadioButton pickupRadioButton = (RadioButton) findViewById(R.id.pickup);
        RadioButton funnyRadioButton = (RadioButton) findViewById(R.id.funnies);
        Button nextButton = (Button) findViewById(R.id.next);
        Button backButton = (Button) findViewById(R.id.back);
        Button randomButton = (Button) findViewById(R.id.random);

        bigRed.setOnClickListener(new BigRedBtnCLicked());

        nextButton.setOnClickListener(new NextButtonClicked());
        backButton.setOnClickListener(new BackButtonClicked());
        randomButton.setOnClickListener(new RandomButtonClicked());

        pickupRadioButton.setOnCheckedChangeListener(this);
        funnyRadioButton.setOnCheckedChangeListener(this);

        // Big Red Click Settings
        states.addState(new int[]{android.R.attr.state_pressed},
                getResources().getDrawable(R.drawable.clicked));
        states.addState(new int[]{android.R.attr.state_focused},
                getResources().getDrawable(R.drawable.clicked));
        states.addState(new int[]{},
                getResources().getDrawable(R.drawable.unclicked)); // Normal State
        bigRed.setBackgroundDrawable(states);
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SpinnerAdapter spinnerAdapter = spinner.getAdapter();

        if (spinnerAdapter.equals(pickupLinesAdapter)) {
            stopPlaying();
            int media = pickupLines.get(pickupLinesItemTitles[position]);
            play(media);
        } else if (spinnerAdapter.equals(funnySoundAdapter)) {
            stopPlaying();
            int media = funnySounds.get(funnySoundTitles[position]);
            play(media);
        }
    }

    private void play(int media) {
        mediaPlayer = MediaPlayer.create(this, media);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean arg1) {

        switch (button.getId()) {
            case R.id.pickup:
                spinner.setAdapter(funnySoundAdapter);
                break;
            case R.id.funnies:
                spinner.setAdapter(pickupLinesAdapter);
                break;
        }
    }

    /**
     * OnClickListeners
     */

    private class NextButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int current = spinner.getSelectedItemPosition() + 1;
            spinner.setSelection(current++);
            if (spinner.getAdapter().equals(pickupLinesAdapter)) {
                if (current == 17) {
                    spinner.setSelection(0);
                }
            } else if (spinner.getAdapter().equals(funnySoundAdapter)) {
                if (current == 10) {
                    spinner.setSelection(0);
                }
            }
        }
    }

    private class BackButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int current = spinner.getSelectedItemPosition() - 1;
            spinner.setSelection(current--);
            if (spinner.getAdapter().equals(pickupLinesAdapter)) {
                if (current == -2) {
                    spinner.setSelection(15);
                }
            } else if (spinner.getAdapter().equals(funnySoundAdapter)) {
                if (current == -2) {
                    spinner.setSelection(8);
                }
            }
        }
    }

    private class RandomButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Random random = new Random();
            spinner.setSelection(random.nextInt(15));

            if (spinner.getAdapter().equals(pickupLinesAdapter)) {
                spinner.setSelection(random.nextInt(15));
            } else if (spinner.getAdapter().equals(funnySoundAdapter)) {
                spinner.setSelection(random.nextInt(8));
            }
        }
    }

    private class BigRedBtnCLicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mediaPlayer.start();
        }
    }
}
