package se.su.inlupp;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StationView extends Circle{
    private final Station station;
    private final Text label;

    private double mouseStartPositionX;
    private double mouseStartPositionY;
    
    public StationView(Station station, double x, double y) {
        super(x, y, 16);
        this.station = station;

        this.setFill(Color.CORNFLOWERBLUE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);

        this.label = new Text(station.getName());
        this.label.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        updateLabelPosition();

        this.setOnMouseClicked(e -> {
            if (this.getParent() instanceof Map map && map.isConnecting) {
                map.chooseConnectingStations(this);

                e.consume();
            }

        });

        dragAndDrop();
    }

    public Station getStation() {
        return station;
    }

    public Text getLabel() {
        return label;
    }

    public void updateLabelPosition() {
        this.label.setX(this.getCenterX() - (this.label.getLayoutBounds().getWidth() / 2));
        this.label.setY(this.getCenterY() - 18);
    }

    private void dragAndDrop() {
        this.setOnMousePressed(event -> {
            mouseStartPositionX = event.getX() - this.getCenterX();
            mouseStartPositionY = event.getY() - this.getCenterY();

            this.setFill(Color.YELLOW);
        });

        this.setOnMouseDragged(event -> {
            double newX = event.getX() - mouseStartPositionX;
            double newY = event.getY() - mouseStartPositionY;

            this.setCenterX(newX);
            this.setCenterY(newY);

            updateLabelPosition();
        });

        this.setOnMouseReleased(event -> {
            this.setFill(Color.WHITE);
        });
    }
}
