package auctionsniper;

import auctionsniper.ui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class Main extends Application {
  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;
  private static final int ARG_ITEM_ID = 3;
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final CountDownLatch uiReadySignal = new CountDownLatch(1);
  private MainController mainController;
  private volatile Chat notToBeGCd;

  public static void main(String... args) throws Exception {
    launch(args);
  }

  public void joinAuction(XMPPConnection connection, String itemId) throws XMPPException, InterruptedException {
    final Chat chat = connection.getChatManager().createChat(
      auctionId(itemId, connection),
      (aChat, message) -> {
        Platform.runLater(() -> {
          mainController.showStatus(MainController.STATUS_LOST);
        });
      }
    );
    notToBeGCd = chat;
    uiReadySignal.await();
    chat.sendMessage(new Message());
  }

  @Override
  public void init() throws Exception {
    final List<String> args = getParameters().getRaw();
    executorService.submit(
      () -> {
        try {
          joinAuction(
            connection(
              args.get(ARG_HOSTNAME),
              args.get(ARG_USERNAME),
              args.get(ARG_PASSWORD)
            ),
            args.get(ARG_ITEM_ID)
          );
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

  private void startUserInterface(Stage primaryStage) throws java.io.IOException {
    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
    final StackPane root = loader.load();
    mainController = loader.getController();
    primaryStage.setTitle("Auction Sniper");
    primaryStage.setScene(new Scene(root, 800, 600));
    primaryStage.show();
  }
}
