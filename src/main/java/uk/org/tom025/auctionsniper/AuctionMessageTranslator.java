package uk.org.tom025.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class AuctionMessageTranslator implements MessageListener {
  private final AuctionEventListener listener;

  public AuctionMessageTranslator(AuctionEventListener listener) {
    this.listener = listener;
  }

  @Override
  public void processMessage(Chat chat, Message message) {
    final Map<String, String> event = unpackEvent(message);
    final String type = event.get("Event");
    if ("CLOSE".equals(type)) {
      listener.auctionClosed();
    } else if ("PRICE".equals(type)) {
      listener.currentPrice(
        parseInt(event.get("CurrentPrice")),
        parseInt(event.get("Increment"))
      );
    }
  }

  private static Map<String, String> unpackEvent(Message message) {
    final HashMap<String, String> event = new HashMap<>();
    for (String element : message.getBody().split(";")) {
      final String[] pair = element.split(":");
      event.put(pair[0].trim(), pair[1].trim());
    }
    return event;
  }
}
