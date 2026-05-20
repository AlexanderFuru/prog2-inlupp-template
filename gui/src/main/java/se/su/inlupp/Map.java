package se.su.inlupp;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Map extends Pane {
    private final ImageView backgroundImageView;

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
}
