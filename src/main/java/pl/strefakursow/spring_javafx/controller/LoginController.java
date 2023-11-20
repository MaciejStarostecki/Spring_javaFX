package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.Main;
import pl.strefakursow.spring_javafx.dto.OperatorCredentialsDto;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.Authenticator;
import pl.strefakursow.spring_javafx.rest.AuthenticatorImplementation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public static final String APP_FXML = "app.fxml";
    public static final String APP_TITTLE = "MS_System";
    private PopupFactory popupFactory;
    private Authenticator authenticator;

    @FXML
    private Button exitButton;

    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private TextField loginTextField;

    @FXML
    private TextField passwordTextField;

    public LoginController() {
        popupFactory = new PopupFactory();
        authenticator = new AuthenticatorImplementation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeExitButton();
        initializeLoginButton();
    }

    private void initializeLoginButton() {
        loginButton.setOnAction(actionEvent -> {
            performAuthentication();
        });
    }

    private void performAuthentication() {
        Stage waitingPopup = popupFactory.createWaitingPopup("Connecting to the server...");
        waitingPopup.show();
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        OperatorCredentialsDto dto = new OperatorCredentialsDto();
        dto.setLogin(login);
        dto.setPassword(password);

        authenticator.authenticate(dto, authenticationResult -> {
            Platform.runLater(() -> waitingPopup.close());
            if(authenticationResult.isAuthenticated()) {
                openAppAndCloseLoginStage();
            }
            else showIncorrectCredentialsMessage();
        });


    }

    private void showIncorrectCredentialsMessage() {
//        TODO implement method
    }

    private void openAppAndCloseLoginStage() {
        Stage appStage = new Stage();
        Parent appRoot = null;
        try {
            //metoda getClass() nie działa
            appRoot = FXMLLoader.load(getClass().getResource(APP_FXML));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(appRoot, 1200, 800);
        appStage.setTitle(APP_TITTLE);
        appStage.setScene(scene);
        appStage.show();
        getStage().close();

    }

    private void initializeExitButton() {
        exitButton.setOnAction(actionEvent -> {
            getStage().close();
        });
    }

    private Stage getStage() {
        return (Stage) loginAnchorPane.getScene().getWindow();
    }
}
