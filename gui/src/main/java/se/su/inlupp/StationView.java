package se.su.inlupp;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StationView extends Group{
    private final Station station;
    private final Text label;

    private final Set<Color> activeLineColors = new HashSet<>();

    private Shape visualShape;

    private final javafx.beans.property.DoubleProperty centerX = new javafx.beans.property.SimpleDoubleProperty();
    private final javafx.beans.property.DoubleProperty centerY = new javafx.beans.property.SimpleDoubleProperty();
    
    public StationView(Station station, double x, double y) {
        this.station = station;
        this.centerX.set(x);
        this.centerY.set(y);

        this.label = new Text(station.getName());
        this.label.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        updateAppearance();
        this.getChildren().add(label);

        this.setOnMouseClicked(e -> {
            if (this.getParent() instanceof Map map && map.getIsConnecting()) {
                map.chooseConnectingStations(this);

                e.consume();
            }

            if (this.getParent() instanceof Map map && map.getIsRemoving()) {
                map.chooseStationToRemove(this);

                e.consume();
            }

            if (this.getParent() instanceof Map map && map.getIsChoosingRoute()) {
                map.chooseRouteEndPoints(this);

                e.consume();
            }
        });

        dragAndDrop();
    }

    public javafx.beans.property.DoubleProperty centerXProperty() {
        return centerX;
    }

    public javafx.beans.property.DoubleProperty centerYProperty() {
        return centerY;
    }

    public Station getStation() {
        return station;
    }

    public Text getLabel() {
        return label;
    }

    public Shape getShape() {
        return visualShape;
    }

    public void addLineColor(Color color) {
        activeLineColors.add(color);
        updateAppearance();
    }

    private void dragAndDrop() {
        this.setOnMouseDragged(e -> {
            centerX.set(e.getX());
            centerY.set(e.getY());
            updateAppearance();
        });
    }

    public void updateAppearance() {
        if (visualShape != null) {
            this.getChildren().remove(visualShape);
        }

        //TO-DO: Fixa så att den faktiskt kan känna av hur många olika linjer den är kopplad till
        int numberOfLines = activeLineColors.size();

        if (numberOfLines <= 1) {
            Circle circle = new Circle(centerX.get(), centerY.get(), 16);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(3);

            visualShape = circle;
        }
        else {
            double width = 30 + (numberOfLines * 15);
            double height = 32;

            Rectangle rectangle = new Rectangle(centerX.get() - width/2, centerY.get() - height/2, width, height);
            rectangle.setArcWidth(12);
            rectangle.setArcHeight(12);
            rectangle.setFill(Color.WHITE);
            rectangle.setStroke(Color.GREY);
            rectangle.setStrokeWidth(3);

            visualShape = rectangle;
        }

        this.getChildren().add(0, visualShape);

        label.setX(centerX.get() - (label.getLayoutBounds().getWidth() / 2));
        label.setY(centerY.get() - 25);
    }
}
