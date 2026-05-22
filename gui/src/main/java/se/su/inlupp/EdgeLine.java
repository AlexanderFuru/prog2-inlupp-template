package se.su.inlupp;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeLine extends Line {
    
    private final GraphEdge<Station> edgeData;
    private final StationView fromStationView;
    private final StationView toStationView;

    private final int lineWidth = 4;

    public EdgeLine(StationView fromStationView, StationView toStationView, GraphEdge<Station> edgeData) {
        this.fromStationView = fromStationView;
        this.toStationView = toStationView;
        this.edgeData = edgeData;

        this.setStroke(Color.RED);
        this.setStrokeWidth(lineWidth);

        this.startXProperty().bind(fromStationView.centerXProperty());
        this.startYProperty().bind(fromStationView.centerYProperty());
        this.endXProperty().bind(toStationView.centerXProperty());
        this.endYProperty().bind(toStationView.centerYProperty());

        this.toBack();
    }

    public GraphEdge<Station> getEdgeData() {
        return edgeData;
    }

    public StationView getFromStationView() {
        return fromStationView;
    }

    public StationView getToStationView() {
        return toStationView;
    }
}
