package pl.strefakursow.spring_javafx.rest;

import pl.strefakursow.spring_javafx.dto.OperatorAuthenticationResultDto;

@FunctionalInterface
public interface AuthenticationResultHandler {

    void handle(OperatorAuthenticationResultDto operatorAuthenticationResultDto);
}
