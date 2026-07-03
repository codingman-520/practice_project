package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.dto.DifyChatRequestDTO;
import com.aicompanion.service.DifyChatService;
import com.aicompanion.vo.DifyChatResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class DifyChatController {

    private final DifyChatService difyChatService;

    @PostMapping
    public Result<DifyChatResponseVO> chat(@RequestBody DifyChatRequestDTO requestDTO) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        requestDTO.setUser(userId != null ? userId.toString() : null);
        DifyChatResponseVO responseVO = difyChatService.sendChatMessage(requestDTO);
        return Result.success(responseVO);
    }
}
