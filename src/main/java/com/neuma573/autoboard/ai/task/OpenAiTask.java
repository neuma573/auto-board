package com.neuma573.autoboard.ai.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuma573.autoboard.ai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenAiTask {

    private final OpenAiService openAiService;


    @Scheduled(fixedRate = 360000)
    public void refreshTopic() {
        openAiService.refreshTopic();
    }

    @Scheduled(fixedRate = 28000)
    public void scheduledOpenAiTask() throws JsonProcessingException {
        if(shouldExecuteTask()) {
            openAiService.ask();
        }
    }

    private boolean shouldExecuteTask() {
        return ThreadLocalRandom.current().nextDouble() < 0.33;
    }
}
