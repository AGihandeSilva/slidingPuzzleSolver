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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Gihan
 */
public class PuzzleFileMenuBuilder
{
    public PuzzleFileMenuBuilder(PuzzleFrame frame)
    {
        this.frame = frame;
        fileMenu = new JMenu("File");
    }
    
    public JMenu build()
    {
        populateFileMenu();
        return fileMenu;
    }
    
    private void populateFileMenu()
    {
        fileMakeNewPuzzle();
        
        fileMakeCloseCurrentPuzzle();
        
        fileMenu.addSeparator();
        
        fileMakePuzzleList();
        
        fileMenu.addSeparator();
        
        fileMakeExitItem();
    }
    
        
    private void fileMakeNewPuzzle()
    {
        JMenuItem newPuzzleItem =  new JMenuItem("New Puzzle");
        fileNewPuzzleListener listener = new fileNewPuzzleListener(frame, PuzzleFrame.getFileChooser());
        newPuzzleItem.addActionListener(listener);
        
        
        fileMenu.add(newPuzzleItem);
    }
    
    private class fileNewPuzzleListener implements ActionListener
    {
        public fileNewPuzzleListener(PuzzleFrame owner, JFileChooser FILE_CHOOSER)
        {
            this.owner = owner;
            this.FILE_CHOOSER = FILE_CHOOSER;
        }
        
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            if (rootDialog == null)
            {
                rootDialog =  new JDialog();
                rootDialog.setResizable(false);
                rootDialog.setTitle("New Puzzle");
                

                dialog = new NewPuzzlePanel(owner, "ImageName", rootDialog);
                
                rootDialog.addWindowListener(
                new WindowAdapter()
                        { 
                            @Override
                            public void windowActivated(WindowEvent e)
                            {
                                dialog.updateApplyButtonEnabled();
                            }
                        }
                );
                
                LoadImageListener listener = new LoadImageListener(owner, FILE_CHOOSER);

                dialog.setBrowseListener(listener);
                
                rootDialog.add(dialog);
                rootDialog.pack();
            }
            
            rootDialog.setVisible(true);
        }
        

        
        private JDialog rootDialog;
        private NewPuzzlePanel dialog;
        final private PuzzleFrame owner;
        final private JFileChooser FILE_CHOOSER;
    }
    
    private void fileMakeCloseCurrentPuzzle()
    {
        closeCurrentPuzzleItem =  new JMenuItem("Close");
        
        fileCloseCurrentPuzzleListener listener = new fileCloseCurrentPuzzleListener(frame);
        closeCurrentPuzzleItem.addActionListener(listener);
        
        setFileCloseCurrentPuzzleEnabled(false);
        
        fileMenu.add(closeCurrentPuzzleItem);
    }
    
    public void setFileCloseCurrentPuzzleEnabled(boolean enabled)
    {
        assert(closeCurrentPuzzleItem != null);
        closeCurrentPuzzleItem.setEnabled(enabled);
    }
    
    public boolean getCloseCurrentPuzzleIsEnabled()
    {
        boolean result = false;
        
        if (closeCurrentPuzzleItem  != null)
        {
            return (closeCurrentPuzzleItem.isEnabled());
        }
        
        return result;
    }
    

        private class fileCloseCurrentPuzzleListener implements ActionListener {
            
        public fileCloseCurrentPuzzleListener(PuzzleFrame owner)
        {
            this.owner = owner;
        }
            
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listItem == null) {
                    return;
                }
                ListModel model = listItem.getModel();
                int index = listItem.getSelectedIndex();
                if (index == PuzzleFrame.NO_SELECTED_ITEM_INDEX) //TODO is there a proper constant to use for this?
                {
                    return;
                }
                assert (index <= model.getSize());

                if (model.getSize() > 1)
                {
                    JavaPuzzle2.ClosePuzzle(owner, index);
                    owner.updatePuzzleDocumentList();
                }
            }

        final private PuzzleFrame owner;
    }
    
    private void fileMakePuzzleList()
    {
        JLabel label =  new JLabel("Available Puzzles:");
        String[] descriptions = JavaPuzzle2.getPuzzleDescriptions();
        //TODO revise this
        listItem = frame.makeDescriptionList(descriptions);
        assert(listItem != null);
        
        ListSelectionListener l = (ListSelectionEvent e) ->
        {
                int index = listItem.getSelectedIndex();
                if (index != PuzzleFrame.NO_SELECTED_ITEM_INDEX) //TODO is there a proper constant to use for this?
                {
                    assert (index < JavaPuzzle2.getNumberOfPuzzles());
                    JavaPuzzle2.logger.log(Level.FINE, "In JList listener! index:{0}", index);

                    PuzzleTileSet selectedPuzzle = JavaPuzzle2.getPuzzle(index);
                    assert (selectedPuzzle != null);

                    Point2D.Double rowColInfo = selectedPuzzle.getRowColData();
                    JavaPuzzle2.logger.log(Level.FINE, "Selected puzzle numRows{0}numCols{1}", new Object[]{rowColInfo.getX(), rowColInfo.getY()});

                    JavaPuzzle2.SwitchDisplayToPuzzle(frame, selectedPuzzle);
                    frame.repaint();
                    
                    setFileCloseCurrentPuzzleEnabled(JavaPuzzle2.getNumberOfPuzzles() > 1);
                }
                else
                {
                    JavaPuzzle2.logger.fine("selected Index was -1!");
                    setFileCloseCurrentPuzzleEnabled(false);
                }
                
                
        };
        
        listItem.addListSelectionListener(l);
        
        fileMenu.setHorizontalAlignment(JMenu.LEFT);
        fileMenu.add(label);
        fileMenu.add(listItem);
    }
    
    
    private void fileMakeExitItem()
    {
        Action exitAction = new AbstractAction("Exit")
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                System.exit(0);
            }
        };
        
        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);
    }
    
    private JList<String> listItem;
    private JMenuItem closeCurrentPuzzleItem;
    final private JMenu fileMenu;
    final private PuzzleFrame frame;
}
