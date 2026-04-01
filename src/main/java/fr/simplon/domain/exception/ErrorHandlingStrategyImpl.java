package fr.simplon.domain.exception;

import fr.simplon.domain.strategy.ErrorHandlingStrategy;

public class ErrorHandlingStrategyImpl implements ErrorHandlingStrategy {

    @Override
    public void handleError(Exception e) {
        System.out.println("[FATAL: Tomcat n'a pas pu démarrer ] " + e.getMessage());
    }
}
