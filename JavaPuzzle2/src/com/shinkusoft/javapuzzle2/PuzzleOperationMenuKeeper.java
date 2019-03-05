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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Gihan
 */
public class PuzzleOperationMenuKeeper
{
    public PuzzleOperationMenuKeeper(PuzzleFrame owner)
    {
        this.owner = owner;
        operationMenu = new JMenu("Operations");
    }
    
    public JMenu build()
    {
        populateOperationMenu();
        return operationMenu;
    }
    
    private void populateOperationMenu()
    {
        operationMakeShuffleCommand();
        operationMakeResetCommand();
        operationMenu.addSeparator();
        operationMakeSolveCommand();
        operationMakeHaltSolverCommand();
    }
    
    private void operationMakeShuffleCommand()
    {
        JMenuItem shuffleItem =  new JMenuItem("Shuffle Puzzle");
        operationShuffleListener listener = new operationShuffleListener();
        shuffleItem.addActionListener(listener);
        
        operationMenu.add(shuffleItem);
    }
    
    private class operationShuffleListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            JavaPuzzle2.logger.fine("Starting shuffle operation!");
            owner.Shuffle();
        }
    }
    
    private void operationMakeResetCommand()
    {
        resetItem =  new JMenuItem("Reset Puzzle");
        
        operationResetPuzzleListener listener = new operationResetPuzzleListener();
        resetItem.addActionListener(listener);
        setResetEnabled(false);
        operationMenu.add(resetItem);
    }
    
    public void setResetEnabled(boolean enabledState)
    {
        assert(resetItem != null);
        
        resetItem.setEnabled(enabledState);
    }
    
    private class operationResetPuzzleListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            JavaPuzzle2.logger.fine("Starting reset operation!");
            owner.ResetPuzzle();
        }
    }
    
    private void operationMakeSolveCommand()
    {
        JMenuItem solveItem =  new JMenuItem("Solve Puzzle");
        solveItem.setEnabled(true);
        
        ActionListener l = (ActionEvent event) ->
        {
            JavaPuzzle2.logger.log(Level.FINE, "SOLVER SOLUTION RESULT: {0}", owner.Solve());
            haltSolverItem.setEnabled(true);
        };
        
        solveItem.addActionListener(l);
        operationMenu.add(solveItem);
    }
    
    private void operationMakeHaltSolverCommand()
    {
        haltSolverItem =  new JMenuItem("Stop Solver");
        haltSolverItem.setEnabled(true);
        
        ActionListener l = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                owner.haltSolver();
                haltSolverItem.setEnabled(false);
            }
        };
        
        haltSolverItem.addActionListener(l);
        haltSolverItem.setEnabled(false);
        operationMenu.add(haltSolverItem);
    }
    
    
    final private JMenu operationMenu;
    private JMenuItem resetItem;
    private JMenuItem haltSolverItem;
    final private PuzzleFrame owner;
}
