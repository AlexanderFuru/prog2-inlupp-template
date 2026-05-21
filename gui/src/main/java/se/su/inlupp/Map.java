package se.su.inlupp;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Map extends Pane {
    private final ImageView backgroundImageView;

    private StationView firstSelectedStation;

    public boolean isConnecting = false;

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
        System.out.println("Input detected at " + x + ", " + y);
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
                System.out.println("Connecting stations...Cannot select the same station, choose the second station");
                return;
            }

            System.out.println("Second station has been selected: " + clickedStation.getStation().getName());
            se.su.inlupp.GraphEdge<se.su.inlupp.Station> testEdge = new se.su.inlupp.GraphEdge<Station>(clickedStation.getStation(), "TestLine", 5);

            connectStations(firstSelectedStation, clickedStation, testEdge);

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
