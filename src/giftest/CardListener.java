
package giftest;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class CardListener extends MouseAdapter {

    private final Board b;
    private ArrayList<Card> manipulatedCards;
    private boolean dragging;
    private Point absHandle;
    private CardContainer lastCardContainer;
    private final CardGame theGame;

    CardListener(Board b, CardGame theGame) {
        // get reference of the board
        this.b = b;
        // get reference of the game being played...
        this.theGame = theGame;
        // initialize other things...
        manipulatedCards = new ArrayList<>();
        dragging = false;
    }

    ArrayList<Card> getManipulatedCards() {
        return manipulatedCards;
    }

    boolean isDragging() {
        return dragging;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        // if were holding a card....
        if (dragging) {
            // for each card were moving...
            for (int idx = 0; idx < manipulatedCards.size(); idx++) {
                // move them to our new mouse location with an offset
                // based on where we click the card...
                Point p = new Point(
                        me.getPoint().x - absHandle.x,
                        me.getPoint().y - absHandle.y + +20 * idx
                );
                // set its position to our current cursor pos + the offset
                // given by where we held the card.
                manipulatedCards.get(idx).setPos(p);
            }
            // repaint because we dont want to look like were running at 2 fps..
            b.forceRepaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        manipulatedCards = new ArrayList<>();
        // get the container we clicked on if we clicked on a container...
        CardContainer cc = getContainer(me.getPoint());
        // we didnt click on the container, do nothing...
        if (cc == null) {
            return;
        }
        // we clicked on the deck so dont allow a drag but interact instead.
        if (cc.getClass() == Deck.class) {
            theGame.interact(cc);
            return;
        }

        // if the container we clicked on is not empty...
        if (!cc.getStack().isEmpty()) {
            // get the card we clicked on.
            Card c = getCard(me.getPoint());
            // if its the shallow only allow the top card as our selected card.
            if (cc.getClass() == Shallow.class) {
                if (c != cc.getTopCard()) {
                    return;
                }
            }
            
            //get all cards under the selected card.
            for (int idx = c.getStackHeight(); idx < cc.getStack().size(); idx++) {
                manipulatedCards.add(cc.getStack().get(idx));
            }
            // see if we can move these cards based on the games logic...
            if (theGame.canManipulateStack(manipulatedCards)) {
                // if so start dragging...
                dragging = true;
                // record the last container it was in...
                lastCardContainer = manipulatedCards.get(0).getContainer();
                // and create a relative offset based on click...
                absHandle = new Point(
                        me.getPoint().x - manipulatedCards.get(0).getPos().x,
                        me.getPoint().y - manipulatedCards.get(0).getPos().y
                );
                // remove the cards from their' container.
                manipulatedCards.get(0).getContainer().removeCards(manipulatedCards);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        // if were dragging some cards...
        if (dragging && manipulatedCards != null) {
            // see if we dropped them into another container...
            CardContainer cc = approximateDrop(me);
            // if we did then see if were allowed to move the cards to that
            // container based on the games logic...
            if (theGame.canMoveCardTo(cc, manipulatedCards.get(0))) {
                // if we did then record the cards movement for the movement list.
                CardContainer prev_cc = manipulatedCards.get(0).getContainer();
                // move the card to the new container...
                cc.addCards(manipulatedCards);
                b.addMove(manipulatedCards, prev_cc, manipulatedCards.get(0).getContainer());
                // let the game do logic to determine if it needs to do anything.
                theGame.processMove();
            } else {
                // move the card to the last container it was in...
                lastCardContainer.addCards(manipulatedCards);
            }
            // clear the card list for later...
            manipulatedCards = new ArrayList<>();
            // clear the container for later..
            lastCardContainer = null;
            // tell the game were no longer holding cards...
            dragging = false;
        }
    }

    private CardContainer approximateDrop(MouseEvent me) {
        Point cCenter = manipulatedCards.get(0).getCenterPoint();
        //try to see if our mouse is hovering over a top card or an empty container...
        CardContainer best_match = getContainer(me.getPoint());
        if (best_match != null) {
            return best_match;

        }
        // try to see if the center of our card is hovering over the top of a
        // card or an empty container...
        best_match = getContainer(cCenter);
        if (best_match != null) {
            return best_match;
        }
        // return whatever container the cards were already in as a last effort
        // to find a container.
        return manipulatedCards.get(0).getContainer();
    }

    // looks for the container that contains the point p and returns the
    // container; returns null if no container was found.
    private CardContainer getContainer(Point p) {
        for (Stack s : b.getStacks()) {
            if ((s.containsPoint(p) && s.getStack().isEmpty()) || s.containsPoint(p)) {
                return (CardContainer) s;
            }
        }

        if ((b.getShallow().getTopCard() != null) && b.getShallow().containsPoint(p)) {
            return b.getShallow();
        }

        for (Card c_idx : b.getCards()) {
            if (c_idx.containsPoint(p)) {
                return c_idx.getContainer();
            }
        }

        return null;
    }

    // finds the card that intersects with point p that is the closest to the
    // top
    private Card getCard(Point p) {
        int height = -1;
        Card c = null;
        for (Card c_idx : b.getCards()) {
            if (c_idx.containsPoint(p) && (c_idx.getStackHeight() > height)) {
                c = c_idx;
            }
        }
        return c;
    }
}
