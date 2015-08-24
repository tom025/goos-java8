package uk.org.tom025.auctionsniper;

import uk.org.tom025.auctionsniper.ui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static uk.org.tom025.auctionsniper.ui.controller.MainController.*;

public class Main extends Application {
  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;
  private static final int ARG_ITEM_ID = 3;
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final CountDownLatch uiReadySignal = new CountDownLatch(1);
  private MainController mainController;
  private Chat notToBeGCd;
  private XMPPConnection connection;

  public static void main(String... args) throws Exception {
    launch(args);
  }

  public void joinAuction(XMPPConnection connection, String itemId) throws XMPPException, InterruptedException {
    final Chat chat = connection.getChatManager().createChat(
      auctionId(itemId, connection),
      null
    );
    Auction auction = new XMPPAuction(chat);
    notToBeGCd = chat;
    uiReadySignal.await();
    chat.addMessageListener(
      new AuctionMessageTranslator(
        connection.getUser().replaceAll("@\\w+/\\w+$", ""),
        new AuctionSniper(auction, new SniperStateDisplayer(mainController))
      )
    );
    auction.join();
  }

  @Override
  public void init() throws Exception {
    final List<String> args = getParameters().getRaw();
    executorService.submit(
      () -> {
        try {
          connection = connection(
            args.get(ARG_HOSTNAME),
            args.get(ARG_USERNAME),
            args.get(ARG_PASSWORD)
          );
          joinAuction(connection, args.get(ARG_ITEM_ID));
        } catch (XMPPException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    );
  }

  private static String auctionId(String itemId, XMPPConnection connection) {
    return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
  }

  private static XMPPConnection connection(
    final String hostname,
    final String username,
    final String password
  ) throws XMPPException {
    final XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);
    return connection;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    startUserInterface(primaryStage);
    uiReadySignal.countDown();
  }

  @Override
  public void stop() throws Exception {
    if (connection != null) { connection.disconnect(); }
  }

  private void startUserInterface(Stage primaryStage) throws java.io.IOException {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
    final StackPane root = loader.load();
    mainController = loader.getController();
    primaryStage.setTitle("Auction Sniper");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.show();
  }


  private static class XMPPAuction implements Auction {
    private final Chat chat;

    public XMPPAuction(Chat chat) {
      this.chat = chat;
    }

    @Override
    public void join() {
      sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void bid(int amount) {
      sendMessage(format(BID_COMMAND_FORMAT, amount));
    }

    private void sendMessage(String message) {
      try {
        chat.sendMessage(message);
      } catch (XMPPException e) {
        e.printStackTrace();
      }
    }

  }

  private static class SniperStateDisplayer implements SniperListener {
    private final MainController mainController;

    public SniperStateDisplayer(MainController mainController) {
      this.mainController = mainController;
    }

    @Override
    public void sniperBidding() {
      Platform.runLater(() -> mainController.showStatus(STATUS_BIDDING));
    }

    @Override
    public void sniperWinning() {
      Platform.runLater(() -> mainController.showStatus(STATUS_WINNING));
    }

    @Override
    public void sniperWon() {
      Platform.runLater(() -> mainController.showStatus(STATUS_WON));
    }

    @Override
    public void sniperLost() {
      Platform.runLater(() -> mainController.showStatus(STATUS_LOST));
    }
  }
}
