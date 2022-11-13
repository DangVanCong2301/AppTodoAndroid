package com.example.todolistapp.models;

public class ModelJob {
    private String task;
    private String description;
    private String id;
    private String data;

    public ModelJob() {
    }

    public ModelJob(String task, String description, String id, String data) {
        this.task = task;
        this.description = description;
        this.id = id;
        this.data = data;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
