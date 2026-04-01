package fr.simplon.domain.strategy;

public interface ErrorHandlingStrategy {

    public void handleError(Exception e);
}
