package com.neuma573.autoboard.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.ai.model.dto.OpenAiRequest;
import com.neuma573.autoboard.ai.model.dto.OpenAiResponse;
import com.neuma573.autoboard.ai.model.entity.OpenAiJson;
import com.neuma573.autoboard.ai.repository.OpenAiJsonRepository;
import com.neuma573.autoboard.global.model.entity.Option;
import com.neuma573.autoboard.global.service.OptionService;
import com.neuma573.autoboard.post.service.PostService;
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

    private final OptionService optionService;

    private final OpenAiJsonRepository openAiJsonRepository;

    private final ObjectMapper objectMapper;

    private final PostService postService;

    @Transactional
    @Async
    public void ask() throws JsonProcessingException {
        OpenAiRequest openAiRequest = OpenAiRequest.builder()
                .chatRequestSystemMessage(optionService.findByKey("requestSystemMessage").replace("${topic}", optionService.findByKey("topic")))
                .chatRequestAssistantMessage(optionService.findByKey("requestAssistantMessage"))
                .chatRequestUserMessage(optionService.findByKey("requestDefaultMessage"))
                .deploymentOrModelName(optionService.findByKey("gptModel"))
                .build();


        String jsonResponse = getStringFromResponse(
                getResponse(openAiRequest)
        );



        saveJsonResponse(jsonResponse);

        log.info("GPT post will be posted : {} ...", jsonResponse.substring(0, 20));

        postService.saveAiPost(objectMapper.readValue(jsonResponse, OpenAiResponse.class));

    }

    @Transactional
    @Async
    public void ask(String requestUserMessage) throws JsonProcessingException {
        OpenAiRequest openAiRequest = OpenAiRequest.builder()
                .chatRequestSystemMessage(optionService.findByKey("requestSystemMessage"))
                .chatRequestAssistantMessage(optionService.findByKey("requestAssistantMessage"))
                .chatRequestUserMessage(optionService.findByKey(requestUserMessage))
                .deploymentOrModelName(optionService.findByKey("gptModel"))
                .build();

        String jsonResponse = getStringFromResponse(
                getResponse(openAiRequest)
        );

        saveJsonResponse(jsonResponse);

        postService.saveAiPost(objectMapper.readValue(jsonResponse, OpenAiResponse.class));

    }

    @Transactional
    @Async
    public void refreshTopic() {
        OpenAiRequest openAiRequest = OpenAiRequest.builder()
                .chatRequestSystemMessage(optionService.findByKey("topicSystemMessage"))
                .chatRequestAssistantMessage(optionService.findByKey("topicAssistantMessage"))
                .chatRequestUserMessage(optionService.findByKey("topicRequestUserMessage"))
                .deploymentOrModelName(optionService.findByKey("gptModel"))
                .build();

        String topic = getStringFromResponse(
                getResponse(openAiRequest)
        );

        log.info("Now GPT chooses new Topic : {}", topic);

        Option topicOption = optionService.getOptionByKey("topic");

        if(topicOption == null) {
            optionService.saveOption("topic", topic);
        } else {
            topicOption.setValue(topic);
        }
        openAiJsonRepository.save(
                OpenAiJson
                        .builder()
                        .content(topic)
                        .build()
        );
    }

    private ChatCompletions getResponse(OpenAiRequest openAiRequest) {
        List<ChatRequestMessage> chatMessages = openAiRequest.chatMessages();

        return openAIClient.getChatCompletions(
                openAiRequest.getDeploymentOrModelName(),
                new ChatCompletionsOptions(chatMessages)
        );
    }

    private String getStringFromResponse(ChatCompletions chatCompletions) {

        List<ChatChoice> chatChoices = chatCompletions.getChoices();
        ChatChoice firstChoice = chatChoices.get(0);
        return firstChoice.getMessage().getContent();

    }

    public void saveJsonResponse(String content) {
        openAiJsonRepository.save(
                OpenAiJson
                        .builder()
                        .content(content)
                        .build()
        );
    }
}
