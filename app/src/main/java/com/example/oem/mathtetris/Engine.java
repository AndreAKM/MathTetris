/*
 * Copyright (c) 2018.
 * Create by Andrey Moiseenko for DoFast project
 */

package com.example.oem.mathtetris;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * @class Engine is wrapper under native Engine
 */
public class Engine {
    private static final String TAG = Engine.class.getName();

    int fieldSize;
    int N;
    Field field;

    public boolean isChanged() {
        return changed;
    }

    boolean changed;
    /**
     * constructor
     * @param fieldSize game field size
     * @param N max value of target
     */
    public Engine(int fieldSize, int N) {
        this.fieldSize = fieldSize;
        this.N = N;
        field = new Field(fieldSize, fieldSize, N);
    }

    public void flush(){
        field = new Field(fieldSize, fieldSize, N);
    }

    /**
     * serialize engine
     * @return return jason which contain necessary data to continue measure
     */
    public  String saveState() {
        JSONObject serialyze = null;
        try {
            serialyze = field.toJson();
            return serialyze.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * initialise engine to continue measure
     * @param state json with necessary data
     */
    public void loadState(String state) {
        if (state.isEmpty() == true) return;
        try {
            JSONObject serialyze = new JSONObject(state);
            field = new Field(serialyze);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void choose(int x, int y) {
        synchronized (field) {
            changed = field.changeSelect(new Field.Coordinate(x, y));
            Field.Data data = new Field.Data();
            for (Field.Coordinate c : field.getSelect()) {
                Field.Data d = field.data(c);
                data.color = d.color;
                data.value += d.value;
            }
            if (field.erraiseTask(data)) {
                field.playSelect();
            }
        }
    }

    public void addTask () {
        synchronized (field) {
            field.addTarget(Field.genData(5));
            changed = true;
        }
    }

    public Field read () {
        synchronized (field) {
            changed = false;
            return field;
        }
    }

}
