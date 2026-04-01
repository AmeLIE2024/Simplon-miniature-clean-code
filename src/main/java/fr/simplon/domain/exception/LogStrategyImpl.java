package fr.simplon.domain.exception;

import fr.simplon.domain.strategy.LogStrategy;

public class LogStrategyImpl implements LogStrategy {

    @Override
    public void logMessage(String message) {
        System.out.println("Votre fichier a bien été créé");
    }
}
