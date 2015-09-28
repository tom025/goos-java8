package uk.org.tom025.test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import uk.org.tom025.auctionsniper.AuctionEventListener;
import uk.org.tom025.auctionsniper.AuctionMessageTranslator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.org.tom025.auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslatorTest {
  private static final Chat UNUSED_CHAT = null;
  private static final String SNIPER_ID = "sniper_id";
  private final AuctionEventListener listener = mock(AuctionEventListener.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

  @Test
  public void notifiesAuctionClosedWhenClosedMessageReceived() throws Exception {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: CLOSE");

    translator.processMessage(UNUSED_CHAT, message);

    verify(listener).auctionClosed();
  }

  @Test
  public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
    final Message message = new Message();
    message.setBody(
      "SOLVersion: 1.1;" +
        " Event: PRICE;" +
        " CurrentPrice: 192;" +
        " Increment: 7;" +
        " Bidder: Someone else;"
    );

    translator.processMessage(UNUSED_CHAT, message);

    verify(listener).currentPrice(192, 7, FromOtherBidder);
  }

  @Test
  public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
    final Message message = new Message();
    message.setBody(
      "SOLVersion: 1.1;" +
        " Event: PRICE;" +
        " CurrentPrice: 192;" +
        " Increment: 7;" +
        " Bidder: " + SNIPER_ID + ";"
    );

    translator.processMessage(UNUSED_CHAT, message);

    verify(listener).currentPrice(192, 7, FromSniper);
  }
}
