package com.neuma573.autoboard.ai.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuma573.autoboard.ai.service.OpenAiService;
import com.neuma573.autoboard.global.service.OptionService;
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

    private final OptionService optionService;


    @Scheduled(fixedRate = 3600000)
    public void refreshTopic() {
        if(optionService.findByKey("autoPosting").equals("true")) {
            openAiService.refreshTopic();
        }

    }

    @Scheduled(fixedRate = 358888)
    public void scheduledOpenAiTask() throws JsonProcessingException {
        if(optionService.findByKey("autoPosting").equals("true") && shouldExecuteTask()) {
            openAiService.ask();
        }
    }

    private boolean shouldExecuteTask() {
        return ThreadLocalRandom.current().nextDouble() < 0.33;
    }
}
