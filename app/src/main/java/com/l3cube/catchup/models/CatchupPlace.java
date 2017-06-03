package com.l3cube.catchup.models;

import org.json.JSONArray;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class CatchupPlace {
    String name;
    String id;
    JSONArray votes;

    public CatchupPlace(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVotes() {
        try {
            return votes.length();
        } catch (NullPointerException e){
            return 0;
        }
    }

    public void setVotes(JSONArray votes) {
        this.votes = votes;
    }
}
