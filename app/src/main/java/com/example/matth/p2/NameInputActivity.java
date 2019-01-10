package com.example.matth.p2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * Activity that is used when the user wants to enter an existing group.
 * Lets the user enter his/her name. The name will be displayed as an title on the marker
 *
 * @author Matthias Falk
 */
public class NameInputActivity extends AppCompatActivity {
    private String groupName;
    private Button button;
    private Switch aSwitch;
    private EditText editText;
    private ServerController serverController;

    /**
     * Basic onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);
        initialize();
    }

    /**
     * Initializes the components used in the Activity
     */
    private void initialize() {
        serverController = new ServerController(this);
        groupName = getIntent().getStringExtra("groupname");
        button = findViewById(R.id.nameInputButton);
        aSwitch = findViewById(R.id.nameInputSwitch);
        editText = findViewById(R.id.nameInputEditText);
        button.setOnClickListener(new ButtonListener());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    private void setText(boolean language) {
        if (language) {
            button.setText(R.string.frag_button);
            aSwitch.setText(R.string.switch_sv_svenska);
            editText.setHint(R.string.frag_hint_sv);
        } else {
            button.setText(R.string.frag_button);
            aSwitch.setText(R.string.switch_english);
            editText.setHint(R.string.frag_hint);
        }
    }

    /**
     * Lister used by the button. Requests the server to register to the chosen group via the ServerController
     */
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            serverController.requestToRegister(groupName, String.valueOf(editText.getText()));
        }
    }
}
