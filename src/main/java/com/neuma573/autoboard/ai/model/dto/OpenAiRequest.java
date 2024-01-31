package com.neuma573.autoboard.ai.model.dto;

import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
public class OpenAiRequest {

    private String chatRequestSystemMessage;

    private String chatRequestUserMessage;

    private String chatRequestAssistantMessage;

    @Getter
    private String deploymentOrModelName;

    public ChatRequestSystemMessage getChatRequestSystemMessage() {
        return new ChatRequestSystemMessage(chatRequestSystemMessage);
    }

    public ChatRequestUserMessage getChatRequestUserMessage() {
        return new ChatRequestUserMessage(chatRequestSystemMessage);
    }

    public ChatRequestAssistantMessage getChatRequestAssistantMessage() {
        return new ChatRequestAssistantMessage(chatRequestSystemMessage);
    }

    public List<ChatRequestMessage> chatMessages() {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(getChatRequestSystemMessage());
        chatMessages.add(getChatRequestUserMessage());
        chatMessages.add(getChatRequestAssistantMessage());

        return chatMessages;
    }
}
