/*
** 
** Copyright (C) 2019 Ambrose Gihan de Silva
** 
** Redistribution and use in source and binary forms, with or without 
** modification, are permitted provided that the following conditions are met:
** 
** 1. Redistributions of source code must retain the above copyright notice, this 
** list of conditions and the following disclaimer.
** 
** 2. Redistributions in binary form must reproduce the above copyright notice, 
** this list of conditions and the following disclaimer in the documentation 
** and/or other materials provided with the distribution.
** 
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
** ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
** WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
** DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR 
** ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
**  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
**  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
**  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
**  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
**  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**  
 */
package com.shinkusoft.javapuzzle2;

import com.shinkusoft.javapuzzle2.PuzzleSolverResult;

/**
 *
 * @author gdesi
 */
public class SolverResultReport extends javax.swing.JDialog {

    /**
     * Creates new form solverSuccessReport
     */
    public SolverResultReport(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ResultTitleLabel = new javax.swing.JLabel();
        NumMovesLabel = new javax.swing.JLabel();
        TimeTakenLabel = new javax.swing.JLabel();
        SolverResult = new javax.swing.JLabel();
        NumMoves = new javax.swing.JLabel();
        TimeTaken = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Solution Summary");

        ResultTitleLabel.setText("Solution Status:");
        ResultTitleLabel.setToolTipText("");

        NumMovesLabel.setText("Moves:");
        NumMovesLabel.setToolTipText("");

        TimeTakenLabel.setText("Time Taken (s):");
        TimeTakenLabel.setToolTipText("");

        SolverResult.setBackground(new java.awt.Color(255, 255, 255));
        SolverResult.setForeground(new java.awt.Color(51, 51, 51));
        SolverResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SolverResult.setText("-");
        SolverResult.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        SolverResult.setMaximumSize(new java.awt.Dimension(200, 25));
        SolverResult.setMinimumSize(new java.awt.Dimension(200, 25));
        SolverResult.setOpaque(true);
        SolverResult.setPreferredSize(new java.awt.Dimension(200, 25));
        SolverResult.setRequestFocusEnabled(false);

        NumMoves.setBackground(new java.awt.Color(255, 255, 255));
        NumMoves.setForeground(new java.awt.Color(51, 51, 51));
        NumMoves.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NumMoves.setText("0");
        NumMoves.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        NumMoves.setMaximumSize(new java.awt.Dimension(80, 25));
        NumMoves.setMinimumSize(new java.awt.Dimension(80, 25));
        NumMoves.setOpaque(true);
        NumMoves.setPreferredSize(new java.awt.Dimension(80, 25));
        NumMoves.setRequestFocusEnabled(false);

        TimeTaken.setBackground(new java.awt.Color(255, 255, 255));
        TimeTaken.setForeground(new java.awt.Color(51, 51, 51));
        TimeTaken.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TimeTaken.setText("0.00");
        TimeTaken.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TimeTaken.setMaximumSize(new java.awt.Dimension(80, 25));
        TimeTaken.setMinimumSize(new java.awt.Dimension(80, 25));
        TimeTaken.setOpaque(true);
        TimeTaken.setPreferredSize(new java.awt.Dimension(80, 25));
        TimeTaken.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ResultTitleLabel)
                    .addComponent(NumMovesLabel)
                    .addComponent(TimeTakenLabel))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SolverResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumMoves, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TimeTaken, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ResultTitleLabel)
                    .addComponent(SolverResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NumMoves, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumMovesLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TimeTakenLabel)
                    .addComponent(TimeTaken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SolverResultReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SolverResultReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SolverResultReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SolverResultReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SolverResultReport dialog = new SolverResultReport(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    public void update(PuzzleSolverResult solverOutcome)
    {
        SolverResult.setText(solverOutcome.result ? successfulResult : unsuccessfulResult);
        NumMoves.setText(String.format("%d", solverOutcome.numMoves));
        TimeTaken.setText(String.format("%f", solverOutcome.timeTaken));
    }
    
    final String successfulResult = ("Solution found");
    final String unsuccessfulResult = ("No solution found");
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NumMoves;
    private javax.swing.JLabel NumMovesLabel;
    private javax.swing.JLabel ResultTitleLabel;
    private javax.swing.JLabel SolverResult;
    private javax.swing.JLabel TimeTaken;
    private javax.swing.JLabel TimeTakenLabel;
    // End of variables declaration//GEN-END:variables
}
