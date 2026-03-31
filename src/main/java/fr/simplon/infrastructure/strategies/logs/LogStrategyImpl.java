package fr.simplon.infrastructure.strategies.logs;

import fr.simplon.domain.gateway.LogStrategy;

public class LogStrategyImpl implements LogStrategy {



    @Override
    public void logMessage(String message) {
        System.out.println("Votre fichier a bien été créé");
    }
}
