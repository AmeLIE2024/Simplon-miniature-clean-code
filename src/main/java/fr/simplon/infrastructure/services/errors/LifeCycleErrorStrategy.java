package fr.simplon.infrastructure.services.errors;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;

public class LifeCycleErrorStrategy implements ErrorHandlingStrategy {


    @Override
    public void handleError(Exception e) {
        System.out.println("[FATAL: Tomcat n'a pas pu démarrer ] " + e.getMessage());
    }
}
