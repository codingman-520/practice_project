package com.aicompanion.config;

import com.aicompanion.entity.User;
import com.aicompanion.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitConfig {

    @Bean
    public CommandLineRunner initData(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userMapper.selectCount(null) == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("管理员");
                admin.setRole("ADMIN");
                admin.setStatus(1);
                userMapper.insert(admin);

                User student = new User();
                student.setUsername("student01");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setNickname("小明");
                student.setRole("STUDENT");
                student.setStatus(1);
                userMapper.insert(student);
            }
        };
    }
}
