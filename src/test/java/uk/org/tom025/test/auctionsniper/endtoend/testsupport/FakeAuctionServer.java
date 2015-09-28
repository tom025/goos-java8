package uk.org.tom025.test.auctionsniper.endtoend.testsupport;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import uk.org.tom025.auctionsniper.Main;

import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static uk.org.tom025.auctionsniper.Main.BID_COMMAND_FORMAT;

public class FakeAuctionServer {
  private static final String XMPP_HOSTNAME = "localhost";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_PASSWORD = "auction";
  private static final String AUCTION_RESOURCE = "Auction";
  private final String itemId;
  private final XMPPConnection connection;
  private Chat currentChat;
  private final SingleMessageListener messageListener = new SingleMessageListener();

  public FakeAuctionServer(String itemId) {
    this.itemId = itemId;
    this.connection = new XMPPConnection(XMPP_HOSTNAME);
  }

  public String getItemId() {
    return itemId;
  }

  public void startSellingItem() throws XMPPException {
    connection.connect();
    connection.login(format(ITEM_ID_AS_LOGIN, itemId),
      AUCTION_PASSWORD,
      AUCTION_RESOURCE);
    connection.getChatManager().addChatListener(
      (chat, createdLocally) -> {
        currentChat = chat;
        chat.addMessageListener(messageListener);
      }
    );
  }

  public void hasReceivedJoinRequestFromSniper(
    String sniperId
  ) throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
  }

  private void receivesAMessageMatching(
    String sniperId,
    Matcher<? super String> messageMatcher
  ) throws InterruptedException {
    messageListener.receivesAMessage(messageMatcher);
    assertThat(currentChat.getParticipant(), startsWith(sniperId));
  }

  public void announceClosed() throws XMPPException {
    currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
  }

  public void stop() {
    connection.disconnect();
  }

  public void reportPrice(int price, int increment, String bidder) throws XMPPException {
    currentChat.sendMessage(format(
      "SOLVersion: 1.1; Event: PRICE; "
        + "CurrentPrice: %d; Increment: %d; Bidder: %s;",
      price, increment, bidder));
  }

  public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(format(BID_COMMAND_FORMAT, bid)));
  }

  private static class SingleMessageListener implements MessageListener {
    private final ArrayBlockingQueue<Message> messages =
      new ArrayBlockingQueue<>(1);

    @Override
    public void processMessage(Chat chat, Message message) {
      messages.add(message);
    }

    public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
      final Message message = messages.poll(5, SECONDS);
      assertThat("Message", message, is(notNullValue()));
      assertThat(message.getBody(), messageMatcher);
    }
  }
}
