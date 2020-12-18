package it.unito.prog.scene;

import it.unito.prog.Main;
import it.unito.prog.controllers.BaseController;
import it.unito.prog.utils.PlatformUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SceneSwitcher {
    public static SceneSwitcher instance;
    private final ConcurrentHashMap<AppScene, Tuple> scenes;
    private final Scene main;

    public SceneSwitcher(Stage stage) {
        this.scenes = new ConcurrentHashMap<>();
        this.main = new Scene(new Pane());
        stage.setScene(this.main);

        instance = this;
    }

    public static SceneSwitcher getInstance() {
        return instance;
    }

    public void addScene(AppScene appScene) throws IOException {
        String fxml = appScene.toString();
        String location = fxml + '/' + fxml + "_scene.fxml";
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(location));
        scenes.put(appScene, new Tuple(loader.load(), loader.getController()));
    }

    public BaseController getController(AppScene name) {
        Tuple tuple = scenes.get(name);
        return tuple.controller;
    }

    public void switchTo(AppScene name) {
        Tuple tuple = scenes.get(name);
        PlatformUtil.safeRun(() -> main.setRoot(tuple.view));
    }

    /**
     * Tuple<Pane, BaseController> Sub-Class
     */
    private final static class Tuple {
        private final Pane view;
        private final BaseController controller;

        private Tuple(Pane view, BaseController controller) {
            this.view = view;
            this.controller = controller;
        }
    }
}
