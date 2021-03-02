package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<WebSocketMessage> {

  // Not a CDI managed bean so we cannot use @Inject
  ObjectMapper objectMapper = CDI.current().select(ObjectMapper.class).get();

  @Override
  public WebSocketMessage decode(String s) throws DecodeException {

    try {
      return objectMapper.readValue(s, WebSocketMessage.class);
    } catch (JsonProcessingException e) {
      throw new DecodeException(s, "", e);
    }
  }

  @Override
  public boolean willDecode(String s) {
    return true;
  }

  @Override
  public void init(EndpointConfig config) {

  }

  @Override
  public void destroy() {

  }
}
