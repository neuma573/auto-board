package com.neuma573.autoboard.user.task;

import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserTask {

    private final UserService userService;

    @Scheduled(cron = "0 0 4 * * *")
    public void executeDailyTask() {
        userService.initializeFailCount();
    }
}
