package fr.simplon;

import fr.simplon.domain.gateway.TomcatService;
import fr.simplon.infrastructure.services.TomcatServiceImpl;
import fr.simplon.infrastructure.services.errors.LifeCycleErrorStrategy;

public class App {

    public static void main(String[] args) {
        TomcatService tomcatService = new TomcatServiceImpl(new LifeCycleErrorStrategy());
        tomcatService.setUpTomcat();
    }
}