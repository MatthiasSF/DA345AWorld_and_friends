package com.example.matth.p2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * The Activity that starts the application
 *
 * @author Matthias Falk
 */
public class StartActivity extends AppCompatActivity {
    private Switch langSwitch;
    private Button buttonCreate;
    private Button buttonExisting;
    private ActivityController activityController;
    private ServerController serverController;

    /**
     * Basic onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialize();
    }

    /**
     * Initializes the components used in the Activity
     */
    public void initialize() {
        activityController = new ActivityController(this);
        serverController = new ServerController(this);
        this.langSwitch = findViewById(R.id.sa_switch);
        this.buttonCreate = findViewById(R.id.sa_buttonCreateNew);
        this.buttonExisting = findViewById(R.id.sa_buttonExistingGroup);
        buttonExisting.setOnClickListener(new ButtonListener());
        buttonCreate.setOnClickListener(new ButtonListener());
        langSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setText(isChecked);
            }
        });
        setText(false);
    }

    /**
     * Sets the strings used in the Activity to either english or swedish depending on the switch value
     * The default language is set in initialize to english
     *
     * @param language - boolean that decides the language. False = english, true = swedish
     */
    public void setText(Boolean language) {
        if (language) {
            langSwitch.setText(R.string.switch_sv_svenska);
            buttonCreate.setText(R.string.sa_sv_create_group);
            buttonExisting.setText(R.string.sa_sv_pick_existing);
        } else {
            langSwitch.setText(R.string.switch_english);
            buttonCreate.setText(R.string.sa_create_group);
            buttonExisting.setText(R.string.sa_pick_existing);
        }
    }

    /**
     * Listener used by the buttons in the application
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sa_buttonCreateNew) {
                activityController.startCreateNewGroupActivity();
            }
            if (v.getId() == R.id.sa_buttonExistingGroup) {
                serverController.requestGroupsFromServer();
            }
        }
    }
}
