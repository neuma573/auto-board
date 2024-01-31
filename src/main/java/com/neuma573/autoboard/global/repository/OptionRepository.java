package com.neuma573.autoboard.global.repository;

import com.neuma573.autoboard.global.model.entity.Option;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OptionRepository extends CrudRepository<Option, Long> {

    Optional<Option> findByOptionKey(String key);
}
