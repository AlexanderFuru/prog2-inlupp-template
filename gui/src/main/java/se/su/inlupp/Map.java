package se.su.inlupp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Map extends Pane {
    private final ImageView backgroundImageView;

    private StationView firstSelectedStation;
    private StationView stationToRemove;

    private EdgeLine lineToRemove;
    private TransitLine currentTransitLine;

    protected boolean isConnecting = false;
    protected boolean isRemoving = false;

    public Map() {
        this.backgroundImageView = new ImageView();

        this.backgroundImageView.setPreserveRatio(true);

        this.getChildren().add(backgroundImageView);

        setupMouseListeners();
    }

    //#region Map

    public void setBackgroundImage(File imageFile) {
        if (imageFile != null && imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString());
            backgroundImageView.setImage(image);

            this.setPrefSize(image.getWidth(), image.getHeight());
        }
    }

    private void setupMouseListeners() {
        this.setOnMouseClicked(event -> {
            if (event.getTarget() == this || event.getTarget() == backgroundImageView) {
                double x = event.getX();
                double y = event.getY();

                handleMapClick(x, y);
            }
        });
    }

    private void handleMapClick(double x, double y) {
        if (isConnecting) {
            isConnecting = false;

            if (firstSelectedStation != null) {
                firstSelectedStation.setFill(javafx.scene.paint.Color.WHITE);
                System.out.println("Connect mode has been canceled");
                return;
            }
        }

        if (isRemoving) {
            isRemoving = false;
            System.out.println("Remove mode has been canceled");
        }

        handleNewStation(x, y);
            
        System.out.println("Input detected at " + x + ", " + y);
    }

    //#endregion

    //#region AddObjects

    public void handleNewStation(double mouseClickX, double mouseClickY) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New station");
        dialog.setHeaderText("Add new station");
        dialog.setContentText("Enter station name");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String stationName = result.get().trim();

            if (stationName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid input");
                alert.setHeaderText("Invalid name");
                alert.setContentText("Station name may not be empty, please enter station name");
                alert.showAndWait();
                handleNewStation(mouseClickX, mouseClickY);
                return;
            }

            Station newStation = new Station(stationName);

            //Skicka till modellen att den ska lägga till den nya noden istället

            addStationToMap(newStation, mouseClickX, mouseClickY);

            System.out.println("New station has been created: " + stationName);
        }
    }

    public void addStationToMap(Station staion, double x, double y) {
        StationView stationView = new StationView(staion, x, y);

        this.getChildren().addAll(stationView.getLabel(), stationView);
    }

    public void setCurrentTransitLine(ComboBox<TransitLine> transitLineSelectionBox) {
        currentTransitLine = transitLineSelectionBox.getValue();
    }

    //#endregion

    //#region Connect Objects

    public void startConnectingStations() {
        this.isConnecting = true;
        this.firstSelectedStation = null;
        System.out.println("Connecting stations...Choose the first station");
    }

    public void chooseConnectingStations(StationView clickedStation) {
        if (firstSelectedStation == null) {
            firstSelectedStation = clickedStation;
            firstSelectedStation.setFill(javafx.scene.paint.Color.GREEN);
            System.out.println("Connecting stations...First station has been selected: " + firstSelectedStation.getStation().getName() + ", choose the second station");
        }
        else {
            if (firstSelectedStation == clickedStation) {
                DialogHandler.showErrorAlert("Error", "Invalid connection", "A station cannot be connected to itself");
                return;
            }

            Optional<DialogHandler.LineInputData> input = DialogHandler.showConnectDialog(firstSelectedStation.getStation().getName(), clickedStation.getStation().getName());
            if (input.isPresent()) {
                DialogHandler.LineInputData inputData = input.get();

                se.su.inlupp.GraphEdge<se.su.inlupp.Station> testEdge = new se.su.inlupp.GraphEdge<>(clickedStation.getStation(), currentTransitLine.getName(), inputData.weight);
                System.out.println("Second station has been selected: " + clickedStation.getStation().getName());
                connectStations(firstSelectedStation, clickedStation, testEdge);
            }
            
            firstSelectedStation.setFill(javafx.scene.paint.Color.WHITE);
            System.out.println("Stations " + firstSelectedStation.getStation().getName() + " and " + clickedStation.getStation().getName() + " have been connected");
            System.out.println("Exiting connect mode");
            isConnecting = false;
            firstSelectedStation = null;
        }
    }

    private void connectStations(StationView station1, StationView station2, GraphEdge<Station> edge) {
        EdgeLine edgeView = new EdgeLine(station1, station2, edge, currentTransitLine.getColor());

        //Skicka till modellen att den ska koppla två noder

        this.getChildren().add(edgeView);

        edgeView.toBack();
    }

    //#endregion

    //#region Remove Objects

    public void startRemovingObject() {
        isRemoving = true;
        stationToRemove = null;
        lineToRemove = null;
    }

    public void chooseStationToRemove(StationView clickedStation) {
        if (stationToRemove == null) {
            stationToRemove = clickedStation;
            removeStation(clickedStation);
        }
    }

    public void chooseLineToRemove(EdgeLine clickedLine) {
        if (lineToRemove == null) {
            lineToRemove = clickedLine;
            removeLine(clickedLine);
        }
    }

    private void removeStation(StationView clickedStaion) {
        Station station = clickedStaion.getStation();

        List<EdgeLine> linesToRemove = new ArrayList<>();
        for (javafx.scene.Node node : this.getChildren()) {
            if (node instanceof EdgeLine edgeLine) {
                if (edgeLine.getFromStationView() == stationToRemove || edgeLine.getToStationView() == stationToRemove) {
                    linesToRemove.add(edgeLine);
                }
            }
        }

        this.getChildren().removeAll(linesToRemove);
        this.getChildren().remove(stationToRemove);
        this.getChildren().remove(stationToRemove.getLabel());

        //Skicka till modellen att station ska tas bort
        
        System.out.println(stationToRemove.getStation().getName() + " Has been removed");
        System.out.println("Exiting station remove mode");
        stationToRemove = null;
        isRemoving = false;
    }

    private void removeLine(EdgeLine clickedLine) {
        GraphEdge<Station> edge = clickedLine.getEdgeData();

        //Skicka till modellen att edge ska tas bort

        this.getChildren().remove(clickedLine);
        System.out.println(clickedLine.getEdgeData().getName() + " has been removed");
        lineToRemove = null;
        isRemoving = false;
    }

    //#endregion
}
