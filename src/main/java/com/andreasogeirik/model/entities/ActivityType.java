package com.andreasogeirik.model.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrena on 05.05.2016.
 */
public enum ActivityType {
    OTHER(-1), WALK(0), RUN(1), BIKE(2), SKI(3), SWIM(4), GROUP_WORKOUT(5);

    private int id;

    ActivityType(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private static final Map<Integer, ActivityType> idToTypeMap = new HashMap<Integer, ActivityType>();

    static {
        for (ActivityType type : ActivityType.values()) {
            idToTypeMap.put(type.id, type);
        }
    }

    public static ActivityType getTypeById(int id) {
        return idToTypeMap.get(Integer.valueOf(id));
    }
}
