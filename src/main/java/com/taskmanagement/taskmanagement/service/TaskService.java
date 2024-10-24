package com.taskmanagement.taskmanagement.service;

import com.taskmanagement.taskmanagement.entity.Task;

import java.util.List;

public interface TaskService {

    void createTask(Task task, int userId);
    List<Task> getTasksForUser(int userId);
    void updateTask(int taskId, Task updatedTask);
    void deleteTask(int taskId);
}
