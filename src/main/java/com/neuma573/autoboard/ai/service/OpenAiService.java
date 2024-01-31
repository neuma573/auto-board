package com.neuma573.autoboard.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.neuma573.autoboard.ai.model.dto.OpenAiRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAiService {

    private final OpenAIClient openAIClient;

    @Async
    public void ask(OpenAiRequest openAiRequest) {
        List<ChatRequestMessage> chatMessages = openAiRequest.chatMessages();

        ChatCompletions chatCompletions = openAIClient.getChatCompletions(
                openAiRequest.getDeploymentOrModelName(),
                new ChatCompletionsOptions(chatMessages)
        );

    }

    @Transactional
    public void readAndResponse(PostResponse postResponse) {

    }
}
