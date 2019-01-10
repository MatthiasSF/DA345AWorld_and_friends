package com.example.matth.p2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Activity that displays all the groups in the server
 *
 * @author Matthias Falk
 */
public class ExistingGroupActivity extends AppCompatActivity {
    private ListView groupList;
    private TextView tv;
    private String[] groups;
    private Switch langSwitch;
    private ActivityController acController;

    /**
     * Basic onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_group);
        initialize();
    }

    /**
     * Initializes the components used in the Activity
     */
    public void initialize() {
        acController = new ActivityController(this);
        groupList = findViewById(R.id.exGroup_lv);
        tv = findViewById(R.id.exGroup_tv);
        langSwitch = findViewById(R.id.exGroup_switch);
        groups = getIntent().getStringArrayExtra("groups");
        if (groups == null) {
            groups = new String[0];
        }
        groupList.setAdapter(new ExistingGroupListAdapter(this, groups));
        groupList.setOnItemClickListener(new ClickListener());
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
            tv.setText(R.string.exGroup_sv_text);
            langSwitch.setText(R.string.switch_sv_svenska);
        } else {
            tv.setText(R.string.exGroup_text);
            langSwitch.setText(R.string.switch_english);
        }
    }

    /**
     * Listener used by the ListView. Sends the selected group to the ActivityController.
     */
    private class ClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            acController.startNameInputActivity(groups[position]);
        }
    }
}
