package com.aicompanion.service.impl;

import com.aicompanion.config.DifyConfig;
import com.aicompanion.dto.DifyChatRequestDTO;
import com.aicompanion.service.DifyChatService;
import com.aicompanion.vo.DifyChatResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DifyChatServiceImpl implements DifyChatService {

    private final RestTemplate restTemplate;
    private final DifyConfig difyConfig;

    @Override
    public DifyChatResponseVO sendChatMessage(DifyChatRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyConfig.getApiKey());

        if (requestDTO.getInputs() == null) {
            requestDTO.setInputs(new HashMap<>());
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", requestDTO.getQuery());
        requestBody.put("user", requestDTO.getUser());
        requestBody.put("inputs", requestDTO.getInputs());
        requestBody.put("response_mode", "blocking");
        
        if (requestDTO.getConversation_id() != null && !requestDTO.getConversation_id().isEmpty()) {
            requestBody.put("conversation_id", requestDTO.getConversation_id());
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        String url = difyConfig.getBaseUrl() + "/chat-messages";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null) {
            throw new RuntimeException("调用 Dify 接口失败，响应为空");
        }

        String answer = (String) responseBody.get("answer");
        String conversationId = (String) responseBody.get("conversation_id");
        
        Object createdAtObj = responseBody.get("created_at");
        Long createAt = null;
        if (createdAtObj instanceof Number) {
            createAt = ((Number) createdAtObj).longValue();
        }

        return DifyChatResponseVO.builder()
                .answer(answer)
                .conversation_id(conversationId)
                .create_at(createAt)
                .build();
    }
}
