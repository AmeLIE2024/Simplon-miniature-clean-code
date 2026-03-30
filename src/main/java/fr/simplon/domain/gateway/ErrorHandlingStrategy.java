package fr.simplon.domain.gateway;

public interface ErrorHandlingStrategy {

    public void handleError (Exception e);
}
