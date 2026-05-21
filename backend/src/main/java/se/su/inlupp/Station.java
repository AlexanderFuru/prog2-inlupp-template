package se.su.inlupp;

public class Station {

    private String name;

    public Station (String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
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
        return name;
    }
}
