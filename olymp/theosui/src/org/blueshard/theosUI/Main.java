package org.blueshard.theosUI;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.blueshard.theosUI.UIStarter.Login;
import org.blueshard.theosUI.client.Action;
import org.blueshard.theosUI.client.ServerSender;
import org.blueshard.theosUI.config.Config;
import org.blueshard.theosUI.data.DataCodes;
import org.blueshard.theosUI.exception.FatalIOException;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.logging.MainLogger;
import org.blueshard.theosUI.utils.SVGToGenericImage;
import org.blueshard.theosUI.utils.TheosUIImage;
import org.blueshard.theosUI.utils.UIUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.atomic.AtomicReference;

import static org.blueshard.theosUI.utils.UIUtils.errorAlert;
import static org.blueshard.theosUI.utils.UIUtils.onClose;

public class Main extends Application {

    public static final String version = "0.1.0";

    protected static Socket server;
    protected static Config config;
    protected static Logger logger;
    protected static byte[] privateKey;
    protected static ServerSender serverSender;
    protected static Action action;

    public static void main(String[] args) throws IOException, UnexpectedException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException {
        /*config = new Config();
        if (config.getFileLogging()) {
            logger = new MainLogger(config.getLogLevel(), OSUtils.getLogFile().getAbsolutePath()).getLogger();
        } else {
            logger = new MainLogger(config.getLogLevel()).getLogger();
        }*/
        logger = new MainLogger(Level.ALL).getLogger();
        server = SocketFactory.getDefault().createSocket("localhost", 8269);
        logger.info("Connected to server");

        Object[] keys;
        try {
            keys = Action.initE2EE(server.getInputStream(), server.getOutputStream(), logger);
            logger.info("Received server public key");
        } catch (UnexpectedException ignore) {
            return;
        }
        privateKey = (byte[]) keys[0];
        byte[] serverPublicKey = (byte[]) keys[1];

        serverSender = new ServerSender(server.getOutputStream(), serverPublicKey, logger);
        action = new Action(server.getInputStream(), privateKey, serverSender);

        try {
            if (action.initConnection()) {
                int update = action.decryptE2EEData().getCode();

                if (update == DataCodes.Server.OPTIONALUPDATE) {
                    //
                } else if (update == DataCodes.Server.REQUIREDUPDATE) {
                    //
                }
            }
        } catch (FatalIOException e) {
            logger.fatal("Send wrong server agent, close connection");
            server.close();
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(Main::notCaughtExceptionAlert);

        AnchorPane main = FXMLLoader.load(getClass().getResource("resources/main.fxml"));
        Scene scene = new Scene(main, 1280, 720);

        main.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> onClose());

        primaryStage.setTitle(UIUtils.HEADING);
        try {
            primaryStage.getIcons().add(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.theos_logo).asStream(),
                    SVGToGenericImage.Transcoder.PNG, 30f, 30f).asByteArrayInputStream()));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(scene);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                primaryStage.setResizable(false);
                try {
                    new Login(primaryStage).getStage().show();
                } catch (IOException e) {
                    logger.fatal("Couldn't show login window");
                    errorAlert(null, "Login Fenster konnte nicht angezeigt werden", e);
                }
            });
        });
        t.start();

        primaryStage.show();
    }

    private static void notCaughtExceptionAlert(Thread thread, Throwable throwable) {
        throwable.printStackTrace();

        AtomicReference<Double> exceptionAlertX = new AtomicReference<>(Screen.getPrimary().getBounds().getMaxX() / 2);
        AtomicReference<Double> exceptionAlertY = new AtomicReference<>(Screen.getPrimary().getBounds().getMaxY() / 2);

        Alert notCaughtException = new Alert(Alert.AlertType.ERROR, "Error: " + throwable, ButtonType.OK);
        notCaughtException.setTitle("Error");
        notCaughtException.setResizable(true);
        try {
            ((Stage) notCaughtException.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.theos_logo).asStream(),
                    SVGToGenericImage.Transcoder.PNG, 30f, 30f).asByteArrayInputStream()));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        notCaughtException.getDialogPane().setContent(new Label("Error: " + throwable));

        Scene window = notCaughtException.getDialogPane().getScene();

        window.setOnMouseDragged(dragEvent -> {
            notCaughtException.setX(dragEvent.getScreenX() + exceptionAlertX.get());
            notCaughtException.setY(dragEvent.getScreenY() + exceptionAlertY.get());
        });
        window.setOnMousePressed(pressEvent -> {
            exceptionAlertX.set(window.getX() - pressEvent.getSceneX());
            exceptionAlertY.set(window.getY() - pressEvent.getSceneY());
        });

        notCaughtException.show();
    }

    public static Action getAction() {
        return action;
    }

    public static Logger getLogger() {
        return logger;
    }

}
