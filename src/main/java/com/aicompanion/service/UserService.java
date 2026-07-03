package com.aicompanion.service;

import com.aicompanion.dto.UserLoginDTO;
import com.aicompanion.dto.UserRegisterDTO;
import com.aicompanion.entity.User;
import com.aicompanion.vo.LoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO registerDTO);
    LoginVO login(UserLoginDTO loginDTO);
}
