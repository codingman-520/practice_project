package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.common.UserContext;
import com.aicompanion.dto.UserLoginDTO;
import com.aicompanion.dto.UserRegisterDTO;
import com.aicompanion.entity.User;
import com.aicompanion.service.UserService;
import com.aicompanion.vo.LoginVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/login/admin")
    public Result<LoginVO> loginAdmin(@Validated @RequestBody UserLoginDTO loginDTO) {
        LoginVO loginVO = userService.loginAdmin(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/login/student")
    public Result<LoginVO> loginStudent(@Validated @RequestBody UserLoginDTO loginDTO) {
        LoginVO loginVO = userService.loginStudent(loginDTO);
        return Result.success(loginVO);
    }
}

@RestController
@RequestMapping("/api/user")
class UserProfileController {
    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Long userId = UserContext.getUserId();
        Map<String, Object> stats = userService.getUserStats(userId);
        return Result.success(stats);
    }
}
