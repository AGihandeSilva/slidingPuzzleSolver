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

import java.util.ArrayList;
import com.shinkusoft.javapuzzle2.PuzzleTileSet.PuzzleState;
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class PuzzleTileSetHistory
{
    public PuzzleTileSetHistory(PuzzleTileSet puzzle, PuzzleState state)
    {
        this.puzzle = puzzle;
        historyLog = new ArrayList<>();
        historyLog.add(state);
        index = 0;
    }
    
    private void saveState()
    {
        PuzzleState state = puzzle.getStateCopy();
        historyLog.add(state);
        
        index = historyLog.size() - 1;
        
        puzzle.propagateStateChange();
        
        JavaPuzzle2.logger.log(Level.FINER, "SAVE STATE index: {0} log length: {1}", new Object[]{index, historyLog.size()});
    }
    
    public boolean undoIsPossible()
    {
        return (index > 0);
    }
    
    //TODO test this!
    public void undo()
    {
        assert(undoIsPossible());
        int newIndexPos = index - 1;
        setNewState(newIndexPos);
    }
    
    private void setNewState(final int newStateIndex)
    {
        assert(newStateIndex >= 0 && newStateIndex < historyLog.size());
        puzzle.restoreState(historyLog.get(newStateIndex));
        index = newStateIndex;
        puzzle.propagateStateChange();
    }
    
    public boolean redoIsPossible()
    {
        return (index < (historyLog.size() - 1));
    }
    
    public void redo()
    {
        assert(redoIsPossible());
        setNewState(index + 1);
    }
    
    public void saveEditClearRedoList()
    {
        //in the case of a new edit,
        //clear any possible redo steps
        //ie if we have undo-ed to an old state,
        //a new edit will make it impossible to redo to later steps
        //this is non-branched redo
        final int highestIndex = historyLog.size() - 1;
        if (index < highestIndex)
        {
          for (int i =  highestIndex; i > index; --i)
          {
              historyLog.remove(i);
          }
        }
        saveState();
    }
    
    public void reset()
    {
        setNewState(0);
        saveState();
        index = historyLog.size() - 1; //TODO: is this the correct behaviour?
    }
    
    public int getLength()
    {
        assert(historyLog != null);
        return historyLog.size();
    }
    
    public int getCurrentMoveIndex()
    {
        return index;
    }
    
    final private ArrayList<PuzzleState>    historyLog;
    final private PuzzleTileSet             puzzle;
    private int index;
}
