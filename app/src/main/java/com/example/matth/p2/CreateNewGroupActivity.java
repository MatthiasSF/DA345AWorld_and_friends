package com.example.matth.p2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * An Activity that let's the user create an new group and add it to the server
 *
 * @author Matthias
 */
public class CreateNewGroupActivity extends AppCompatActivity {
    private Switch langSwitch;
    private EditText etName;
    private EditText etGroup;
    private Button button;
    private ServerController serverController;

    /**
     * Basic onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        initialize();
    }

    /**
     * Initializes the components used in the Activity
     */
    public void initialize() {
        serverController = new ServerController(this);
        langSwitch = findViewById(R.id.createNew_switch);
        etName = findViewById(R.id.createNew_editTextName);
        etGroup = findViewById(R.id.createNew_EditTextGroupName);
        button = findViewById(R.id.createNew_Button);
        button.setOnClickListener(new ButtonListener());
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
    public void setText(boolean language) {
        if (language) {
            etName.setHint(R.string.createNew_sv_hintName);
            etGroup.setHint(R.string.createNew_sv_hintGroup);
            button.setText(R.string.createNew_sv_Button);
            langSwitch.setText(R.string.switch_sv_svenska);
        } else {
            etName.setHint(R.string.createNew_hintName);
            etGroup.setHint(R.string.createNew_hintGroup);
            button.setText(R.string.createNew_Button);
            langSwitch.setText(R.string.switch_english);
        }
    }

    /**
     * Listener used by the button. Gets the inputs the user has made and registers the group to the server.
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String name = String.valueOf(etName.getText());
            String groupName = String.valueOf(etGroup.getText());
            serverController.requestToRegister(groupName, name);
        }
    }
}
