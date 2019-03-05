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

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import com.shinkusoft.javapuzzle2.JavaPuzzle2.ResultObserver;
import java.text.DecimalFormat;


/**
 *
 * @author Gihan
 */
public class PuzzleFrame extends JFrame implements ResultObserver
{
    public interface PuzzleFrameFileSetter
    {
        void setFilename(String filename);
    }
    
    public enum SolverInfoDialogUpdateMode
    {
        SOLVER_MOVES
                {
            @Override
            void updateField(PuzzleInfoPanel infoPanel)
            {              
                int numMoves = JavaPuzzle2.getCurrentSolver().getMovesDone();
                        
                infoPanel.SolverMoveTreeValue.setText(Integer.toString(numMoves));
                infoPanel.repaint();
            }
        }, 
        
        SOLVER_MAX_MOVES
                        {
            @Override
            void updateField(PuzzleInfoPanel infoPanel)
            {
                int value = JavaPuzzle2.getToolsConfigData().getMaxSolverTreeSize();
                infoPanel.SolverMaxStepsValue.setText(Integer.toString(value));
            }
        },
        SOLVER_STATE
                {
            @Override
            void updateField(PuzzleInfoPanel infoPanel)
            {            
                JavaPuzzle2.SolverState state = JavaPuzzle2.getCurrentSolver().getState();
                        
                infoPanel.CurrentStatusValue.setText(JavaPuzzle2.SolverState.getSolverStateName(state));
                infoPanel.repaint();
            }
        }, ;
        
        abstract void updateField(PuzzleInfoPanel infoPanel);
        
    }
    
    public PuzzleFrame()
    {
        //get screen dimensions
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        
        //set frame width, height and let platform pick screen location
        
        setSize(screenWidth / 2, (screenHeight * 2) / 3);
        setLocationByPlatform(true);
        
        //prepare timeFormatter for elapsed timer
        timeFormatter = new DecimalFormat("#0.00");
        
        // set frame icon and title
        
        Image img = kit.getImage("icon.gif");
        setIconImage(img);
        
        createMenus();
        
        createPuzzleSpace();
        
        createInfoBar();
        
        createResultsReport();
        
        validate();
    }
    
    public void setCurrentPuzzle(PuzzleTileSet currentPuzzle)
    {
        String Title = "";
        this.currentPuzzle = currentPuzzle;
        if (currentPuzzle != null)
        {
            Title += currentPuzzle.getName();
        }
        
        Title += " - " + APP_TITLE_STRING;
        setTitle(Title);
    }

    public boolean compareCurrentPuzzle(PuzzleTileSet puzzle)
    {
        assert(currentPuzzle != null);
        
        return (currentPuzzle == puzzle);
    }
    
    public void Shuffle()
    {
        assert(currentPuzzle != null);
        currentPuzzle.ExecuteShuffleFlow(); //TODO: allow user to change settings
    }
    
    private void setUndoEnabled(boolean undoEnabledState)
    {
        assert(editMenuKeeper != null);
        editMenuKeeper.setUndoEnabled(undoEnabledState);
    }
    
    public void ExecuteUndo()
    {
        currentPuzzle.executeUndo();
    }
    
    private void setRedoEnabled(boolean redoEnabledState)
    {
        assert(editMenuKeeper != null);
        editMenuKeeper.setRedoEnabled(redoEnabledState);
    }
    
    public void ExecuteRedo()
    {
        currentPuzzle.executeRedo();
    }
    
    public boolean Solve()
    {
        final boolean solverResult = currentPuzzle.Solve();

        this.updateSolverRelatedInfo(SolverInfoDialogUpdateMode.SOLVER_MOVES);
        return (solverResult);
    }
    
