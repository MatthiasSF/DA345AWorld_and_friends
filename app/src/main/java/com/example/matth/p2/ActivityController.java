package com.example.matth.p2;

import android.content.Context;
import android.content.Intent;

/**
 * Controller that switches between Activitys
 *
 * @author Matthias Falk
 */
public class ActivityController {
    private Context context;
    Intent intent;

    /**
     * @param context
     */
    public ActivityController(Context context) {
        this.context = context;
    }

    /**
     * Starts the Activity that handles the map.
     *
     * @param ID - the id of the user that connects
     */
    public void startMapActivity(String ID) {
        intent = new Intent(context, MapsActivity.class);
        intent.putExtra("ID", ID);
        context.startActivity(intent);
    }

    /**
     * Starts the Activity that displays the groups that is registered on the server
     *
     * @param groups - An array with the groups
     */
    public void startExistingGroupActivity(String[] groups) {
        intent = new Intent(context, ExistingGroupActivity.class);
        if (groups != null) {
            intent.putExtra("groups", groups);
        }
        context.startActivity(intent);
    }

    /**
     * Starts the Activity that let's the user create an new group and register it on the server
     */
    public void startCreateNewGroupActivity() {
        intent = new Intent(context, CreateNewGroupActivity.class);
        context.startActivity(intent);
    }

    /**
     * Starts an activity that let's the user enter his/her's name when picking an existing group
     *
     * @param groupName - the group that the user chose to join
     */
    public void startNameInputActivity(String groupName) {
        intent = new Intent(context, NameInputActivity.class);
        if (groupName != null) {
            intent.putExtra("groupname", groupName);
        }
        context.startActivity(intent);
    }
}
