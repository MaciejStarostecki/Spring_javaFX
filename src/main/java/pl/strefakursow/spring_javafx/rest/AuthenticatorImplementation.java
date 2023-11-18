package pl.strefakursow.spring_javafx.rest;

import org.springframework.http.ResponseEntity;
import pl.strefakursow.spring_javafx.dto.OperatorAuthenticationResultDto;
import pl.strefakursow.spring_javafx.dto.OperatorCredentialsDto;
import org.springframework.web.client.RestTemplate;

public class AuthenticatorImplementation implements Authenticator {

    private final RestTemplate restTemplate;

    private static final String AUTHENTICATION_URL = "http://localhost:8080/verify_operator_credentials";

    public AuthenticatorImplementation() {
        restTemplate = new RestTemplate();
    }

    @Override
    public void authenticate(OperatorCredentialsDto operatorCredentialsDto, AuthenticationResultHandler authenticationResultHandler) {
        Runnable authenticationTask = () -> processAuthentication(operatorCredentialsDto, authenticationResultHandler);
        Thread authenticationThread = new Thread(authenticationTask);
        authenticationThread.setDaemon(true);
        authenticationThread.start();

    }

    private void processAuthentication(OperatorCredentialsDto operatorCredentialsDto, AuthenticationResultHandler authenticationResultHandler) {
        ResponseEntity<OperatorAuthenticationResultDto> responseEntity = restTemplate.postForEntity(AUTHENTICATION_URL, operatorCredentialsDto, OperatorAuthenticationResultDto.class);
        authenticationResultHandler.handle(responseEntity.getBody());
    }
}
