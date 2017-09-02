
package giftest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Ryland
 */
public class ScorePanel extends javax.swing.JPanel {

    private Main m;
    
    public ScorePanel(Main m, String scoreFile) throws IOException {
        this.m = m;
        initComponents();
        loadAndSetScores(scoreFile);
    }
    
    public void loadAndSetScores(String scoreFile) throws IOException{
        File f = new File(scoreFile);
        String[] scores;
        if(!f.exists()){
            scores = new String[1];
            scores[0] = "NOBODY!!!";
        } else {
            BufferedReader r = new BufferedReader(new FileReader(scoreFile));
            String temp_s = r.readLine();
            if(temp_s == null){
                scores = new String[1];
                scores[0] = "NOBODY!!!";
            } else {
                int count = Integer.parseInt(temp_s);
                scores = new String[count];
                temp_s = r.readLine();
                int idx = 0;
                while(temp_s != null){
                    scores[idx] = temp_s;
                    temp_s = r.readLine();
                    idx++;
                }
            }
        }
        
        DefaultListModel listModel = new DefaultListModel();
        for(String s : scores){
            listModel.addElement(s); 
        }
        jList1 = new JList(listModel);
        jScrollPane1.setViewportView(jList1);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 180, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        m.makeMainMenu();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
