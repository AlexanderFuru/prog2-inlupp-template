package se.su.inlupp;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class FileHandler {

    public void saveData(RoutePlanner planner, String imagePath, File fileName){


        try{
        
         FileWriter fileWriter = new FileWriter(fileName);
         PrintWriter printWriter = new PrintWriter(fileWriter);

         Graph<Station> graph = planner.getGraph(); 

         for(Station station: graph.getNodes()){
           
            printWriter.println("NODE;" + station.getName() + ";" + station.getX() + ";" + station.getY());
            
         } 
         
         for(Station station : graph.getNodes()){
    
            for(Edge<Station> edge : graph.getEdgesFrom(station)){

                Station from = station;
                Station to = edge.getDestination();

                if(from.getName().compareTo(to.getName()) < 0){
                printWriter.println("EDGE;" + from.getName() + ";" + to.getName() + ";" + edge.getName() + ";" + edge.getWeight());    
                }
        
                
            } 
        } 

        if (imagePath != null){
        printWriter.println("IMAGE;" + imagePath);
        }

        printWriter.close();
        fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
      

    }


    public String loadData(RoutePlanner planner, File fileName){
        
        String imagePath = null;
        
        try{
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            Map<String, Station> stations = new HashMap<>(); 
            

            while((line = reader.readLine()) != null){

                String[] parts = line.split(";");
                
                if (parts[0].equals("NODE")){

                    Station s = new Station(parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
                    planner.addStation(s);
                    stations.put(s.getName(), s);

                } else if(parts[0].equals("EDGE")){
                    
                    Station from = stations.get(parts[1]);
                    Station to = stations.get(parts[2]);
                    planner.connectStations(from, to, parts[3], Integer.parseInt(parts[4]));

                } else if (parts[0].equals("IMAGE")){
                    imagePath = parts[1];  
                }
               

            }

        fileReader.close();
        reader.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        return imagePath;
    }

   
}
