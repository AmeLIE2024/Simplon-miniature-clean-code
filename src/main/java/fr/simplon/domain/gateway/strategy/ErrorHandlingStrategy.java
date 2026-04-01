package fr.simplon.domain.gateway.strategy;

public interface ErrorHandlingStrategy {

    public void handleError(Exception e);
}
