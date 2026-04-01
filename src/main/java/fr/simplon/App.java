package fr.simplon;

import fr.simplon.domain.gateway.services.TomcatService;
import fr.simplon.infrastructure.services.tomcat.TomcatServiceImpl;
import fr.simplon.infrastructure.strategies.errors.ErrorHandlingStrategyImpl;
import fr.simplon.infrastructure.strategies.logs.LogStrategyImpl;

public class App {

    public static void main(String[] args) {
        TomcatService tomcatService = new TomcatServiceImpl(
                new LogStrategyImpl(),
                new ErrorHandlingStrategyImpl());
        tomcatService.setUpTomcat();
    }
}