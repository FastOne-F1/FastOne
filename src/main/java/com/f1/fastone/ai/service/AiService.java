package com.f1.fastone.ai.service;

import com.f1.fastone.ai.entity.AiRequestLog;
import com.f1.fastone.ai.repository.AiRequestLogRepository;
import com.f1.fastone.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final AiRequestLogRepository aiRequestLogRepository;

    public String generateDescription(User user, String prompt) {
        String aiResponse = chatClient.prompt(prompt)
                .call()
                .content();

        // 로그 저장
        AiRequestLog log = AiRequestLog.builder()
                .prompt(prompt)
                .response(aiResponse)
                .model("gemini-1.5-flash")
                .requester(user)
                .build();

        aiRequestLogRepository.save(log);
        return aiResponse;
    }
}