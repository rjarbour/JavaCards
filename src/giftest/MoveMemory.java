package giftest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoveMemory {
    
    // reference to the board...
    private Board b;
    // an array of moves to keep track of the moves made...
    private ArrayList<Move> moveHistory;
    // the seed of the current game.
    private final long seed;

    // creates fresh move history.
    public MoveMemory(Board b, long seed) {
        moveHistory = new ArrayList<>();
        this.b = b;
        this.seed = seed;
    }

    // loads move history from a file and continues from there...
    public MoveMemory(Board b, long seed, String saveFile) {
        moveHistory = new ArrayList<>();
        this.b = b;
        this.seed = seed;
        try {
            // try to load the file!
            loadSave(saveFile);
        } catch (IOException ex) {
            // woops something went wrong 50ms after initial check...
            Logger.getLogger(MoveMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addMove(Move m) {
        moveHistory.add(m);
    }

    public void Undo() {
        // continue until the move is not a carry move...
        while (true) {
            // is there a move to undo?
            if (moveHistory.isEmpty()) {
                // no; were done here.
                break;
            }
            // yes; lets get the most recent move...
            Move m = moveHistory.get(moveHistory.size() - 1);
            // is the move a compound move?
            boolean carryNext = m.getCarry();
            // if we somehow got a null move.... end here; honestly cant
            // remember why this is here but Im pretty certain its here for a
            // reason...
            if (m == null) {
                break;
            }
            // get a shallow reference of the card that was last moved...
            Card c_ref = Card.deSerialize(m.getSeralizedCards().get(0));
            // for each card on the board...
            for (Card c : b.getCards()) {
                // if the shallow card matches the real cards values; use the
                // matching card.
                if (c.getValue() == c_ref.getValue() && c.getSuit() == c_ref.getSuit()) {
                    ArrayList<Card> cl = new ArrayList<>();
                    ArrayList<Card> cs = c.getContainer().getStack();
                    // chances are if the card is up the stack, then they were 
                    // all moved together; so take all of the cards under the
                    // the given card.
                    for (int idx = cs.indexOf(c); idx < cs.size(); idx++) {
                        cl.add(cs.get(idx));
                    }
                    // if the container we're moving the cards back to is not 
                    // empty then set the card's face to not be revealed.
                    if (!b.getContainer(m.getPreviousCardContainer()).getStack().isEmpty()) {
                        b.getContainer(m.getPreviousCardContainer()).getTopCard().setRevealed(false);
                    }
                    // move cards back to their original container...
                    b.getContainer(m.getPreviousCardContainer()).addCards(cl);
                    // and remove them from their current container...
                    b.getContainer(m.getCurrentCardContainer()).removeCards(cl);
                    // undo history! remove the history that the move ever happened.
                    moveHistory.remove(m);

                }
            }
            // did it mention to carry to the next move?
            if (!carryNext) {
                // no; return.
                break;
            }
        }
    }

    // sets a previous move to be a compound move just in case we need it...
    public void setLastCarry(boolean t) {
        if (moveHistory.size() > 0) {
            moveHistory.get(0).setCarry(t);
        }
    }

    public int getSize() {
        return moveHistory.size();
    }

    public void saveGame() throws IOException {
        // create a file with the seed of this game...
        File file = new File("solitaire" + seed + ".txt");
        file.createNewFile();
        
        // write the seed of the game, followed by all the moves serialized...
        FileWriter writer = new FileWriter(file);
        writer.write(seed + "\n");
        writer.write(b.time + "\n");
        for (Move m : moveHistory) {
            writer.write(m.toString() + "\n");
        }
        
        // clean up...
        writer.flush();
        writer.close();
    }

    private void loadSave(String saveFile) throws FileNotFoundException, IOException {
        // try to open the file
        BufferedReader r = new BufferedReader(new FileReader(saveFile));
        // skip the seed of the game, we already have it.
        r.readLine();
        // set the time back to what it was...
        b.time = Long.parseLong(r.readLine());
        // loop until we have no more lines from the file.
        while (true) {
            String s = r.readLine();
            // if there are no more lines then stop loading.
            if (s == null) {
                break;
            }
            // cut off some meta characters from the data..
            s = s.substring(1, s.length() - 1);
            // split the cards from the containers...
            String ss[] = s.split("\\*");
            // get a list of the shallow references of cards.
            String[] string_list = ss[0].split(",");
            ArrayList<Card> card_list = new ArrayList<>();
            // only one card?
            if(string_list == null) {
                // hold the shallow references to help find real cards soon.
                card_list.add(Card.deSerialize(ss[0]));
            } else {
                // many cards; iterate through them...
                for(int idx = 0; idx < string_list.length; idx++){
                    // hold the shallow references to help find real cards soon.
                    card_list.add(Card.deSerialize(string_list[idx]));
                }
            }
            // go through our cards...
            for (Card c_ref : card_list) {
                // go through the real cards...
                for (Card c : b.getCards()) {
                    // find the matching real cards to our fake cards...
                    if (c.getValue() == c_ref.getValue() && c.getSuit() == c_ref.getSuit()) {
                        // and move the real cards to the corrisponding containers
                        // that the move specified....
                        c.getContainer().removeCard(c);
                        // work around for moving cards  to the deck instead of 
                        // the shallow...
                        String c_name = (ss[1].split(","))[1].equals("shallow") ? "deck" : (ss[1].split(","))[1];
                        // move cards to the container.
                        b.getContainerByID(c_name).addCard(c);
                        // card has been moved; its probably revealed...
                        c.setRevealed(true);
                       
                    }
                }
            }
            
            // re-serialize our cards and recreate the move history for the game.
            ArrayList<String> card_serial = new ArrayList<>();
            for(Card c : card_list){
                card_serial.add(c.getSerialized());
            }
            CardContainer prev = b.getContainerByID((ss[1].split(","))[0]);
            CardContainer cur = b.getContainerByID((ss[1].split(","))[1]);
            moveHistory.add(new Move(card_serial, prev, cur));
        }
    }
}
