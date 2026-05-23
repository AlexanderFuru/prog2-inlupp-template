package se.su.inlupp;

import javafx.scene.paint.Color;

public class TransitLine {
    private final String name;
    private final Color color;

    public TransitLine(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
