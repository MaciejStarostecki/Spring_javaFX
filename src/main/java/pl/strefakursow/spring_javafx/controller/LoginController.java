package pl.strefakursow.spring_javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.strefakursow.spring_javafx.dto.OperatorCredentialsDto;
import pl.strefakursow.spring_javafx.factory.PopupFactory;
import pl.strefakursow.spring_javafx.rest.Authenticator;
import pl.strefakursow.spring_javafx.rest.AuthenticatorImplementation;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

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
            System.out.println("Authentication result: " + authenticationResult.isAuthenticated() + "Authentication result: " + authenticationResult.toString());
        });


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
