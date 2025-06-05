package org.example;

import java.util.List;
import java.util.Random;

public class Character {

    public int characterID;
    public int xCoord;
    public int yCoord;
    public int inventory;
    public int movementPoints;
    public Spice currentWantedSpice;

    public Character(int characterID, int xCoord, int yCoord, int movementPoints){
        this.characterID = characterID;
        this.xCoord=xCoord;
        this.yCoord=yCoord;
        this.inventory=0;
        this.movementPoints = movementPoints;
        this.currentWantedSpice = null;
    }

    /**
     * chooses random spice out of the lise, which will be collected by the character
     * @param spiceList
     */
    public void chooseRandomSpice(List<Spice> spiceList) {
        int min = 0;
        int max = spiceList.size()-1;

        Random random = new Random();

        int value = random.nextInt(max + min) + min;


        currentWantedSpice = spiceList.get(value);

        System.out.println("=================================================selected Spice: "+currentWantedSpice);
    }

    @Override
    public String toString(){
        return "{CharacterID: " + characterID+
                "(" +xCoord+
                ", " +yCoord+
                ")}";
    }
}
