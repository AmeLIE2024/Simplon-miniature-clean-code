package fr.simplon.infrastructure.strategies.errors;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;

public class ErrorHandlingStrategyImpl implements ErrorHandlingStrategy {

    @Override
    public void handleError(Exception e) {
        System.out.println("[FATAL: Tomcat n'a pas pu démarrer ] " + e.getMessage());
    }
}
