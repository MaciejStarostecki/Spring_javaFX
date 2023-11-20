package pl.strefakursow.spring_javafx.rest;

import pl.strefakursow.spring_javafx.dto.OperatorCredentialsDto;
import pl.strefakursow.spring_javafx.handler.AuthenticationResultHandler;

public interface Authenticator {

    void authenticate(OperatorCredentialsDto operatorCredentialsDto, AuthenticationResultHandler authenticationResultHandler);
}
