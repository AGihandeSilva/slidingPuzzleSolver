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
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author Gihan
 */
public class PuzzleEditMenuKeeper
{
    public PuzzleEditMenuKeeper(PuzzleFrame owner)
    {
        this.owner = owner;
        editMenu = new JMenu("Edit");
    }
    
    public JMenu build()
    {
        populateEditMenu();
        return editMenu;
    }
    
    public void setUndoEnabled(boolean enabledState)
    {
        assert(undoItem != null);
        undoItem.setEnabled(enabledState);
        undoAction.setEnabled(enabledState);
    }
    
    public void setRedoEnabled(boolean enabledState)
    {
        assert(redoItem != null);
        redoItem.setEnabled(enabledState);
        redoAction.setEnabled(enabledState);
    }
    
    private void populateEditMenu()
    {
        operationMakeUndoCommand();
        operationMakeRedoCommand();
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        
        InputMap imap = owner.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        imap.put(KeyStroke.getKeyStroke("ctrl Z"), "pane.undo");
        imap.put(KeyStroke.getKeyStroke("ctrl Y"), "pane.redo");
        
        ActionMap amap = owner.getRootPane().getActionMap();
        amap.put("pane.undo", undoAction);
        amap.put("pane.redo", redoAction);
    }
    
    private void operationMakeUndoCommand()
    {
        undoItem =  new JMenuItem("Undo (Ctrl Z)"); //TODO display this more prettily
        undoAction = new editUndoAction();
        undoItem.addActionListener(undoAction);
        
        editMenu.add(undoItem);
    }
    
    private class editUndoAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            JavaPuzzle2.logger.fine("Starting undo!");
            owner.ExecuteUndo();
        }
    }
    
    private void operationMakeRedoCommand()
    {
        redoItem =  new JMenuItem("Redo (Ctrl Y)"); //TODO display this more prettily
        redoAction = new editRedoAction();
        redoItem.addActionListener(redoAction);
        editMenu.add(redoItem);
    }
    
    private class editRedoAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            JavaPuzzle2.logger.fine("Starting Redo!");
            owner.ExecuteRedo();
        }
    }
    
    final private JMenu editMenu;
    private JMenuItem undoItem;
    private editUndoAction undoAction;
    private JMenuItem redoItem;
    private editRedoAction redoAction;
    final private PuzzleFrame owner;
}
