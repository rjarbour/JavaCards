
package giftest;

import java.awt.Graphics;
import java.awt.image.ImageObserver;


public class SortedStack extends CardContainer {
    
    public SortedStack(){
        super();
    }
    
    public SortedStack(int x, int y, String id){
        super(x,y);
        this.id = id;
    }

    @Override
    void processCard(Card c) {
        if(c != null){
            // set all cards to be in the container and revealed.
            for(int idx = 0; idx < cards.size(); idx++){
                cards.get(idx).setPos(this.pos);
                cards.get(idx).setContainer(this);
                cards.get(idx).setStackHeight(idx);
                cards.get(idx).setRevealed(true);
            }
        }
    }

    @Override
    void drawCards(Graphics g, ImageObserver io) {
        getTopCard().draw(g, io);
    }
    
}
