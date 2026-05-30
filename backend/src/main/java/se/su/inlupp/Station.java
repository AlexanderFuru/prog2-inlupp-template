package se.su.inlupp;

public class Station {

    private String name;
    private double x;
    private double y;

    public Station (String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName(){
        return this.name;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }


    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Station station){
            return name.equals(station.name);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }
    
    public String toString(){
        return name + ", " + x + ", " + y;
    }
}
