package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<WebSocketMessage> {

  // Not a CDI managed bean so we cannot use @Inject
  ObjectMapper objectMapper = CDI.current().select(ObjectMapper.class).get();

  @Override
  public String encode(WebSocketMessage object) throws EncodeException {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new EncodeException(object, "", e);
    }
  }

  @Override
  public void init(EndpointConfig config) {

  }

  @Override
  public void destroy() {

  }
}
