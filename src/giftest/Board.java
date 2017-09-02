package giftest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private Image ii;
    private CardListener cL;
    private CardGame theGame;
    private Deck deck;
    private ArrayList<Stack> piles;
    private ArrayList<SortedStack> sortedPiles;
    private Shallow shallow;
    private MoveMemory mm;
    private JMenuBar menuBar_1;
    private Main main;
    
    // was going to implement this but not enough time.
    public Timer timer;
    public long time;
    //public boolean isStarted = false;
    //public boolean isPaused = false;

    
    // called if no seed is presented
    public Board(Main main) {
        // create frame reference...
        this.main = main;
        // create seed based off time...
        long seed = System.currentTimeMillis()%32767;
        // load in the games data and set the board up...
        loadData(seed);
        // load the drawing assests and other nonesense...
        init(seed, null);
        // tell the game to update every 0.4 ms...
        timer = new Timer(400, this);
        timer.start();
        // keep track of time....
        time = 0;
        // last minute changes before drawing....
        setBackground(Color.DARK_GRAY);
        // refresh twice as often...
        setDoubleBuffered(true);
        // obvious...
        setFocusable(true);
    }
    
    public Board(Main main, long seed, String saveFile){
        // create frame reference
        this.main = main;
        // load game data and recreate board state from save...
        loadData(seed);
        // load assests and over nonesense...
        init(seed, saveFile);
        timer = new Timer(400, this);
        timer.start();
        
        // last minute changes before drawing....
        setBackground(Color.DARK_GRAY);
        // refresh twice as often...
        setDoubleBuffered(true);
        // obvious...
        setFocusable(true);
    }
        
        // add a specific card movement to move history, boolean t says if we carry.
    public void addMove(Card c, CardContainer prev_cc, CardContainer cur_cc, boolean t) {
        this.mm.addMove(new Move(c.getSerialized(), prev_cc, cur_cc, t));
    }

        // add specific cards to move history, carry assumed false.
    public void addMove(ArrayList<Card> c, CardContainer prev_cc, CardContainer cur_cc) {
        ArrayList<String> cl = new ArrayList<>();
        for (Card cidx : c) {
            cl.add(cidx.getSerialized());
        }
        this.mm.addMove(new Move(cl, prev_cc, cur_cc));
    }
        
        // add specific cards to move history, boolean t says if we carry.
    public void addMove(ArrayList<Card> c, CardContainer prev_cc, CardContainer cur_cc, boolean t) {
        ArrayList<String> cl = new ArrayList<>();
        for (Card cidx : c) {
            cl.add(cidx.getSerialized());
        }
        this.mm.addMove(new Move(cl, prev_cc, cur_cc, t));
    }

    private void init(long seed, String saveFile) {
        d = new Dimension(400, 400);
        Card.back = new ImageIcon(this.getClass().getResource("assets/back01.gif")).getImage();
        
        //record or set the boards initial state...
        theGame = new Solitaire(this);
        if(saveFile != null){
            mm = new MoveMemory(this, seed, saveFile);
        } else {
            mm = new MoveMemory(this, seed);
        }
        theGame.processMove();
        
        menuBar_1 = new JMenuBar();
        //menuBar_1.setBounds(0, 0, 441, 21);
        this.add(menuBar_1);
        
        cL = new CardListener(this, theGame);
        this.addMouseMotionListener(cL);
        this.addMouseListener(cL);
        
        // messy keylistener; didnt deserve its own class...
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {}
            @Override
            public void keyReleased(KeyEvent ke) {}
            @Override
            public void keyPressed(KeyEvent ke) {
                System.out.println("key");
                if(ke.isControlDown()){
                    System.out.println("ctrl");
                    if(ke.getKeyCode() == KeyEvent.VK_Z){
                        System.out.println("undo!");
                        mm.Undo();
                        theGame.processMove();
                    }
                   if(ke.getKeyCode() == KeyEvent.VK_S){
                        try {
                            mm.saveGame();
                        } catch (IOException ex) {
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }
    
    public CardContainer getContainerByID(String id){
        for (Stack s : piles) {
            if(s.getID() == null ? id == null : s.getID().equals(id)){
                return s;
            }
        }

        if(deck.getID() == null ? id == null : deck.getID().equals(id)){
            return deck;
        }

        if(shallow.getID() == null ? id == null : shallow.getID().equals(id)){
            return shallow;
        }

        for (SortedStack ssl : sortedPiles) {
            if(ssl.getID() == null ? id == null : ssl.getID().equals(id)){
                return ssl;
            }
        }
        
        return null;
    }
    
    private void loadData(long seed){
        //loads new data...
        // add deck..
        deck = new Deck(seed, "deck");
        deck.shuffle();
        deck.shuffle();

        // make some stacks from the deck..
        piles = new ArrayList<>();
        for (int n = 0; n < 7; n++) {
            piles.add(new Stack(n * 32, 60, "stack"+n));
            for (int a = 0; a < n + 1; a++) {
                deck.dealCard(piles.get(n));
            }
            piles.get(n).getTopCard().setRevealed(true);
        }

        // specifiy a shallow...
        shallow = new Shallow(40, 0, "shallow");

        // add sorted piles...
        sortedPiles = new ArrayList<>();
        for (int idx = 0; idx < 4; idx++) {
            sortedPiles.add(new SortedStack(32 * (3 + idx), 0, "sortedstack"+idx));
        }
    }

    
    // draws all the cards in all of the containers...
    private void drawCards(Graphics g) {
        for (Stack s : piles) {
            s.drawCards(g, this);
        }

        for (Card c : deck.getDeck()) {
            c.draw(g, this);
        }

        for (Card c : getShallow().getStack()) {
            c.draw(g, this);
        }

        for (SortedStack ssl : sortedPiles) {
            for (Card c : ssl.getStack()) {
                c.draw(g, this);
            }
        }

        if (cL.isDragging()) {
            for (Card c : cL.getManipulatedCards()) {
                c.draw(g, this);
            }

        }

    }

    // finds the manipulated card and removes it from its container.
    public boolean reportManipulatedCard(Card c) {
        for (Stack s : piles) {
            for (Card c_idx : s.getStack()) {
                if (c_idx == c) {
                    return s.removeCard(c);
                }
            }
        }
        return deck.removeCard(c);
    }

    
    // gets all cards and returns it as a list.
    public ArrayList<Card> getCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Stack s : piles) {
            cards.addAll(s.getStack());
        }
        cards.addAll(deck.getStack());
        cards.addAll(shallow.getStack());
        for (CardContainer s : sortedPiles) {
            if (!s.cards.isEmpty()) {
                cards.addAll(s.getStack());
            }
        }
        return cards;
    }

    public ArrayList<Stack> getStacks() {
        return piles;
    }

    public ArrayList<SortedStack> getSortedStacks() {
        return sortedPiles;
    }

    public Deck getDeck() {
        return deck;
    }

    public Shallow getShallow() {
        return shallow;
    }

    public MoveMemory getMoveMemory() {
        return mm;
    }

    // gets the stack containing a specific card.
    public Stack getStackContaining(Card c) {
        for (Stack s : piles) {
            for (Card c_idx : s.getStack()) {
                if (c == c_idx) {
                    return s;
                }
            }
        }
        return null;
    }
    
    // gets the real container of the card; not a instanced reference of it...
    // wish I could use pointers...
    public CardContainer getContainer(CardContainer cc_ref){
        for(CardContainer cc : this.getStacks()){
            if(cc == cc_ref){
                return cc;
            }
        }
        for(CardContainer cc : this.getSortedStacks()){
            if(cc == cc_ref){
                return cc;
            }
        }
        if(cc_ref == this.getShallow()){
            return this.getShallow();
        }
        //should never reach this.
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCards(g);
        g.drawImage(ii, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        time += 1;
    }

    public void forceRepaint() {
        repaint();
    }
    
    
    public void endGame(){
       main.makeMainMenu();
    }
}
