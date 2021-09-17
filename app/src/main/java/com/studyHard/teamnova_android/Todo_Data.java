package com.studyHard.teamnova_android;

public class Todo_Data {
    String date;
    String Todo_content;

    public String getTodo_content() {
        return Todo_content;
    }

    public void setTodo_content(String todo_content) {
        Todo_content = todo_content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Todo_Data(String date, String todo_content) {
        this.date = date;
        this.Todo_content = todo_content;
    }
}
