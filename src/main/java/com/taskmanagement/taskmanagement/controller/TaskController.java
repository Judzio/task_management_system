package com.taskmanagement.taskmanagement.controller;

import com.taskmanagement.taskmanagement.entity.Task;
import com.taskmanagement.taskmanagement.entity.User;
import com.taskmanagement.taskmanagement.service.TaskService;
import com.taskmanagement.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String getUserTasks(Model model, Principal principal){

        String tempUsername = principal.getName();
        User tempUser = userService.findUserByUsername(tempUsername);
        int tempId = tempUser.getId();

        List<Task> tasks = taskService.getTasksForUser(tempId);

        model.addAttribute("tasks", tasks);

        return "user/tasks";
    }

    @GetMapping("/new")
    public String showNewTaskForm(Model model){
        model.addAttribute("task", new Task());

        return "user/task-form";
    }

    @PostMapping
    public String saveNewTask(@ModelAttribute("task") @Valid Task task,
                              BindingResult bindingResult,
                              Principal principal) {

        if (bindingResult.hasErrors()) {
            return "user/task-form";
        }

        String tempUsername = principal.getName();

        taskService.createTask(task, tempUsername);

        return "redirect:/tasks";
    }


    @GetMapping("/edit/{taskId}")
    public String showEditTaskForm(@PathVariable("taskId") int taskId, Model model, Principal principal){

        Task task = taskService.findTaskById(taskId);

        if(taskService.isTaskOwner(taskId, principal)){

            model.addAttribute("task", task);
            return "user/task-form";
        }
        return "redirect:/access-denied";

    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable("taskId") int taskId,
                             @ModelAttribute("task") @Valid Task task,
                             BindingResult bindingResult,
                             Principal principal) {

        if (bindingResult.hasErrors()) {
            return "user/task-form";
        }

        if(taskService.isTaskOwner(taskId, principal)){

            taskService.updateTask(taskId, task);
            return "redirect:/tasks";

        }
        return "redirect:/access-denied";

    }

    @GetMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable("taskId") int taskId, Principal principal){

        if(taskService.isTaskOwner(taskId, principal)){

            taskService.deleteTask(taskId);
            return "redirect:/tasks";
        }
        return "redirect:/access-denied";
    }
}
