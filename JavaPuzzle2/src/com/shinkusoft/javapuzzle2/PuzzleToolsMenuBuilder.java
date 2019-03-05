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
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Gihan
 */
public class PuzzleToolsMenuBuilder
{
    public PuzzleToolsMenuBuilder(PuzzleFrame frame)
    {
        this.frame = frame;
        toolsMenu = new JMenu("Tools");
    }
    
    public JMenu build()
    {
        populateToolsMenu();
        return toolsMenu;
    }

    public JMenuItem getSolverConfigItem()
    {
        return solverConfigItem;
    }
    
    
    private void populateToolsMenu()
    {
        ToolsMakeSolverConfiguration();
        
        toolsMenu.addSeparator();
        
        ToolsMakeOptions();
        

    }
    
    private void ToolsMakeSolverConfiguration()
    {
        
        solverConfigItem =  new JMenuItem("Solver configuration");
        ToolsSolverConfigListener listener = new ToolsSolverConfigListener();
        solverConfigItem.addActionListener(listener);
        toolsMenu.add(solverConfigItem);
    }
    
    
    private void ToolsMakeOptions()
    {
        JMenuItem optionsItem =  new JMenuItem("Options");
        
        optionsItem.setEnabled(false);  //TODO
        
        toolsMenu.add(optionsItem);
    }
    
    private class ToolsSolverConfigListener  implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)      
        {
            if (solverConfigDialog == null)
            {
                final boolean modal = true;
                solverConfigDialog = new ToolsSolverDialog(frame, modal);
                solverConfigDialog.pack();
                
                solverConfigDialog.addWindowListener(
                        
                            new WindowAdapter()
                            {
                                @Override
                                public void windowActivated(WindowEvent e)
                                {
                                    PuzzleToolsConfig toolsConfig = JavaPuzzle2.getToolsConfigData();
                                    assert(toolsConfig != null);
                                    solverConfigDialog.SolverUpdatesGUIcheckBox.setSelected(toolsConfig.getSolverUpdatesGUI());
                                    solverConfigDialog.SolverMethodValue.setSelectedIndex(
                                            toolsConfig.getCurrentSolverMethod().getIntValue());
                                }
                            }
                        );
                
            }
           solverConfigDialog.setVisible(true);
        }
    }
    
    
    final private JMenu toolsMenu;
    private ToolsSolverDialog solverConfigDialog;
    private JMenuItem solverConfigItem;
    final private PuzzleFrame frame;
}
