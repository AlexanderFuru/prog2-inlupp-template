package se.su.inlupp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        isChoosingRoute = false;
        System.out.println("Route canceled");
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

private void resetSelections() {
    isChoosingRoute = false;
    isConnecting = false;
    isRemoving = false;

    if (firstSelectedStation != null)
        firstSelectedStation.updateAppearance();

    if (stationToStartRouteFrom != null)
        stationToStartRouteFrom.updateAppearance();

    firstSelectedStation = null;
    stationToStartRouteFrom = null;
    stationToRemove = null;
    lineToRemove = null;
}

private boolean checkIfStationsOnMap() {
    for (Node node : getChildren()) {
        if (node instanceof StationView stationView) {
            if (stationView != null) {
                return true;
            }
        }
    }
    return false;
}

private boolean checkIfEnoughStationsOnMap() {
    if (stations.size() > 1)
        return true;
    
    else
        return false;
}

    //#endregion

    //#region Route

    public boolean getIsChoosingRoute() {
        return isChoosingRoute;
    }

    public void startChoosingRouteEndPoints() {
        if (!checkIfEnoughStationsOnMap()) {
            DialogHandler.showErrorAlert("Error", "No stations present on map", "Please create at least two stations before attempting to find route");
        }
        else {
            resetSelections();
            isChoosingRoute = true;
            System.out.println("Calculating route...Choose start station");
        }       
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

        if (path == null || path.getEdges().isEmpty()) {
            DialogHandler.showErrorAlert("Error", "Invalid route", "The chosen stations are not connected");
            resetSelections();
            return;
        }

        int totalTime = calculateTotalTime(path);

        System.out.println("FROM: " + from.getName());
        System.out.println("TO: " + to.getName());
        System.out.println("Total travel time: " + totalTime);

        if (path == null || path.getEdges().isEmpty()) {
            System.out.println("No route found");
            return;
        }

        visualizeRoute(path);

        stationToStartRouteFrom = null;

        System.out.println("route completed");
        //resetRouteVisuals();
    }

    public void visualizeRoute(Path<Station> path) {
        if (path == null || path.getEdges().isEmpty()) {
            return;
        }

        Station fromStation = path.getStart();
        Station toStation = path.getEnd();

        for (Node node : this.getChildren()) {
            if (node instanceof StationView stationView) {
                Station currentStation = stationView.getStation();

                if (currentStation.equals(fromStation) || currentStation.equals(toStation)) {
                    stationView.getShape().setFill(Color.BLUE);
                }
            }
        }
        List<Edge<Station>> routeEdges = path.getEdges();
        Station current = path.getStart();

        for (Edge<Station> edge : routeEdges) {
            Station destination = edge.getDestination();

            for (Node node : this.getChildren()) {
                if (node instanceof EdgeLine edgeLine) {

                    Station lineFrom = edgeLine.getFromStationView().getStation();

                    Station lineTo = edgeLine.getToStationView().getStation();

                    boolean matches = (lineFrom.equals(current) && lineTo.equals(destination)) || (lineTo.equals(current) && lineFrom.equals(destination));

                    if (matches){
                        edgeLine.setStrokeWidth(edgeLine.getLineWidth() * 2);
                    }
                }
            }
            current = destination;
        }

        Alert ruttinfo = new Alert(Alert.AlertType.INFORMATION);
        ruttinfo.setTitle("Route information:");
        ruttinfo.setHeaderText("Route found!");
        ruttinfo.setContentText("Total time: " + calculateTotalTime(path)+ "minutes" + "\n Amount of stops: " + (routeEdges.size()));
        ruttinfo.showAndWait();

        resetSelections();
    }

    public void resetRouteVisuals() {
        for (Node node : this.getChildren()) {
            if (node instanceof EdgeLine edgeLine)
                edgeLine.setStrokeWidth(edgeLine.getLineWidth());

            if (node instanceof StationView stationView)
                stationView.updateAppearance();
        }

        resetSelections();
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

            for (Node node : this.getChildren()) {
                if (node instanceof StationView stationView) {
                    if (Objects.equals(stationName, stationView.getStation().getName())) {
                        DialogHandler.showErrorAlert("Error", "Name already exists", "Please enter a new name");
                        handleNewStation(mouseClickX, mouseClickY);
                        return;
                    }
                }               
            }

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

    private boolean isConnected(StationView station1, StationView station2)
    {
        for (Node node : this.getChildren()) {
            if (node instanceof EdgeLine edgeLine) {
                StationView fromStation = edgeLine.getFromStationView();
                StationView toStation = edgeLine.getToStationView();

                if ((fromStation == station1 && toStation == station2) || (fromStation == station2 && toStation == station1))
                    return true;
            }
        }
        return false;
    }

    public void startConnectingStations() {
        if (!checkIfEnoughStationsOnMap()) {
            DialogHandler.showErrorAlert("Error", "No stations present on map", "Please create at least two stations before attempting to connect them");
        }
        else {
            resetSelections();
            isConnecting = true;
            System.out.println("Connecting stations...Choose the first station");
        }
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
                resetSelections();
                firstSelectedStation.updateAppearance();
                return;
            }

            if (isConnected(firstSelectedStation, clickedStation)) {
                DialogHandler.showErrorAlert("Error", "Invalid connection", "Stations already have connections");
                resetSelections();
                firstSelectedStation.updateAppearance();
                return;
            }
                
            Optional<DialogHandler.LineInputData> input = DialogHandler.showConnectDialog(firstSelectedStation.getStation().getName(), clickedStation.getStation().getName());
            if (input.isPresent()) {
                DialogHandler.LineInputData inputData = input.get();

                System.out.println("Second station has been selected: " + clickedStation.getStation().getName());
                connectStations(firstSelectedStation, clickedStation, inputData.weight);
            }
            
            firstSelectedStation.getShape().setFill(Color.WHITE);
            System.out.println("Stations " + firstSelectedStation.getStation().getName() + " and " + clickedStation.getStation().getName() + " have been connected");
            System.out.println("Exiting connect mode");

            resetSelections();
        }
    }

    private void connectStations(StationView station1, StationView station2, int inputData) {

        EdgeLine edgeLine = new EdgeLine(
            station1,
            station2,
            currentTransitLine.getColor()
        );

        station1.addLineColor(currentTransitLine.getColor());
        station2.addLineColor(currentTransitLine.getColor());

        this.getChildren().add(edgeLine);
        edgeLine.toBack();

        routePlanner.connectStations(
            station1.getStation(),
            station2.getStation(),
            currentTransitLine.getName(),
            inputData
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
        if (!checkIfStationsOnMap()) {
            DialogHandler.showErrorAlert("Error", "No objects present on map", "Please create at least one objects before attampting to remove them");
        }
        else {
            resetSelections();
        isRemoving = true;
        }
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
                    linesToRemove.add(edgeLine);
                }
            }
        }

        for (EdgeLine edgeLine : linesToRemove) {
            Color lineColor = (Color) edgeLine.getStroke();

            StationView adjecentStation = (edgeLine.getFromStationView() == stationToRemove)
            ? edgeLine.getToStationView()
            : edgeLine.getFromStationView();

            adjecentStation.removeLineColor(lineColor);
        }

        this.getChildren().removeAll(linesToRemove);
        this.getChildren().remove(stationToRemove);
        this.getChildren().remove(stationToRemove.getLabel());

        routePlanner.removeStation(stationToRemove.getStation());
        
        System.out.println(stationToRemove.getStation().getName() + " Has been removed");
        System.out.println("Exiting station remove mode");

        resetSelections();
    }

    private void removeLine(EdgeLine clickedLine) {
        Color color = (Color) clickedLine.getStroke();
        clickedLine.getFromStationView().removeLineColor(color);
        clickedLine.getToStationView().removeLineColor(color);
        clickedLine.getFromStationView().updateAppearance();
        clickedLine.getToStationView().updateAppearance();

        routePlanner.disconnectStations(clickedLine.getFromStationView().getStation(),clickedLine.getToStationView().getStation());

        this.getChildren().remove(clickedLine);
        System.out.println(clickedLine + " has been removed");
        
        resetSelections();
    }

    //#endregion
}