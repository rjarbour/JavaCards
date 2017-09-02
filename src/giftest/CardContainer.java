/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package giftest;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author Ryland
 */

// does this really need any comments? its all very readable...
public abstract class CardContainer {
    protected ArrayList<Card> cards;
    protected Point pos;
    protected int width;
    protected int height;
    protected String id;
    
    CardContainer(){
        cards = new ArrayList<>();
        pos = new Point(0,0);
        width = 32;
        height = 48;
    }
    
    CardContainer(int x, int y){
        cards = new ArrayList<>();
        pos = new Point(x,y);
        width = 32;
        height = 48;
    }
    
    public void setStack(ArrayList<Card> s){
        cards = s;
    }
    
    public void addCard(Card c){
        cards.add(c);
        processCard(c);
    }
    
    public boolean addCards(ArrayList<Card> cL){
        for(Card c : cL){
            if(cards.contains(c)){
                return false;
            } else {
                cards.add(c);
                processCard(c);
            }
        }
        return true;
    }
    
    public ArrayList<Card> getStack(){
        return cards;
    }
    
    public boolean removeCard(Card c){
        for(Card c_idx : cards){
            if(c_idx == c){
                cards.remove(c);
                processCard(null);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeCards(ArrayList<Card> cL){
        for(Card c : cL){
            if(!cards.remove(c)){return false;}
        }
        return true;
    }
    
    public Card getTopCard(){
        if(cards.isEmpty()){return null;}
        return cards.get(cards.size()-1);
    }
    
        
    String getID(){
        return id;
    }
    
    // check to see if the coordinate is inside the cards bounds...
    boolean containsPoint(Point p){
        Rectangle r = new Rectangle(this.pos, new Dimension(width, height));
        return r.contains(p);
    }
    
    // made for any specific behaviour the cardcontainers might want to do...
    abstract void processCard(Card c);
    // handles any funny ways to draw the card if they want to do anyting...
    abstract void drawCards(Graphics g, ImageObserver io);
}
