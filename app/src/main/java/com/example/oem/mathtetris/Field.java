package com.example.oem.mathtetris;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Field {
    static final List<Integer> palitra =
            Arrays.asList(Color.BLACK, 0xFF8cc800, 0xFF01b5b7, 0xFFd8d051, 0xFFe88a01, 0xFFe90040);
    public static final String TopData ="N";
    public static final String WIDHT ="WIDHT";
    public static final String HEIGHT ="HEIGHT";

    public static final String TASKS = "TASKS";
    public static final String FIELD ="FIELD";
    /**
     * initialise engine to continue measure
     * @param state json with necessary data
     */
    public Field(JSONObject state) throws JSONException {
        N = state.getInt(TopData);
        widht = state.getInt(WIDHT);
        height = state.getInt(HEIGHT)
        JSONArray serialyze = state.getJSONArray(FIELD);
        field = new ArrayList<>(widht);

        for (int x = 0; x < widht; ++x) {
            JSONArray row = serialyze.getJSONArray(x);
            List<Data> coll = new ArrayList<Data>(height);
            field.set(x, coll);
            for (int y = 0; y < height; ++y) {
                coll.set(y, new Data(row.getJSONObject(y)));
            }
            serialyze.put(row);
        }
        JSONArray tasJ = state.getJSONArray(TASKS);
        for(int i = 0; i!= tasJ.length(); ++i) {
            tasks.add(new Data(tasJ.getJSONObject(i)));
        }
    }

    public static class Data {
        int color = palitra.get(0);
        int value = 0;
        public static final String COLOR ="color";
        public static final String VALUE ="value";
        public Data(int color, int value) {
            this.color = color;
            this.value = value;
        }
        public Data(JSONObject s) throws JSONException {
            color = s.getInt(COLOR);
            value = s.getInt(VALUE);
        }

        public Data() {

        }

        public void copy(Data other) {
            color = other.color;
            value = other.value;
        }
        public JSONObject toJson() throws JSONException {
            JSONObject serialyze = new JSONObject();
            serialyze.put(COLOR, color);
            serialyze.put(VALUE, value);
            return serialyze;
        }
        boolean isSame(Data c) {
            return c.color == color && c.value == value;
        }
    }

    public static class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isSame(Coordinate c) {
            return c.x == x && c.y == y;
        }
    }
    private final Data defaultD = new Data(palitra.get(0), -1);
    List<List<Data>> field;
    List<Coordinate> select;
    List<Data> tasks = new ArrayList<>();
    int N;
    int widht;
    int height;
    public Field(int h, int w, int N) {
        height = h;
        widht = w;
        this.N = N;
        field = new ArrayList<>(widht);
        for (int x = 0; x != widht; ++x) {
            List<Data> coll = new ArrayList<Data>(height);
            field.set(x, coll);
            for (int y = 0; y != height; ++y) {
                coll.set(y, genData(0));
            }
        }
    }

    /**
     * serialize engine
     * @return return jason which contain necessary data to continue measure
     */
    public  JSONObject toJson() throws JSONException {
        JSONObject serialyze = new JSONObject();
        serialyze.put(TopData, N);
        serialyze.put(WIDHT, widht);
        serialyze.put(HEIGHT, height);
        JSONArray fieldJ = new JSONArray();
        for (int x = 0; x < widht; ++x) {
            JSONArray row = new JSONArray();
            for (int y = 0; y < height; ++y) {
                row.put(data(new Coordinate(x, y)).toJson());
            }
            fieldJ.put(row);
        }
        serialyze.put(FIELD, fieldJ);
        JSONArray tasJ = new JSONArray();
        for(Data d: tasks) {
            tasJ.put(d.toJson());
        }
        serialyze.put(TASKS, tasJ);
        return serialyze;
    }

    public Data genData(int start) {
        return new Data(genColor(), genValue(start));
    }

    private int genColor() {
        return palitra.get(1 + (int) (Math.random() * 5));
    }

    private int genValue(int start) {
        return start + (int) (Math.random() * (N - start));
    }

    public Data data(Coordinate c) {
        return field.get(c.x).get(c.y);
    }
    public List<Coordinate> getSelect(){
        return select;
    }
    public boolean changeSelect(Coordinate c) {
        try {
            Coordinate cb = select.get(0);
            if (data(cb).color != data(c).color) return false;
            for (Coordinate co: select) {
                if (co.isSame(c)) {
                    select.remove(c);
                    return true;
                }
            }
            select.add(c);
        } catch (IndexOutOfBoundsException I) {
            select.add(c);
        }
        return true;
    }
    public void playSelect() {
        for (Coordinate c : select) {
            Data d = data(c);
            d = genData(0);
            List<Data>coll = field.get(c.x);
            for (int y = c.y -1; y != -1; --y) {
                Collections.swap(coll, y, y + 1);
            }
        }
    }
    public void clearSelected() {
        select.clear();
    }

    public boolean erraiseTask(Data d) {
        int i = 0;
        for(; i != tasks.size(); ++i) {
            if (tasks.get(i).isSame(d)) break;
        }
        if (i != tasks.size()) {
            tasks.remove(i);
            return true;
        }
        return false;
    }

    public void addTarget(Data d) {
        tasks.add(d);
    }

    public List<Data> getTasks() {
        return tasks;
    }
}
