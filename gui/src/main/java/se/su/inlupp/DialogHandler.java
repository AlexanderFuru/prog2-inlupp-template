package se.su.inlupp;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DialogHandler {
    public static class LineInputData {
        int weight;
        public LineInputData(int weight) {
            this.weight = weight;
        }
    }

    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Optional<TransitLine> showNewTransitLineDialog() {
        Dialog<TransitLine> dialog = new Dialog<>();
        dialog.setTitle("Create new subway line");
        dialog.setHeaderText("Input name and choose color");

        ButtonType okButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        ColorPicker colorPicker = new ColorPicker(javafx.scene.paint.Color.RED);
        grid.add(new Label("Line name: "), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Color: "), 0, 1);
        grid.add(colorPicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showErrorAlert("Invalid input", "Missing name", "Transit line must have a name");
                    return null;
                }
                return new TransitLine(name, colorPicker.getValue());
            }

            return null; 
        });

        return dialog.showAndWait();  
    }

    public static Optional<LineInputData> showConnectDialog(String fromName, String toName) {
        Dialog<LineInputData> dialog = new Dialog<>();
        dialog.setTitle("Create connection");
        dialog.setHeaderText("Connect " + fromName + " with " + toName);

        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField weightField = new TextField();
        weightField.setPromptText("Travel time:");

        grid.add(new Label("Travel time (minutes):"), 0, 1);
        grid.add(weightField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String weightString = weightField.getText().trim();

                try {
                    int weight = Integer.parseInt(weightString);
                    if (weight <= 0) {
                        showErrorAlert("Invalid input", "Invalid weight", "Travel time needs to be greater than 0");
                        return null;
                    }
                    return new LineInputData(weight);
                    
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid input", "Non-numerical weight", "Travel time needs to entered as an Integer");
                    return null;
                }
            }

            return null;
        });

        return dialog.showAndWait();
    }
}
