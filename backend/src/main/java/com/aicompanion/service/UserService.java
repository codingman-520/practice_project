package com.aicompanion.service;

import com.aicompanion.dto.UserLoginDTO;
import com.aicompanion.dto.UserRegisterDTO;
import com.aicompanion.entity.User;
import com.aicompanion.vo.LoginVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO registerDTO);
    LoginVO loginAdmin(UserLoginDTO loginDTO);
    LoginVO loginStudent(UserLoginDTO loginDTO);
    Page<User> getUserPage(String username, String role, Integer page, Integer size);
    void updateUserRole(Long id, String role);
    void updateUserStatus(Long id, Integer status);
    java.util.Map<String, Object> getUserStats(Long userId);
}
