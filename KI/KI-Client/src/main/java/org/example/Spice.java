package org.example;

public class Spice {
    public int xCoord;
    public int yCoord;


    public Spice(int xCoord, int yCoord){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    @Override
    public String toString(){
        return "{Spice: ("+xCoord+", "+yCoord+") }";
    }
}
