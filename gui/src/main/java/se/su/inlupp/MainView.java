package se.su.inlupp;

import java.io.File;
import java.util.Optional;

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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainView extends BorderPane {

    private Map map;

    private ComboBox<TransitLine> transitLineSelectionBox;
    
    public MainView() {
        setupLayout();
    }

    //#region Layout

    private void setupLayout() {
        MenuBar menuBar = createMenuBar();

        ToolBar toolBar = createToolBar();

        VBox topContainer = new VBox(menuBar, toolBar);
        this.setTop(topContainer);

        this.map = new Map(new RoutePlanner(new BFSPathFinder<>()));
        this.setCenter(map);

        handleChangeTransitLine();
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

        Button newTransitLineButton = new Button("Create transit line");
        newTransitLineButton.setOnAction(e -> handleNewTransitLine());

        Button connectStationsClickButton = new Button("Connect stations");
        connectStationsClickButton.setOnAction(e -> handleConnectStations());

        Button removeStationButton = new Button("Remove object");
        removeStationButton.setOnAction(e -> handleRemoveObject());

        Button findRouteButton = new Button("Find route");
        findRouteButton.setOnAction(e -> handleFindRoute());

        Button clearRouteButton = new Button("Clear route");
        clearRouteButton.setOnAction(e -> handleClearRoute());

        transitLineSelectionBox = new ComboBox<>();
        transitLineSelectionBox.setPromptText("Select current transit line");
        transitLineSelectionBox.getItems().add(new TransitLine("Green Line", Color.GREEN));
        transitLineSelectionBox.getItems().add(new TransitLine("Red Line", Color.RED));
        transitLineSelectionBox.setValue(transitLineSelectionBox.getItems().get(0));
        transitLineSelectionBox.getSelectionModel().selectedItemProperty().addListener((obj, oldLine, newLine) -> {
            if (newLine != null) {
                handleChangeTransitLine();
            }
        });

        ComboBox<String> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll("Fastest route (BFS)", "Fewest transfers (DFS)");
        algorithmBox.setValue("Fastest route (BFS)");
        algorithmBox.setOnAction(e -> handleAlgorithmChange(algorithmBox.getValue() ));

        toolBar.getItems().addAll(
        newTransitLineButton, new Label("Current transit line: "), transitLineSelectionBox, new Separator(), 
        connectStationsClickButton,  new Separator(), 
        removeStationButton, new Separator(), 
        new Label("Algorithm: "), algorithmBox, findRouteButton, clearRouteButton);

        return toolBar;
    }

    //#endregion

    //#region Handle Methods

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textfiler (*.txt)", "*.txt"));

        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            FileHandler fileHandler = new FileHandler();
            String imagePath = fileHandler.loadData(map.getRoutePlanner(),file);     
            map.RebuildMapFromGraph();

            if(imagePath != null){
             map.setBackgroundImage(new File(imagePath));
            }

            System.out.println("Loading file: " + file.getAbsolutePath());
        }
    }

    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textfiler (*.txt)", "*.txt"));

        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null){
         FileHandler fileHandler = new FileHandler();
         fileHandler.saveData(map.getRoutePlanner(), map.getImagePath(), file);

         System.out.println("Operation -Save- has been chosen");
        }
    }

    private void handleExit() {
        System.out.println("Operation -Exit- has been chosen");
    }

    private void handleNewTransitLine() {
        Optional<TransitLine> newTransitLine = DialogHandler.showNewTransitLineDialog();
        if (newTransitLine.isPresent()) {
            TransitLine transitLine = newTransitLine.get();
            transitLineSelectionBox.getItems().add(transitLine);
            transitLineSelectionBox.setValue(transitLine);
            map.setCurrentTransitLine(transitLineSelectionBox);

            System.out.println("New line created: " + transitLine.getName());
        }
    }

    private void handleChangeTransitLine() {
        map.setCurrentTransitLine(transitLineSelectionBox);
    }

    private void handleConnectStations() {
        map.startConnectingStations();
    }

    private void handleRemoveObject() {
        map.startRemovingObject();
    }

    private void handleFindRoute() {
        map.startChoosingRouteEndPoints();
    }

    private void handleClearRoute() {
        map.resetRouteVisuals();
    }

    private void handleAlgorithmChange(String selectedAlgorithm) {

        if (selectedAlgorithm.contains("BFS")) {
            map.getRoutePlanner().setPathFinder(new BFSPathFinder<>());
            System.out.println("BFS selected");
        }
        else {
            map.setAlgorithm(new DFSPathFinder<>());
            System.out.println("DFS selected");
        }
    }

    //#endregion
}
