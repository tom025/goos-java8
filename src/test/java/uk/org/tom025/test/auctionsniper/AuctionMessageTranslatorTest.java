package uk.org.tom025.test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.mockito.Mockito;
import uk.org.tom025.auctionsniper.AuctionEventListener;
import uk.org.tom025.auctionsniper.AuctionMessageTranslator;

import static org.mockito.Mockito.*;

public class AuctionMessageTranslatorTest {
  private static final Chat UNUSED_CHAT = null;
  private final AuctionEventListener listener = mock(AuctionEventListener.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

  @Test
  public void notifiesAuctionClosedWhenClosedMessageReceived() throws Exception {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: CLOSE");

    translator.processMessage(UNUSED_CHAT, message);

    verify(listener).auctionClosed();
  }

  @Test
  public void notifiesBidDetailsWhenCurrentPriceMessageReceived() throws Exception {
    final Message message = new Message();
    message.setBody(
      "SOLVersion: 1.1;" +
        " Event: PRICE;" +
        " CurrentPrice: 192;" +
        " Increment: 7;" +
        " Bidder: Someone else;"
    );

    translator.processMessage(UNUSED_CHAT, message);

    verify(listener).currentPrice(192, 7);
  }
}
