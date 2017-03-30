package com.l3cube.catchup.models;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class CatchupPlace {
    String name;
    String id;

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
}
