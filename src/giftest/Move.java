package giftest;

import java.util.ArrayList;


public class Move {
    private ArrayList<String> Cards;
    private CardContainer prev_cc;
    private CardContainer cur_cc;
    private boolean carry;
    
    // bad constructor...
    public Move(){
        Cards = new ArrayList<>();
        prev_cc = null;
        cur_cc = null;
        this.carry = false;
    }
    
    // single card with no carry.
    public Move(String c, CardContainer prev_cc, CardContainer cur_cc){
        Cards = new ArrayList<>();
        Cards.add(c);
        this.prev_cc = prev_cc;
        this.cur_cc = cur_cc;
        this.carry = false;
    }
    
    // many cards with no carry.
    public Move(ArrayList<String> c, CardContainer prev_cc, CardContainer cur_cc){
        Cards = c;
        this.prev_cc = prev_cc;
        this.cur_cc = cur_cc;
        this.carry = false;
    }
    
    // many cards with carry.
    public Move(ArrayList<String> c, CardContainer prev_cc, CardContainer cur_cc, boolean carry){
        Cards = c;
        this.prev_cc = prev_cc;
        this.cur_cc = cur_cc;
        this.carry = carry;
    }
    
    // single card with carry.
    public Move(String c, CardContainer prev_cc, CardContainer cur_cc, boolean carry){
        Cards = new ArrayList<>();
        Cards.add(c);
        this.prev_cc = prev_cc;
        this.cur_cc = cur_cc;
        this.carry = carry;
    }
    
    public void setCarry(boolean t){
        this.carry = t;
    }
    
    public boolean getCarry(){
        return this.carry;
    }
    
    public ArrayList<String> getSeralizedCards(){
        return Cards;
    }
    
    public CardContainer getPreviousCardContainer(){
        return prev_cc;
    }
    
    public CardContainer getCurrentCardContainer(){
        return cur_cc;
    }
    
    //creats a seralized version of the move...
    @Override
    public String toString(){
        String derp = "*";
        for(int idx = 0; idx < Cards.size(); idx++){
            // get the cards serialized value...
            derp += Cards.get(idx)+"";
            if(idx != Cards.size()-1){
                derp+=",";
            }
        }
        // get the seralized moves...
        derp += "*"+prev_cc.getID()+","+cur_cc.getID()+"*";
        return derp;
    }
}
