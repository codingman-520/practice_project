package com.aicompanion.service;

import com.aicompanion.dto.DifyChatRequestDTO;
import com.aicompanion.vo.DifyChatResponseVO;

public interface DifyChatService {
    DifyChatResponseVO sendChatMessage(DifyChatRequestDTO requestDTO);
}
