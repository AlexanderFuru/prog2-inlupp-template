package se.su.inlupp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Map extends Pane {
    private final ImageView backgroundImageView;

    private StationView firstSelectedStation;
    private StationView stationToRemove;
    private StationView stationToStartRouteFrom;

    private EdgeLine lineToRemove;
    private TransitLine currentTransitLine;
    private final RoutePlanner routePlanner;

    private boolean isConnecting = false;
    private boolean isRemoving = false;
    private boolean isChoosingRoute = false;

    private final List<Station> stations = new ArrayList<>();

    public Map(RoutePlanner routePlanner) {
    this.routePlanner = routePlanner;
    this.backgroundImageView = new ImageView();
    this.backgroundImageView.setPreserveRatio(true);
    this.getChildren().add(backgroundImageView);
    setupMouseClick();
    }

    //#region Map

    public void setBackgroundImage(File imageFile) {
        if (imageFile != null && imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString());
            backgroundImageView.setImage(image);

            this.setPrefSize(image.getWidth(), image.getHeight());
        }
    }

    private void setupMouseClick() {
        this.setOnMouseClicked(event -> {
            if (event.getTarget() == this || event.getTarget() == backgroundImageView) {
                double x = event.getX();
                double y = event.getY();

                handleMapClick(x, y);
            }
        });
    }

