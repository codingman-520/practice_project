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
        Long userId = com.aicompanion.common.UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未登录或 Token 无效");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + difyConfig.getChat().getApiKey());

            if (requestDTO.getInputs() == null) {
                requestDTO.setInputs(new HashMap<>());
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", requestDTO.getQuery());
            requestBody.put("user", userId.toString());
            requestBody.put("inputs", requestDTO.getInputs());
            // 确保使用同步阻塞请求模式
            requestBody.put("response_mode", "blocking");
            
            if (requestDTO.getConversation_id() != null && !requestDTO.getConversation_id().isEmpty()) {
                requestBody.put("conversation_id", requestDTO.getConversation_id());
            }

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            String url = difyConfig.getChat().getBaseUrl() + "/chat-messages";

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
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
        } catch (Exception e) {
            System.err.println("Dify Chat failed, fallback to mock: " + e.getMessage());
        }

        // Mock fallback response
        String reply = "关于您的问题 \"" + requestDTO.getQuery() + "\"，AI 伴学助手建议：在前端架构设计中，要时刻注意数据流动清晰性。推荐使用 Composition API 来聚合相关逻辑，并利用 Axios 统一封装网络请求。此外，配合 Pinia 等轻量化状态库可以很好地实现状态共享。";
        if (requestDTO.getQuery() != null) {
            if (requestDTO.getQuery().toLowerCase().contains("vue")) {
                reply = "Vue 3 引入的 Composition API 极大地改善了代码的可维护性。建议了解一下 ref 与 reactive 的核心工作原理（基于 ES6 Proxy 的属性拦截）。同时，熟练掌握 Vue Router 4 的路由钩子和动态路由对后台开发至关重要。";
            } else if (requestDTO.getQuery().toLowerCase().contains("docker")) {
                reply = "Docker 容器技术在现代 DevOps 中是不可或缺的。对于前端开发者，通常将打包生成的 dist 文件夹放置在 Nginx 镜像中进行轻量化部署。这对于构建 CI/CD 自动化流水线（如 Jenkins 或 GitHub Actions）非常方便。";
            }
        }

        return DifyChatResponseVO.builder()
                .answer(reply)
                .conversation_id(requestDTO.getConversation_id() != null ? requestDTO.getConversation_id() : "mock-conversation-" + System.currentTimeMillis())
                .create_at(System.currentTimeMillis() / 1000)
                .build();
    }
}
