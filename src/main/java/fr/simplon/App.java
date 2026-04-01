package fr.simplon;

import fr.simplon.domain.exception.ErrorHandlingStrategyImpl;
import fr.simplon.domain.exception.LogStrategyImpl;
import fr.simplon.domain.services.TomcatService;
import fr.simplon.infrastructure.services.TomcatServiceImpl;

public class App {

    public static void main(String[] args) {
        TomcatService tomcatService = new TomcatServiceImpl(
                new LogStrategyImpl(),
                new ErrorHandlingStrategyImpl());
        tomcatService.setUpTomcat();
    }
}