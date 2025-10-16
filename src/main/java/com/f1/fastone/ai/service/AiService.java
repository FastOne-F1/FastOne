package com.f1.fastone.ai.service;

import com.f1.fastone.ai.entity.AiRequestLog;
import com.f1.fastone.ai.repository.AiRequestLogRepository;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final AiRequestLogRepository aiRequestLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateDescription(User user, String prompt) {
        try {
            String aiResponse = chatClient.prompt(prompt)
                    .call()
                    .content();

            // 로그 저장
            AiRequestLog log = AiRequestLog.builder()
                    .prompt(prompt)
                    .response(aiResponse)
                    .model("gpt-4o-mini")
                    .requester(user)
                    .build();

            aiRequestLogRepository.save(log);
            return aiResponse;
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.EXTERNAL_SERVER_ERROR);
        }
    }
}