private void handleMapClick(double x, double y) {

    if (isChoosingRoute) {
        return;
    }

    if (isConnecting) {
        isConnecting = false;
        System.out.println("connect canceled");
        return;
    }

    if (isRemoving) {
        isRemoving = false;
        System.out.println("remove canceled");
        return;
    }

    handleNewStation(x, y);
}

    //#endregion

    //#region Route

    public boolean getIsChoosingRoute() {
        return isChoosingRoute;
    }

    public void startChoosingRouteEndPoints() {
        isChoosingRoute = true;
        stationToStartRouteFrom = null;
        System.out.println("Calculating route...Choose start station");
    }

    public void chooseRouteEndPoints(StationView clickedStation) {


        if (stationToStartRouteFrom == null) {
            stationToStartRouteFrom = clickedStation;
            clickedStation.getShape().setFill(Color.YELLOW);

            System.out.println("Start station selected: "
                + clickedStation.getStation().getName());
            return;
        }

        if (stationToStartRouteFrom == clickedStation) {
            return;
        }

        Station from = stationToStartRouteFrom.getStation();
        Station to = clickedStation.getStation();

        System.out.println("End station selected: " + to.getName());

        Path<Station> path = routePlanner.getRoute(from, to);
        int totalTime = calculateTotalTime(path);

        System.out.println("FROM: " + from.getName());
        System.out.println("TO: " + to.getName());
        System.out.println("Total travel time: " + totalTime);

        if (path == null || path.getEdges().isEmpty()) {
            System.out.println("No route found");
            return;
        }

        visualizeRoute(path);

        stationToStartRouteFrom.getShape().setFill(Color.WHITE);
        stationToStartRouteFrom = null;

        System.out.println("route completed");
        //resetRouteVisuals();
    }

    public void visualizeRoute(Path<Station> path) {
        if (path == null || path.getEdges().isEmpty())
            return;

        Station fromStation = path.getStart();
        Station toStation = path.getEnd();

        for (Node node : this.getChildren())
            if (node instanceof StationView stationView) {
                Station currentStation = stationView.getStation();

                if (currentStation.equals(fromStation) || currentStation.equals(toStation)) {
                    stationView.getShape().setFill(Color.BLUE);
                }
            }

        for (Node node : this.getChildren()) {
                if (node instanceof EdgeLine edgeLine) {

                    //RoutePlanner: Hämta vilka edges som utgör path
                    edgeLine.setStrokeWidth(edgeLine.getLineWidth() * 2);
                }
            }
        }

    public void resetRouteVisuals() {
        for (Node node : this.getChildren()) {
            if (node instanceof EdgeLine edgeLine)
                edgeLine.setStrokeWidth(edgeLine.getLineWidth());

            if (node instanceof StationView stationView)
                stationView.updateAppearance();
        }

        System.out.println("Route has been cleared");
    }

    public void setAlgorithm(PathFinder<Station> pathFinder) {
    routePlanner.setPathFinder(pathFinder);
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

            //RoutePlanner: Skicka att den ska lägga till nod istället
            Station newStation = new Station(stationName);

            routePlanner.addStation(newStation);
            addStationToMap(newStation, mouseClickX, mouseClickY);
            


            System.out.println("New station has been created: " + stationName);
        }
    }

    public void addStationToMap(Station station, double x, double y) {

        stations.add(station);

        StationView stationView = new StationView(station, x, y);
        this.getChildren().addAll(stationView.getLabel(), stationView);
    }

    public void setCurrentTransitLine(ComboBox<TransitLine> transitLineSelectionBox) {
        currentTransitLine = transitLineSelectionBox.getValue();
    }

    private int calculateTotalTime(Path<Station> path) {
    int totalTime = 0;

    for (Edge<Station> edge : path.getEdges()) {
        totalTime += edge.getWeight();
    }

    return totalTime;
    }

    //#endregion

    //#region Connect Objects

    public boolean getIsConnecting() {
        return isConnecting;
    }

    public void startConnectingStations() {
        this.isConnecting = true;
        this.firstSelectedStation = null;
        System.out.println("Connecting stations...Choose the first station");
    }

    public void chooseConnectingStations(StationView clickedStation) {
        if (firstSelectedStation == null) {
            firstSelectedStation = clickedStation;
            firstSelectedStation.getShape().setFill(Color.GREEN);
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

                //Skicka till modellen att den ska koppla två noder istället
                GraphEdge<Station> testEdge = new GraphEdge<>(clickedStation.getStation(), currentTransitLine.getName(), inputData.weight);

                System.out.println("Second station has been selected: " + clickedStation.getStation().getName());
                routePlanner.connectStations(firstSelectedStation.getStation(), clickedStation.getStation(), currentTransitLine.getName(), inputData.weight);
                connectStations(firstSelectedStation, clickedStation, testEdge);
            }
            
            firstSelectedStation.getShape().setFill(Color.WHITE);
            System.out.println("Stations " + firstSelectedStation.getStation().getName() + " and " + clickedStation.getStation().getName() + " have been connected");
            System.out.println("Exiting connect mode");
            isConnecting = false;
            firstSelectedStation = null;
        }
    }

    private void connectStations(StationView station1, StationView station2, GraphEdge<Station> edge) {

        EdgeLine edgeLine = new EdgeLine(
            station1,
            station2,
            edge,
            currentTransitLine.getColor()
        );

        station1.addLineColor(currentTransitLine.getColor());
        station2.addLineColor(currentTransitLine.getColor());

        this.getChildren().add(edgeLine);
        edgeLine.toBack();

        routePlanner.connectStations(
            station1.getStation(),
            station2.getStation(),
            edge.getName(),
            edge.getWeight()
        );
    }

    public RoutePlanner getRoutePlanner() {
    return routePlanner;
    }

    //#endregion

    //#region Remove Objects

    public boolean  getIsRemoving() {
        return isRemoving;
    }

    public void startRemovingObject() {
        isRemoving = true;
        stationToRemove = null;
        lineToRemove = null;
    }

    public void chooseStationToRemove(StationView clickedStation) {
        if (stationToRemove == null) {
            stationToRemove = clickedStation;
            removeStation();
            clickedStation.updateAppearance();
        }
    }

    public void chooseLineToRemove(EdgeLine clickedLine) {
        if (lineToRemove == null) {
            lineToRemove = clickedLine;
            removeLine(clickedLine);
        }
    }

    private void removeStation() {
        List<EdgeLine> linesToRemove = new ArrayList<>();
        for (Node node : this.getChildren()) {
            if (node instanceof EdgeLine edgeLine) {
                if (edgeLine.getFromStationView() == stationToRemove || edgeLine.getToStationView() == stationToRemove) {
                    routePlanner.removeStation(stationToRemove.getStation());
                    linesToRemove.add(edgeLine);
                }
            }
        }

        this.getChildren().removeAll(linesToRemove);
        this.getChildren().remove(stationToRemove);
        this.getChildren().remove(stationToRemove.getLabel());

        //RoutePlanner: Skicka att den ska ta bort stationToRemove.getStation();
        
        System.out.println(stationToRemove.getStation().getName() + " Has been removed");
        System.out.println("Exiting station remove mode");
        stationToRemove = null;
        isRemoving = false;
    }

    private void removeLine(EdgeLine clickedLine) {

        //RoutePlanner: Skicka att den ska ta bort edge, via disconnect

        clickedLine.getFromStationView().updateAppearance();
        clickedLine.getToStationView().updateAppearance();
        routePlanner.disconnectStations(clickedLine.getFromStationView().getStation(),clickedLine.getToStationView().getStation());

        this.getChildren().remove(clickedLine);
        System.out.println(clickedLine.getEdgeData().getName() + " has been removed");
        lineToRemove = null;
        isRemoving = false;
    }

    //#endregion

}