    public void haltSolver()
    {
        JavaPuzzle2.haltSolver();
        
        JOptionPane.showMessageDialog(this,
                "Solve attempt unsuccessful",
                "Solver was halted as requested",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void generateResultResponse(PuzzleSolverResult solverResult)
    {
        resultsWindow.update(solverResult);
        resultsWindow.setVisible(true);

        if (!solverResult.result && !JavaPuzzle2.solverWasHalted())
        {
            JOptionPane.showMessageDialog(this,
                    "Solve attempt unsuccesful, please consider adjusting solver parameters",
                    "Puzzle solution not found",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void setResetEnabled(boolean resetEnabledState)
    {
        assert(operationMenuKeeper != null);
        operationMenuKeeper.setResetEnabled(resetEnabledState);
    }
    
    public void ResetPuzzle()
    {
        currentPuzzle.resetPuzzle();
    }
    
    public void setFilename(String filename)
    {
        this.newFilename = filename;
    }
    
    public void updatePuzzleDocumentList()
    {
        String[] descriptions = JavaPuzzle2.getPuzzleDescriptions();
        makeDescriptionList(descriptions); 
    }
    
    static public JFileChooser getFileChooser()
    {
        return FILE_CHOOSER;
    }
    
    public JList<String> makeDescriptionList(String[] descriptions)
    {
        if (listItem == null)
        {
            listItem = new JList<>(descriptions);
        }
        else
        {
            listItem.setListData(descriptions);
            
        }
        
        listItem.setAlignmentX(JList.LEFT_ALIGNMENT);
        
        int descriptionListWidth = DEFAULT_PUZZLE_DESC_LIST_WIDTH;
        JavaPuzzle2.logger.log(Level.FINE, "FileMenu width:{0}", descriptionListWidth);
        listItem.setFixedCellWidth(descriptionListWidth);
        listItem.setBackground(getBackground());
        listItem.setSelectionBackground(Color.GRAY);
        listItem.setSelectionForeground(Color.WHITE);
        return listItem;
    }
    
    private void createMenus()
    {
        JMenuBar  menuBar = new JMenuBar();
        
        Font menuFont = new java.awt.Font("Consolas", 0, 18);
        
        menuBar.setFont(menuFont);
        
        setJMenuBar(menuBar);
        
        fileMenu = (new PuzzleFileMenuBuilder(this)).build();
        fileMenu.setFont(menuFont);
        menuBar.add(fileMenu);
        
        editMenuKeeper = new PuzzleEditMenuKeeper(this);
        editMenu = editMenuKeeper.build();
        editMenu.setFont(menuFont);
        menuBar.add(editMenu);
        
        operationMenuKeeper = new PuzzleOperationMenuKeeper(this);
        operationMenu = (operationMenuKeeper).build();
        operationMenu.setFont(menuFont);
        menuBar.add(operationMenu);
        operationMenu.addMenuListener(

        new MenuListener()
        {
            @Override
            public void menuDeselected(MenuEvent e)
            {
                 JavaPuzzle2.logger.fine("Operations deselected!");
            }
            
            @Override
            public void menuCanceled(MenuEvent e)
            {
                 JavaPuzzle2.logger.fine("Operations canceled!");
            }
            
            @Override
            public void menuSelected(MenuEvent e)
            {
                JavaPuzzle2.logger.fine("Operations selected!");
            }
        }
                
        );
        
        PuzzleToolsMenuBuilder toolsBuilder = new PuzzleToolsMenuBuilder(this);
        
        toolsMenu = toolsBuilder.build();
        toolsSolverItem = toolsBuilder.getSolverConfigItem();
        toolsMenu.setFont(menuFont);
        setSolverMenuEnable(true);
        menuBar.add(toolsMenu);
        
        helpMenu = new JMenu("Help");
        helpMenu.setFont(menuFont);
        menuBar.add(helpMenu);
    }
    
    private void createInfoBar()
    {
        infoPanel = new PuzzleInfoPanel();
        infoPanel.setVisible(true);
        this.getContentPane().add(infoPanel, BorderLayout.NORTH);
        this.getContentPane().validate();
    }
    
    private void createResultsReport()
    {
        final boolean modal = false;
        resultsWindow = new SolverResultReport(this, modal);
        resultsWindow.setVisible(false);
    }
    
    private void updateInfo(final int currentCost, final int numMoves, final int numSolverMoves, final int numHistoryMoves)
    {
        if (currentCost != JavaPuzzle2.UNSPECIFIED_VALUE)
        {
            assert (currentCost >= 0);
            infoPanel.costValue.setText(Integer.toString(currentCost));
        }
        
        if (numMoves != JavaPuzzle2.UNSPECIFIED_VALUE)
        {
            assert(numMoves >= 0);
            infoPanel.numMovesValue.setText(Integer.toString(numMoves));       
        }
        
        //TODO solver moves
        
        if (numHistoryMoves != JavaPuzzle2.UNSPECIFIED_VALUE)
        {
            assert(numHistoryMoves >= 0);
            infoPanel.MoveHistoryLength.setText(Integer.toString(numHistoryMoves));    
        }
    }
    
    public void updateInProgressValues(final double elapsedTime, int movesDone)
    {
        String elapsedTimeString = timeFormatter.format(elapsedTime);
        infoPanel.SolverTimeValue.setText(elapsedTimeString);
        infoPanel.SolverMoveTreeValue.setText(Integer.toString(movesDone));
    }
    
    private void createPuzzleSpace()
    {
        puzzleAllDataPanel = new  JPanel(new BorderLayout());
        puzzleAllDataPanel.setBackground(new Color(230, 230, 230));
        puzzleAllDataPanel.setVisible(true);
        this.add(puzzleAllDataPanel, BorderLayout.CENTER);
        
        
        this.setContentPane(puzzleAllDataPanel);
    }
    public Graphics getPuzzleGraphics()
    {
        assert(puzzleAllDataPanel != null);
        Graphics g = puzzleAllDataPanel.getGraphics();
        if (g != null)
        {
            g.translate(0, getInfoPanelHeight());
        }
        return g;
    }
    
    public void resizeForPuzzle(Rectangle2D.Double background, final float frameScalingFactor)
    {
        setMinimumSize(new Dimension((int)((background.getX() + background.getWidth()) * frameScalingFactor),
                                (int)((background.getY() + background.getHeight()) * frameScalingFactor)));
    }
    
    public int getInfoPanelHeight()
    {
        final int infoPanelHeight = infoPanel.getHeight();
        //below avoids string concatenation
        JavaPuzzle2.logger.log(Level.FINER, "info panel height: {0}", infoPanelHeight);
        
        //comment to help with explanation of above code....
        //Integer[] data = {infoPanelHeight, 2, 19};
        //JavaPuzzle2.logger.log(Level.FINER, "info panel height: {0} {1} {2}", data); -> prints out infoPanelHeight value 2 19 at end of message
        return infoPanelHeight;
    }
    
    public void updatePuzzleRelatedInfo(PuzzleTileSet puzzle)
    {
        assert(puzzle !=  null);
        PuzzleTileSetHistory history = puzzle.getHistory();
        setResetEnabled(!puzzle.isSolved());
        setUndoEnabled(history.undoIsPossible());
        setRedoEnabled(history.redoIsPossible());
        
        updateInfo(puzzle.getCurrentSolutionCost(), history.getCurrentMoveIndex(), 
                JavaPuzzle2.UNSPECIFIED_VALUE,
                history.getLength() - 1); //TODO  
    }
    
    public void updateSolverRelatedInfo(SolverInfoDialogUpdateMode mode)
    {
        if (mode == PuzzleFrame.SolverInfoDialogUpdateMode.SOLVER_STATE) {
            assert(currentPuzzle != null);
            infoPanel.CurrentStatusValue.setText(JavaPuzzle2.SolverState.getSolverStateName(JavaPuzzle2.getCurrentSolverState()));
        } else {
            int solverMethodIndex = JavaPuzzle2.getToolsConfigData().getCurrentSolverMethod().getIntValue();

            if (solverMethodIndex != JavaPuzzle2.UNSPECIFIED_VALUE) {
                infoPanel.SolverMethodNameValue.setText(JavaPuzzle2.getSolverName(solverMethodIndex));
            }

            int solverMaxMovesValue = JavaPuzzle2.getToolsConfigData().getMaxSolverTreeSize();
            infoPanel.SolverMaxStepsValue.setText(Integer.toString(solverMaxMovesValue));
        }


        mode.updateField(infoPanel);
    }
    
    public void setSolverMenuEnable(final boolean enabled)
    {
        assert (toolsSolverItem != null);

        toolsSolverItem.setEnabled(enabled);

    }

    private void recordOperationsEnableState()
    {
        assert (operationMenu != null);
        final int operationCount = operationMenu.getItemCount();
        operationsDefaultEnable = new boolean[operationCount];

        for (int i = 0; i < operationCount; ++i)
        {
            JMenuItem item = operationMenu.getItem(i);
            if (item == null)
            {
                continue;
            }
            operationsDefaultEnable[i] = item.isEnabled();
        }
    }

    public void setOperationsEnable(final boolean enabled)
    {
        assert (operationMenu != null);

        final int operationCount = operationMenu.getItemCount();

        if (enabled)
        {
            if (operationsDefaultEnable != null
                    && operationCount == operationsDefaultEnable.length)
            {
                for (int i = 0; i < operationCount; ++i)
                {
                    JMenuItem item = operationMenu.getItem(i);
                    if (item == null)
                    {
                        continue;
                    }
                    item.setEnabled(operationsDefaultEnable[i]);
                }
            } 
            else
            {
                for (int i = 0; i < operationCount; ++i)
                {
                    JMenuItem item = operationMenu.getItem(i);
                    if (item == null)
                    {
                        continue;
                    }
                    item.setEnabled(true);
                }
            }
        } 
        else
        {
            //disabled
            recordOperationsEnableState();
            
            for (int i = 0; i < operationCount; ++i)
            {
                JMenuItem item = operationMenu.getItem(i);
                if (item == null)
                {
                    continue;
                }
                item.setEnabled(false);
            }
        }
    }
       
    private JList<String> listItem;
    private JMenu fileMenu;
    private JMenu editMenu;
    private PuzzleEditMenuKeeper editMenuKeeper;
    private JMenu operationMenu;
    private boolean[] operationsDefaultEnable;
    private PuzzleOperationMenuKeeper operationMenuKeeper;
    private JMenu toolsMenu;
    private JMenuItem toolsSolverItem;
    private JMenu helpMenu;
    private PuzzleInfoPanel infoPanel;
    private SolverResultReport resultsWindow;
    private JPanel          puzzleAllDataPanel;
    private PuzzleTileSet currentPuzzle;
    private String        newFilename;
    public  static final int     NO_SELECTED_ITEM_INDEX = -1;
    private static final String  APP_TITLE_STRING = "Java Tile Puzzle";
    private static final JFileChooser  FILE_CHOOSER = new JFileChooser();
    private DecimalFormat timeFormatter;
    
    static final int DEFAULT_PUZZLE_DESC_LIST_WIDTH = 750;
}
