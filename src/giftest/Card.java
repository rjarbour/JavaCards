
package giftest;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;


// card is very forward; doesnt require too much commenting because the names
// of the methods and variables are pretty obvious.
public class Card {
    private final int SUIT;
    private final int VALUE;
    // point in space.
    private Point p;
    // dimensions of the cards
    private int width;
    private int height;
    private boolean revealed;
    // hight with 0 being the bottom and n being the top. 0 <= n < highest int
    private int stackHeight; 
    private CardContainer container;
    // card face to draw.
    public Image i;
    // card back; static because call cards share one instance of the image.
    public static Image back;
    
    // DONT USE THIS!!
    Card(int suit, int value){
        this.SUIT = suit;
        this.VALUE = value;
    }
    
    Card(Image i, int suit, int value){
        this.i = i;
        this.SUIT = suit;
        this.VALUE = value;
        this.revealed = true;
        this.stackHeight = 0;
        this.container = null;
        p = new Point(0,0);
        width = 32;
        height = 48;
    }
    
    Card(Image i, int suit, int value, CardContainer container){
        this.i = i;
        this.SUIT = suit;
        this.VALUE = value;
        this.revealed = false;
        this.stackHeight = 0;
        this.container = container;
        p = new Point(0,0);
        width = 32;
        height = 48;
    }
    
    Image getImage(){
        return revealed ? i : back;
    }
    
    void setPos(int x, int y){
        p = new Point(x, y);
    }
    
    void setPos(Point pos){
        p = pos;
    }
    
    void setContainer(CardContainer cc){
        this.container = cc;
    }
    
    Point getPos(){
        return p;
    }
    
    CardContainer getContainer(){
        return container;
    }
    
    Point getCenterPoint(){
        return new Point(p.x+(width/2), p.y+(height/2));
    }
    
    int getValue(){
        return VALUE;
    }
    
    int getSuit(){
        return SUIT;
    }
    
    // returns the card in a form that can be saved and loaded from file.
    String getSerialized(){
        String derp = "";
        derp += this.VALUE > 9 ? this.VALUE : "0"+this.VALUE;
        derp += "0"+this.SUIT;

        return derp;
    }
    
    void setRevealed(boolean b){
        revealed = b;
    }
    
    void setStackHeight(int d){
        stackHeight = d;
    }
    
    int getStackHeight(){
        return stackHeight;
    }
    
    int x(){
       return p.x;
    }
    
    int y(){
        return p.y;
    }
    
    boolean isRevealed(){
        return revealed;
    }
    
    // draws the front or back of the card based on revealed
    void draw(Graphics g, ImageObserver io){
        g.drawImage((revealed ? i : back), p.x, p.y, io);
    }
    
    // returns true if the point is inside the bounds of the card.
    boolean containsPoint(Point p){
        Rectangle r = new Rectangle(this.p, new Dimension(width, height));
        return r.contains(p);
    }
    
    // takes a serialized card and turns it into a card instance with no .
    static Card deSerialize(String id){
        String derp = id+"";

        int val = Integer.parseInt(derp.substring(2, 4));
        int suit = Integer.parseInt(derp.substring(0, 2));
        return new Card(val, suit);
    }
}
