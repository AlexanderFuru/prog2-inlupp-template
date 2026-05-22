package se.su.inlupp;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Map extends Pane {
    private final ImageView backgroundImageView;

    private StationView firstSelectedStation;

    protected boolean isConnecting = false;

    public Map() {
        this.backgroundImageView = new ImageView();

        this.backgroundImageView.setPreserveRatio(true);

        this.getChildren().add(backgroundImageView);

        setupMouseListeners();
    }

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

        handleNewStation(x, y);
            
        System.out.println("Input detected at " + x + ", " + y);
    }

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
            addStationToMap(newStation, mouseClickX, mouseClickY);

            System.out.println("New station has been created: " + stationName);
        }
    }

    public void addStationToMap(Station staion, double x, double y) {
        StationView stationView = new StationView(staion, x, y);

        this.getChildren().addAll(stationView.getLabel(), stationView);
    }

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

                se.su.inlupp.GraphEdge<se.su.inlupp.Station> testEdge = new se.su.inlupp.GraphEdge<>(clickedStation.getStation(), inputData.name, inputData.weight);
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
        EdgeLine edgeView = new EdgeLine(station1, station2, edge);

        this.getChildren().add(edgeView);

        edgeView.toBack();
    }
}
