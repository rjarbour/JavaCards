
package giftest;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Deck extends CardContainer {
    // clubs, diamonds, hearts, spades.
    private final String suits = "cdhs";
    private final long seed;
    
    public Deck(long seed, String id){
        super();
        this.id = "deck";
        this.seed = seed;
        String str = "";
        // load a deck of cards!
        // values...
        for (int n = 1; n <= 13; n++) {
            // suits...
            for (int s = 0; s < 4; s++) {
                // basic parsing for assets...
                if (n <= 9) {
                    str = "assets/0" + n + suits.charAt(s) + ".gif";
                } else {
                    str = "assets/" + n + suits.charAt(s) + ".gif";
                }
                // get the image...
                URL loc = this.getClass().getResource(str);
                // load the image...
                ImageIcon iia = new ImageIcon(loc);
                // hold a reference of the image in the card.
                Card c = new Card(iia.getImage(), s, n, this);
                // add the card to the deck.
                cards.add(c);
            }
        }
    }
    
    public void shuffle(){
        // super mario 64 rng code!
        // I seriously cannot step through this code but I can assure you it is
        // sufficently random; unfortunately java doesnt support unsigned
        // variables so Ive had to improvise...
        for(short idx = 0; idx < cards.size(); idx++){
            short in = (short)(idx+seed);
            short s0 = (short) (in << 8);
            s0 = (short) (s0 ^ in);
            in = (short) (((s0 & 0xFF) << 8) | ((s0 & 0xFF00) >> 8));
            s0 = (short) ((s0 << 1) ^ in);
            short s1 = (short) ((s0 >> 1) ^ 0xFF80);
            if((s0 & 1) == 0){
                in = (short) (s1 ^ 0x1FF4);
            } else {
                in = (short) (s1 ^ 0x8180);
            }
            
            // java doesnt support unsigned... :(
            Card temp = cards.get(Math.abs(in%cards.size()));
            cards.set(Math.abs(in%cards.size()), cards.get(idx));
            cards.set(idx, temp);
        }
    }
    
    public Card drawCard(){
        Card c = this.getTopCard();
        cards.remove(c);
        processCard(null);
        return c;
    }
    
    public void addToBottom(Card c){
        cards.add(0, c);
        processCard(c);
    }
    
    public void dealCard(Stack p){
        p.addCard(cards.get(cards.size()-1));
        cards.remove(cards.size()-1);
        processCard(null);
    }
    
    public ArrayList<Card> getDeck(){
        return cards;
    }

    @Override
    public void drawCards(Graphics g, ImageObserver io) {
        for(Card c : cards){
            c.draw(g, io);
        }
    }

    @Override
    public void processCard(Card c) {
        if(c != null){
            c.setPos(this.pos);
            c.setRevealed(false);
            c.setContainer(this);
        }
        getTopCard().setRevealed(false);
    }
}
