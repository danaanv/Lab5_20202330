package com.example.lab5_20202330;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private final Context context;

    public TaskStorage(Context context) {
        this.context = context;
    }

    public void saveTasks(String pucpCode, List<Task> tasks) {
        try {
            String fileName = getFileName(pucpCode);
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oos.writeObject(tasks);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskStorage", "Error saving tasks: " + e.getMessage());
        }
    }

    public List<Task> loadTasks(String pucpCode) {
        try {
            String fileName = getFileName(pucpCode);
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<Task> tasks = (List<Task>) ois.readObject();
            ois.close();
            return tasks;
        } catch (FileNotFoundException e) {
            return null; // File not found, return null
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskStorage", "Error loading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    private String getFileName(String pucpCode) {
        return "tasks_" + pucpCode + ".dat";
    }
}


