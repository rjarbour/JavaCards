
package giftest;

import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends JFrame {

    JLabel statusbar;
    private MainMenuPanel mmp;
    private ScorePanel sp;
    private Board gp;
    
    public Main() {
        // set some basic stuff fo the panel
        makeMainMenu();
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(32*8, 48*6);
        setVisible(true);  
   }
    
    public void makeMainMenu(){
        // create the main menu panel and add it to the frame
        mmp = new MainMenuPanel(this);
        this.getContentPane().removeAll();
        this.getContentPane().add(mmp);
        setVisible(true); 
        this.validate();
    }
    
    
    // removes anything in the panel and creates the game from scratch.
    public void makeGame(){
        this.getContentPane().removeAll();
        gp = new Board(this);
        this.getContentPane().add(gp);
        setVisible(true);  
    }
    
    public void makeScores() throws IOException{
        sp = new ScorePanel(this, "scores.txt");
        this.getContentPane().removeAll();
        this.getContentPane().add(sp);
        setVisible(true);
        this.repaint();
    }
    
    // removes everything from panel and loads the game from a saved state
    public void loadGame() throws FileNotFoundException, IOException{
        // open a file chooser to go get the file.
        final JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        // get the name of the file
        if(result == JFileChooser.CANCEL_OPTION){
           return;
        }
        String saveFile = fc.getSelectedFile().toString();
        
        // does it end with .txt?
        if(!saveFile.endsWith(".txt")){
            // nope; cry about it.
            System.out.println("bad save!");
        } else {
            // remove the main menu panel and read the random seed of the game.
            this.remove(mmp);
            BufferedReader r = new BufferedReader(new FileReader(saveFile));
            String seed = r.readLine();
            // create a new instance of solitaire with the seed.
            this.add(new Board(this,Long.parseLong(seed), saveFile));
            setVisible(true);  
        }
    }

   public JLabel getStatusBar() {
       return statusbar;
   }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            // run on a new thread....
            @Override
            public void run() {
                Main ex = new Main();
                ex.setVisible(true);
            }
        });
    } 
}
