package com.example.matth.p2;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.JsonWriter;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The controller interprets the communication with the server.
 *
 * @author Matthias Falk
 */
public class ServerController {
    private TCPConnection tcpConnection;
    private Listener listener;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    private boolean updating = false;
    private String group;
    private String ID;
    private Location location;

    /**
     * Constructor that connects to the server via TCPConnection
     *
     * @param context
     */
    public ServerController(Context context) {
        listener = new Listener(context);
        tcpConnection = new TCPConnection("195.178.227.53", 7117, listener);
        tcpConnection.connect();
    }

    /**
     * Sets up the timer
     */
    public void setTimers() {
        updateTimer = new Timer();
        updateTimerTask = new UpdateTimerTask();
        updateTimer.schedule(updateTimerTask, 0, 60000);

    }

    /**
     * Sets the id to the user's active id
     *
     * @param ID - id of the user
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the group to the active group
     *
     * @param group - the users group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * requests the server to get all active groups stored at the server
     */
    public void requestGroupsFromServer() {
        if (updating) {
            updateTimer.cancel();
            updateTimerTask.cancel();
            updating = false;
        }
        if (tcpConnection == null) {
            tcpConnection = new TCPConnection("195.178.227.53", 7117, listener);
            tcpConnection.connect();
        }
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject().name("type").value("groups").endObject();
            String message = stringWriter.toString();
            tcpConnection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an request to the server to register the user in an selected group
     *
     * @param selectedGroup - the group that the user wants to join
     * @param name          - the name of the user. Will be set as an title on the marker
     */
    public void requestToRegister(String selectedGroup, String name) {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject().name("type").value("register")
                    .name("group").value(selectedGroup)
                    .name("member").value(name).endObject();
            String message = stringWriter.toString();
            tcpConnection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregisters the user from the server
     */
    public void unregister() {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject().name("type").value("unregister")
                    .name("id").value(ID).endObject();
            String message = stringWriter.toString();
            tcpConnection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * An Listener that interprets the messages received from the server
     */
    private class Listener implements TCPConnectionListener {
        private ActivityController activityController;
        private Context context;
        private MapsActivity mapsActivity;

        /**
         * Constructor for the Listener
         *
         * @param context
         */
        private Listener(Context context) {
            this.context = context;
            activityController = new ActivityController(context);
        }

        /**
         * Method that interprets the messages depending of the type
         *
         * @param json
         */
        public void receive(final JsonObject json) {
            String type = json.get("type").getAsString();
            switch (type) {
                case "register":
                    group = json.get("group").getAsString();
                    ID = json.get("id").getAsString();
                    activityController.startMapActivity(ID);
                    setGroup(group);
                    break;
                case "unregister":
                    ID = json.get("id").getAsString();
                    updateTimer.cancel();
                    tcpConnection.disconnect();
                    break;
                case "groups":
                    JsonArray jsonArray = json.getAsJsonArray("groups");
                    String[] groups;
                    groups = new String[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String stringToBeSplitted = String.valueOf(jsonArray.get(i));
                        String subStringed = stringToBeSplitted.substring(10);
                        String[] split = subStringed.split("\"");
                        groups[i] = split[0];
                    }
                    activityController.startExistingGroupActivity(groups);
                    break;
                case "location":
                    if (json.get("id").getAsString().matches(ID)) {
                        double latitude = json.get("latitude").getAsDouble();
                        double longitude = json.get("longitude").getAsDouble();
                        final LatLng location = new LatLng(latitude, longitude);
                        String idString = json.get("id").getAsString();
                        String[] split = idString.split(",");
                        final String name = split[1];
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapsActivity = Singleton.getReference().getMapsActivity();
                                mapsActivity.clearMyMarker(name);
                                mapsActivity.addMarker(location, name);
                            }
                        });
                    }
                    break;
                case "locations":
                    if (json.get("group").getAsString().matches(group)) {
                        JsonArray members = json.get("location").getAsJsonArray();
                        JsonObject jsonMember;
                        double latitude;
                        double longitude;
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapsActivity = Singleton.getReference().getMapsActivity();
                                mapsActivity.clearAllMarkers();
                            }
                        });
                        for (JsonElement jsonElement : members) {
                            jsonMember = jsonElement.getAsJsonObject();
                            final String name = jsonMember.get("member").getAsString();
                            latitude = jsonMember.get("latitude").getAsDouble();
                            longitude = jsonMember.get("longitude").getAsDouble();
                            final LatLng location = new LatLng(latitude, longitude);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mapsActivity = Singleton.getReference().getMapsActivity();
                                    mapsActivity.addMarker(location, name);

                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Class uesed to send an updated position of the user every minute
     */
    private class UpdateTimerTask extends TimerTask {
        public void run() {
            try {
                sendLocation(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the location
     *
     * @param location - the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sends the updated location to the server
     *
     * @param location - the new location
     * @throws IOException
     */
    private void sendLocation(Location location) throws IOException {
        if (location != null) {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject().name("type").value("location")
                    .name("id").value(ID)
                    .name("latitude").value("" + location.getLatitude())
                    .name("longitude").value("" + location.getLongitude()).endObject();
            String message = stringWriter.toString();
            tcpConnection.send(message);
        }
    }
}
