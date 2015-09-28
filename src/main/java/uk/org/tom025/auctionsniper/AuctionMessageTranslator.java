package uk.org.tom025.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslator implements MessageListener {
  private final AuctionEventListener listener;
  private final String sniper_id;

  public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
    this.listener = listener;
    this.sniper_id = sniperId;
  }

  @Override
  public void processMessage(Chat chat, Message message) {
    final AuctionEvent event = AuctionEvent.from(message.getBody());
    final String eventType = event.type();
    if ("CLOSE".equals(eventType)) {
      listener.auctionClosed();
    } else if ("PRICE".equals(eventType)) {
      listener.currentPrice(
        event.currentPrice(),
        event.increment(),
        event.isFrom(sniper_id)
      );
    }
  }

  private static class AuctionEvent {
    private final Map<String, String> fields = new HashMap<>();

    static AuctionEvent from(String messageBody) {
      AuctionEvent event = new AuctionEvent();
      for (String field : fieldsIn(messageBody)) {
        event.addField(field);
      }
      return event;
    }

    private void addField(String field) {
      final String[] pair = field.split(":");
      fields.put(pair[0].trim(), pair[1].trim());
    }

    private static String[] fieldsIn(String messageBody) {
      return messageBody.split(";");
    }

    private String get(String key) {
      return fields.get(key);
    }

    public String type() {
      return fields.get("Event");
    }

    private int currentPrice() {
      return getInt("CurrentPrice");
    }

    private int getInt(String key) {
      return parseInt(get(key));
    }

    private int increment() {
      return getInt("Increment");
    }

    public PriceSource isFrom(String sniperId) {
      return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
    }

    private String bidder() {
      return get("Bidder");
    }
  }

}
