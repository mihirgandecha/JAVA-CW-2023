package edu.uob.DBCmnd;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Table {
    public String tbName;
    public int index;
    int rows;
    int cols;
    //id is the key
    static int id = 1;
    private Map<String, String> foreignKeys;
    //HashMap<Integer, Row> rows>

    public void Table(){

    }

    public static void main(String[] args) {
        HashMap<Integer, ArrayList<String>> table = new HashMap<>();
        //note that doesnt care about order so wont always be same
        table.put(id, new ArrayList<>(Arrays.asList("name", "marks", "pass")));        System.out.println(table);
        for (Map.Entry<Integer, ArrayList<String>> entry : table.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

        //check if id=1 is added
        System.out.println("Does table have id=1? " + table.containsKey(1));

        //check for value (select)
        System.out.println("Does table include marks value? " + table.containsValue(Array.getChar("marks", 5)));

        table.computeIfAbsent(1, k -> new ArrayList<>());
    }
}
