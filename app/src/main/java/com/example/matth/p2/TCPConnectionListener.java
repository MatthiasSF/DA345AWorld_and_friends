package com.example.matth.p2;

import com.google.gson.JsonObject;

/**
 * Interface used to listen on messages from the server
 *
 * @author Matthias Falk
 */
public interface TCPConnectionListener {
    void receive(JsonObject message);
}
