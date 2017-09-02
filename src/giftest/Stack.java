
package giftest;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.ImageObserver;

/**
 *
 * @author Ryland
 */
public class Stack extends CardContainer {
    // how far should each card be offset from eachother?
    private final static int Y_OFFSET = 20;
    
    Stack(){
        super();
    }
    
    Stack(int x, int y, String id){
        super(x,y);
        this.id = id;
    }
    
    @Override
    void processCard(Card c){
        Point p = pos;
        // sets the cards in the right spots...
        for(int idx = 0; idx < cards.size(); idx++){
            if(c == cards.get(idx)){
                cards.get(idx).setPos(p.x, p.y + (idx*Y_OFFSET));
                cards.get(idx).setContainer(this);
                cards.get(idx).setStackHeight(idx);
            }
        }
    }
    
    // draws the cards based off of cards' draw method.
    @Override
    void drawCards(Graphics g, ImageObserver io) {
        for(Card c : cards){
            c.draw(g, io);
        }
    }
}
