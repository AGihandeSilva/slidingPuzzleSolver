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

import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import javax.swing.JComponent;

/**
 *
 * @author Gihan
 */
public class PuzzleTileHandler extends JComponent{
                  
    public PuzzleTileHandler(PuzzleTileSet firstPuzzle)
    {
        currentPuzzle = firstPuzzle;
    }
      
    public PuzzleTileSet getCurrentPuzzle()
    {
        return currentPuzzle;
    }
    
    public void setCurrentPuzzle(PuzzleTileSet puzzle)
    {
        currentPuzzle = puzzle;
    }
    
    public boolean TrySlideMove(int logicalIndex)
    {
        if (logicalIndex == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            return false;
        }
        PuzzleTile pressedTile = currentPuzzle.getPuzzleTile(logicalIndex);
        assert (pressedTile != null);

        JavaPuzzle2.logger.log(Level.FINE, "from tile {0}", pressedTile.GetLogicalIndex());

        boolean canMove = currentPuzzle.PuzzleTileCanMove(logicalIndex);

        JavaPuzzle2.logger.log(Level.FINE, "Can move {0}", canMove);
        if (canMove)
        {
            currentPuzzle.PuzzleTileSlide(logicalIndex, doAnimatedSlide);
            currentPuzzle.getHistory().saveEditClearRedoList();
            return true;
        } 
        else
        {
            Toolkit.getDefaultToolkit().beep();
        }
        return false;
    }
    
    public Rectangle2D.Double getPhysicalLocation(int physicalIndex)
    {
        assert(currentPuzzle != null);
        
        return currentPuzzle.getPhysicalLocation(physicalIndex);
    }
       
    public int getPhysicalSlotIndexForPoint(Point2D p)
    {
        assert(currentPuzzle != null);
        return currentPuzzle.getPhysicalSlotIndexForPoint(p);
    }
    
    public int getLogicalIndexFromPhysicalSlotIndex(int physicalSlotIndex)
    {
        assert(currentPuzzle != null);
        return currentPuzzle.getLogicalIndexFromPhysicalSlotIndex(physicalSlotIndex); 
    }
    
    //debug
    public void addDot(Point2D p)
    {
         assert(currentPuzzle != null);
         currentPuzzle.addDot(p);
    }

    private PuzzleTileSet currentPuzzle;
    final private boolean doAnimatedSlide = false;
}
