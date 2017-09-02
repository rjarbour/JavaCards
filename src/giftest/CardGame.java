/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package giftest;

import java.util.ArrayList;

/**
 *
 * @author Ryland
 */
public interface CardGame {

    // can a card move to this container?
    public boolean canMoveCardTo(CardContainer cc, Card c);
    // can a stack of cards be changed?
    public boolean canManipulateStack(ArrayList<Card> c);
    // do logic after a move is done...
    public void processMove();
    // do something when a card container is clicked?
    public void interact(CardContainer cL);
}
