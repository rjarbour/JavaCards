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
import javax.swing.JOptionPane;

/**
 *
 * @author Ryland
 */
public class Solitaire implements CardGame {

    private final Board b;

    public Solitaire(Board b) {
        // hold a reference of the board...
        this.b = b;

        // set all of the tops of the stacks as revealed...
        for (CardContainer p : this.b.getStacks()) {
            if (p.getClass() == Stack.class) {
                p.getTopCard().setRevealed(true);
            }
        }

    }

    @Override
    public boolean canMoveCardTo(CardContainer cc, Card c) {
        // get the top card of the container the card is moving to...
        Card c_top = cc.getTopCard();
        // if the stack is a sorted stack; dont let the player move it there;
        // the computer should do that.
        if (cc.getClass() == SortedStack.class) {
            return false;
        }
        // if the container were moving to is empty, return false...
        if (c_top == null) {
            return true;
        }
        // is our card 1 value lower than the top card of the pile?
        boolean decending_value = c.getValue() == c_top.getValue() - 1;
        int cts = c_top.getSuit();
        int cs = c.getSuit();
        // is our suit a different color than the other 
        boolean different_suit = (cs == 0 || cs == 3) ? (cts == 1 || cts == 2) : (cts == 0 || cts == 3);
        return decending_value && different_suit;
    }

    @Override
    public boolean canManipulateStack(ArrayList<Card> cL) {
        // get the top cards we want to move...
        Card c = cL.get(0);
        // if the top card is in the deck
        if (c.getContainer().getClass() == Deck.class) {
            return false;
        }
        // otherwise if our card is revealed and the cards are in the right order...
        if (c.isRevealed()) {
            for (int idx = 0; idx < cL.size() - 1; idx++) {
                if (cL.get(idx).getValue() <= cL.get(idx + 1).getValue()) {
                    return false;
                }
            }
            // the card was not revealed...
        } else {
            return false;
        }
        // the card is revealed and all the cards are in the right order.
        return true;
    }

