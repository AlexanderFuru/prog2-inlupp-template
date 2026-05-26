package se.su.inlupp;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeLine extends Line {
    
    private final StationView fromStationView;
    private final StationView toStationView;

    private final int lineWidth = 8;

    public EdgeLine(StationView fromStationView, StationView toStationView, Color color) {
        this.fromStationView = fromStationView;
        this.toStationView = toStationView;
        // this.edgeData = edgeData;

        this.setStroke(color);
        this.setStrokeWidth(lineWidth);

        this.startXProperty().bind(fromStationView.centerXProperty());
        this.startYProperty().bind(fromStationView.centerYProperty());
        this.endXProperty().bind(toStationView.centerXProperty());
        this.endYProperty().bind(toStationView.centerYProperty());

        this.toBack();

        this.setOnMouseClicked(e -> {
            if (this.getParent() instanceof Map map && map.getIsRemoving()) {
                map.chooseLineToRemove(this);

                e.consume();
            }
        });
    }

    public int getLineWidth() {
        return lineWidth;
    }

    // public GraphEdge<Station> getEdgeData() {
    //     return edgeData;
    // }

    public StationView getFromStationView() {
        return fromStationView;
    }

    public StationView getToStationView() {
        return toStationView;
    }
}
