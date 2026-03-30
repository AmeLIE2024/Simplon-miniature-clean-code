package fr.simplon;

import fr.simplon.domain.gateway.TomcatService;
import fr.simplon.infrastructure.services.TomcatServiceImpl;

public class App {

    public static void main(String[] args) {

        TomcatService tomcatService = new TomcatServiceImpl();

        tomcatService.setUpTomcat();

    }
}