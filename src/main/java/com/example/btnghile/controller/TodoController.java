package com.example.btnghile.controller;

import com.example.btnghile.entity.Todo;
import com.example.btnghile.repository.TodoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }


    @GetMapping
    public String index(Model model, HttpSession session) {
        String owner = (String) session.getAttribute("owner");

        if (owner == null || owner.isBlank()) {
            return "redirect:/todos/owner";
        }

        model.addAttribute("owner", owner);
        model.addAttribute("todos", todoRepository.findAll());

        return "index";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "form";
    }


    @PostMapping("/save")
    public String saveTodo(
            @Valid @ModelAttribute("todo") Todo todo,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "form";
        }

        todoRepository.save(todo);

        if (todo.getId() == null) {
            redirectAttributes.addFlashAttribute("message", "Thêm task thành công!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Cập nhật task thành công!");
        }

        return "redirect:/todos";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Todo todo = todoRepository.findById(id).orElse(null);

        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy task!");
            return "redirect:/todos";
        }

        model.addAttribute("todo", todo);
        return "form";
    }


    @GetMapping("/delete/{id}")
    public String deleteTodo(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        if (!todoRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message", "Task không tồn tại!");
            return "redirect:/todos";
        }

        todoRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Xóa task thành công!");

        return "redirect:/todos";
    }
    @GetMapping("/owner")
    public String showOwnerForm() {
        return "owner-form";
    }

    @PostMapping("/owner")
    public String saveOwner(
            @RequestParam String owner,
            HttpSession session
    ) {
        session.setAttribute("owner", owner);
        return "redirect:/todos";
    }
}
