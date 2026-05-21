package se.su.inlupp;

import java.io.File;
import java.util.Random;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainView extends BorderPane {

    private Map map;
    private final double newStationSpawnPositionX = 45;
    private final double newStationSpawnPositionY = 35;
    
    public MainView() {
        setupLayout();
    }

    private void setupLayout() {
        MenuBar menuBar = createMenuBar();

        ToolBar toolBar = createToolBar();

        VBox topContainer = new VBox(menuBar, toolBar);
        this.setTop(topContainer);

        this.map = new Map();
        this.setCenter(map);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Archive");

        MenuItem newItem = new MenuItem("Create new map");
        newItem.setOnAction(e -> handleNewMap());

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> handleOpen());

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> handleSave());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> handleExit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        Button newStationButton = new Button("Add station");
        newStationButton.setOnAction(e -> handleNewStation());

        Button connectStationsButton = new Button("Connect stations");
        connectStationsButton.setOnAction(e -> handleConnectStations());

        Button findRouteButton = new Button("Find route");
        findRouteButton.setOnAction(e -> handleFindRoute());

        ComboBox<String> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll("Fastest route (BFS)", "Fewest transfers (DFS)");
        algorithmBox.setValue("Fastest route (BFS)");
        algorithmBox.setOnAction(e -> handleAlgorithmChange(algorithmBox.getValue() ));

        toolBar.getItems().addAll(newStationButton, connectStationsButton, findRouteButton, new Separator(), new Label("Algorithm: "), algorithmBox);

        return toolBar;
    }

    private void handleNewMap() {
        System.out.println("Operation -Create new map- has been chosen");

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select background image");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg"));

        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            map.setBackgroundImage(file);
        }
    }

    private void handleOpen() {
        System.out.println("Operation -Open- has been chosen");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textfiler (*.txt)", "*.txt"));

        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            System.out.println("Loading file: " + file.getAbsolutePath());
        }
    }

    private void handleSave() {
        System.out.println("Operation -Save- has been chosen");
    }

    private void handleExit() {
        System.out.println("Operation -Exit- has been chosen");
    }

    private void handleNewStation() {
        String[] stationNames = {"T-Centralen", "Gamla Stan", "Slussen", "Rådmansgatan", "Tekniska Högskolan"};
        int index = new Random().nextInt(stationNames.length);
        Station newStation = new Station(stationNames[index]);

        map.addStationToMap(newStation, newStationSpawnPositionX, newStationSpawnPositionY);
        System.out.println("Operation -New station- has been chosen");
    }

    private void handleConnectStations() {
        map.startConnectingStations();
    }

    private void handleFindRoute() {
        System.out.println("Operation -Find route- has been chosen");
    }

    private void handleAlgorithmChange(String selectedAlgorithm) {
        if (selectedAlgorithm.contains("BFS")) {
            System.out.println("Fastest route has been chosen");
        }
        else {
            System.out.println("Fewest transfers has been chosen");
        }
    }
}
