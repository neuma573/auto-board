package com.neuma573.autoboard.global.service;

import com.neuma573.autoboard.global.model.entity.Option;
import com.neuma573.autoboard.global.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public String findByKey(String key) {
        return optionRepository.findByOptionKey(key)
                .map(Option::getValue)
                .orElse("");
    }

    public Option getOptionByKey(String key) {
        return optionRepository.findByOptionKey(key).orElse(null);
    }

    public void saveOption(String key, String value) {
        optionRepository.save(
                Option
                    .builder()
                    .optionKey(key)
                    .value(value)
                    .build()
        );
    }
}
