package com.aicompanion.service.impl;

import com.aicompanion.dto.UserLoginDTO;
import com.aicompanion.dto.UserRegisterDTO;
import com.aicompanion.entity.User;
import com.aicompanion.mapper.UserMapper;
import com.aicompanion.service.UserService;
import com.aicompanion.utils.JwtUtils;
import com.aicompanion.vo.LoginVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aicompanion.mapper.LearningRecordMapper;
import com.aicompanion.mapper.UserSkillMapper;
import com.aicompanion.entity.LearningRecord;
import com.aicompanion.entity.UserSkill;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final LearningRecordMapper learningRecordMapper;
    private final UserSkillMapper userSkillMapper;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtils jwtUtils, 
                           LearningRecordMapper learningRecordMapper, UserSkillMapper userSkillMapper) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.learningRecordMapper = learningRecordMapper;
        this.userSkillMapper = userSkillMapper;
    }

    @Override
    public void register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("STUDENT"); // 默认角色
        
        this.save(user);
    }

    @Override
    public synchronized LoginVO loginAdmin(UserLoginDTO loginDTO) {
        return doLogin(loginDTO, "ADMIN");
    }

    @Override
    public synchronized LoginVO loginStudent(UserLoginDTO loginDTO) {
        return doLogin(loginDTO, "STUDENT");
    }

    private LoginVO doLogin(UserLoginDTO loginDTO, String expectedRole) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = this.getOne(queryWrapper);

        if (user == null) {
            // 再次校验以防并发情况下插入重复用户名
            if (this.count(queryWrapper) > 0) {
                user = this.getOne(queryWrapper);
            } else {
                // 账号不存在，自动注册并落库
                user = new User();
                user.setUsername(loginDTO.getUsername());
                user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
                // 根据登录入口分配角色
                user.setRole(expectedRole);
                user.setStatus(1);
                try {
                    this.save(user);
                } catch (Exception e) {
                    // 若发生唯一索引冲突，重新查询
                    user = this.getOne(queryWrapper);
                }
            }
        } 
        
        // 账号存在，验证密码
        if (user != null && !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 校验角色限制：管理员不能登学生端，学生不能登管理端
        if (user != null && !expectedRole.equals(user.getRole())) {
            throw new RuntimeException("权限不足：该账号无法在此端登录");
        }

        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 返回登录信息
        return LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Override
    public Page<User> getUserPage(String username, String role, Integer page, Integer size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username).or().like(User::getNickname, username);
        }
        if (StringUtils.hasText(role)) {
            queryWrapper.eq(User::getRole, role);
        }
        
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public void updateUserRole(Long id, String role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        this.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // Calculate total learning duration
        LambdaQueryWrapper<LearningRecord> recordQuery = new LambdaQueryWrapper<>();
        recordQuery.eq(LearningRecord::getUserId, userId);
        java.util.List<LearningRecord> records = learningRecordMapper.selectList(recordQuery);
        int totalDuration = records.stream().mapToInt(r -> r.getDuration() == null ? 0 : r.getDuration()).sum();

        // Calculate mastered skills (status = 2)
        LambdaQueryWrapper<UserSkill> skillQuery = new LambdaQueryWrapper<>();
        skillQuery.eq(UserSkill::getUserId, userId).eq(UserSkill::getStatus, 2);
        long masteredSkills = userSkillMapper.selectCount(skillQuery);

        stats.put("totalDuration", totalDuration);
        stats.put("masteredSkills", masteredSkills);
        return stats;
    }
}
