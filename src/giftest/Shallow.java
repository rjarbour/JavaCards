
package giftest;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.ImageObserver;

public class Shallow extends CardContainer {
    // how much to offset each card from eachother.
    private final static int X_OFFSET = 10;
    
    Shallow(){
        super();
    }
    
    Shallow(int x, int y, String id){
        super(x,y);
        this.id = id;
    }
    
    // moves the cards by the offset...
    @Override
    void processCard(Card c) {
        Point p = pos;
        for(int idx = 0; idx < cards.size(); idx++){
            cards.get(idx).setRevealed(true);
            cards.get(idx).setPos(p.x+(idx*X_OFFSET), p.y);
            cards.get(idx).setStackHeight(idx);
        }
        if(c != null){
            c.setContainer(this);
        }
    }

    @Override
    void drawCards(Graphics g, ImageObserver io) {
        for(Card c : cards){
            c.draw(g, io);
        }
    }
}