    @Override
    public void processMove() {
        ArrayList<SortedStack> ssl = b.getSortedStacks();

        while (true) {
            // toggle to see if we should cycle through again.
            boolean toggle = true;
            // check to see if we win??
            boolean win = true;
            // look througth all of the stacks on the board.
            for (Stack s : b.getStacks()) {
                // get the top card
                Card c = s.getTopCard();
                // if there is a top card...
                if (c != null) {
                    win = false;
                    int suitID = c.getSuit();
                    // look at the cards corrisponding sorted pile and see if 
                    // that pile is not empty
                    if (!ssl.get(suitID).getStack().isEmpty()) {
                        // if so is our card the next to go on the list
                        if (ssl.get(suitID).getTopCard().getValue() + 1 == c.getValue()) {
                            // then hold the previous container...
                            CardContainer prev_cc = c.getContainer();
                            // add the card to the sorted pile..
                            ssl.get(suitID).addCard(c);
                            // remove the card from its original pile
                            s.removeCard(c);
                            // then add the move to the move list.
                            b.addMove(c, prev_cc, c.getContainer(), true);
                            // tell the program to cycle through again to see
                            // if anything has changed...
                            toggle = false;
                        }
                    } // if there is no top card then is our card an ace?
                    else if (c.getValue() == 1) {
                        // make the last move we did combine with this move
                        // because this move is reflexive.
                        b.getMoveMemory().setLastCarry(true);
                        // hold the container...
                        CardContainer prev_cc = c.getContainer();
                        // add the card to the sorted pile
                        ssl.get(suitID).addCard(c);
                        // remove the card from the current container...
                        s.removeCard(c);
                        // add the move to the move list
                        b.addMove(c, prev_cc, c.getContainer(), true);
                        // cycle through the list again for changes...
                        toggle = false;
                    }
                }
            }
            // did we win?
            if (win) {
                try {
                    winGame();
                } catch (IOException ex) {
                    Logger.getLogger(Solitaire.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // true = dont cycle, false = cycle again.
            if (toggle) {
                break;
            }
        }

        //check to see if any cards need to be revealed...
        for (Stack s : b.getStacks()) {
            Card c = s.getTopCard();
            if (c != null) {
                if (!c.isRevealed()) {
                    c.setRevealed(true);
                }
            }
        }
    }

    @Override
    public void interact(CardContainer cL) {
        // check to see if the container is the deck...
        if (cL.getClass() == Deck.class) {
            // if so cycle the shallow...
            Deck deck = (Deck) cL;
            Shallow sh = b.getShallow();
            // move the shallow to the bottom of the deck...
            while (!sh.getStack().isEmpty()) {
                deck.addToBottom(sh.getStack().get(0));
                sh.removeCard(sh.getStack().get(0));
            }
            int upper = cL.getStack().size();
            int lower = upper - 3;
            // and move the top 3 cards of the deck to the shallow.
            for (; lower < upper; lower++) {

                sh.addCard(deck.drawCard());
            }
        }
    }

    public void winGame() throws FileNotFoundException, IOException {
        // get the players name...
        String playerName = JOptionPane.showInputDialog("Please input your name (no spaces or the game dies): ");
        // get the file...
        File f = new File("scores.txt");
        String[] scores;
        // does the file exist?
        if (!f.exists()) {
            // nope so make some dummy data...
            scores = new String[1];
            scores[0] = "NOBODY!!! 9999999";
        }
            // it does but is there anything in it?
            BufferedReader r = new BufferedReader(new FileReader("scores.txt"));
            String temp_s = r.readLine();
            // nope so make some dummy data...
            if (temp_s == null) {
                scores = new String[1];
                scores[0] = "NOBODY!!! 9999999";
            } else {
                // yep start parsing the data...
                int count = Integer.parseInt(temp_s);
                scores = new String[count];
                temp_s = r.readLine();
                int idx = 0;
                while (temp_s != null) {
                    scores[idx] = temp_s;
                    temp_s = r.readLine();
                    idx++;
                }
            }
        
        ArrayList<Long> scoreList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        // loop through the data to add them to the array lists...
        for (String s : scores) {
            scoreList.add(Long.parseLong((s.split(" "))[1]));
            nameList.add((s.split(" "))[0]);
        }
        
        // add the new player's score to the list.
        scoreList.add(b.time);
        nameList.add(playerName);
        
        // sort the scores from lowest to highest.
        sortScores(nameList, scoreList);
        // write the scores to the scores file.
        writeScoresFile(nameList, scoreList);
        // tell the board to end the game.
        b.endGame();
    }
    
    public void sortScores(ArrayList<String> nameList, ArrayList<Long> scoreList){
        // THIS IS A SORT.
        for (int i = 0; i < scoreList.size() - 1; i++) {
            int idx = i;
            for (int j = i + 1; j < scoreList.size(); j++) {
                if (scoreList.get(j) < scoreList.get(idx)) {
                    idx = j;
                }
            }
            long smallerNumber = scoreList.get(idx);
            String smallestName = nameList.get(idx);
            scoreList.set(idx, scoreList.get(i));
            scoreList.set(i, smallerNumber);
            nameList.set(idx, nameList.get(i));
            nameList.set(i, smallestName);
        }
    }
    
    public void writeScoresFile(ArrayList<String> names, ArrayList<Long> scores) throws IOException{
        File file = new File("scores.txt");
        file.createNewFile();
        
        // rewrite all of the scores to the score file as well as the number of scores in the file.
        FileWriter writer = new FileWriter(file);
        writer.write(names.size() + "\n");
        for (int idx = 0; idx < names.size(); idx++) {
            writer.write(names.get(idx)+" "+scores.get(idx)+"\n");
        }
        
        // clean up...
        writer.flush();
        writer.close();
    }
}
