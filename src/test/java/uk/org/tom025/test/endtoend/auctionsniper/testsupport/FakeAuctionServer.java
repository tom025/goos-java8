package uk.org.tom025.test.endtoend.auctionsniper.testsupport;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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

  public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
    messageListener.receivesAMessage();
  }

  public void announceClosed() throws XMPPException {
    currentChat.sendMessage(new Message());
  }

  public void stop() {
    connection.disconnect();
  }

  private static class SingleMessageListener implements MessageListener {
    private final ArrayBlockingQueue<Message> messages =
      new ArrayBlockingQueue<>(1);

    @Override
    public void processMessage(Chat chat, Message message) {
      messages.add(message);
    }

    public void receivesAMessage() throws InterruptedException {
      assertThat(
        "Message",
        messages.poll(5, SECONDS),
        is(notNullValue())
      );
    }
  }
}
