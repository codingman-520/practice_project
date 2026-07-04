package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.entity.User;
import com.aicompanion.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public Result<Map<String, Object>> getUserList(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        Page<User> userPage = userService.getUserPage(username, role, page, size);
        return Result.success(Map.of(
                "list", userPage.getRecords(),
                "total", userPage.getTotal()
        ));
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        userService.updateUserRole(id, role);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable("id") Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateUserStatus(id, status);
        return Result.success();
    }
}
