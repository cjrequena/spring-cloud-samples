package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.domain.model.event.FooEvent;
import com.cjrequena.sample.shared.common.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FooMapper {

  Logger log = LoggerFactory.getLogger(FooMapper.class);


  default FooEvent mapToFooEventFromJsonString(String jsonString) {
    try {
      return JsonUtil.jsonStringToObject(jsonString, FooEvent.class);
    } catch (JsonProcessingException ex) {
      String errorMessage = String.format("Failed to map JSON string to FooEvent: %s", jsonString);
      log.error(errorMessage);
      throw new RuntimeException(ex);
    }
  }

  default String mapToJsonStringFromFooEvent(FooEvent value) {
    try {
      return JsonUtil.objectToJsonString(value);
    } catch (JsonProcessingException ex) {
      String errorMessage = String.format("Failed to map FooEvent to JSON string: %s", value);
      log.error(errorMessage);
      throw new RuntimeException(ex);
    }
  }
}
