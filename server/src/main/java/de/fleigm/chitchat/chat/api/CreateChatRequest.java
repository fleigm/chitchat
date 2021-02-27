package de.fleigm.chitchat.chat.api;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateChatRequest.Private.class, name = "private"),
    @JsonSubTypes.Type(value = CreateChatRequest.Group.class, name = "group")
})
public interface CreateChatRequest {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class Private implements CreateChatRequest {
    private UUID user;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class Group implements CreateChatRequest {
    private String name;
    private List<UUID> members;
  }
}
