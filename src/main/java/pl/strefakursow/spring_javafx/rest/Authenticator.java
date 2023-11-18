package pl.strefakursow.spring_javafx.rest;

import pl.strefakursow.spring_javafx.dto.OperatorCredentialsDto;

public interface Authenticator {

    void authenticate(OperatorCredentialsDto operatorCredentialsDto, AuthenticationResultHandler authenticationResultHandler);
}
