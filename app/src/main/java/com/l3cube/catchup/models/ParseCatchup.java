package com.l3cube.catchup.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityashirole on 30-03-2017.
 */

@ParseClassName("Catchup")
public class ParseCatchup extends ParseObject{
    public ParseCatchup() {
    }



    public void changeVote(Boolean addVote, String placeId) throws JSONException {
        JSONArray placesArray = getJSONArray("placesJSONArray");

        for (int i = 0; i < placesArray.length(); i++) {
            JSONObject jsonPlace = placesArray.getJSONObject(i);
            if (jsonPlace.getString("id").equalsIgnoreCase(placeId)) {
                Integer oldVotes = jsonPlace.getInt("votes");
                Integer change = 0;
                if (addVote) {
                    change = 1;
                } else {
                    change = -1;
                }
                Integer newVotes = oldVotes + change;

                jsonPlace.put("votes",newVotes);

                placesArray.put(i,jsonPlace);
            }
        }

        put("placesJSONArray",placesArray);

    }
}
