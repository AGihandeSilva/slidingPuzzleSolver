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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class PuzzleTileMouseListener extends MouseAdapter{
    
    public PuzzleTileMouseListener(PuzzleTileHandler tileDisplayer, PuzzleFrame frame)
    {
        this.tileDisplayer = tileDisplayer;
        this.frame = frame;
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        JavaPuzzle2.logger.entering("PuzzleTileMouseListener", "mousePressed");
        
        //fix the click offset to match the pointer 
        //TODO : is there a better way to fix this?
        e.translatePoint(PRESS_OFFSET_X, PRESS_OFFSET_Y - frame.getInfoPanelHeight());
        
        final int x = e.getX();
        final int y = e.getY();
        
        final Point p = e.getPoint();
        final double px = p.getX();
        final double py = p.getY();
        
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            //TODO: merge these calls
            int physicalIndex = tileDisplayer.getPhysicalSlotIndexForPoint(p);
            int logicalIndex = tileDisplayer.getLogicalIndexFromPhysicalSlotIndex(physicalIndex);
            
            JavaPuzzle2.logger.log(Level.FINE, "button1 x: {0} y: {1} px: {2} py: {3} physical slot: {4} logical tile: {5}", 
                                            new Object[]{x, y, px, py, physicalIndex, logicalIndex});
            
            //debug
            if (JavaPuzzle2.DEBUG_DRAW_DOTS_AT_PRESS_POS)
            {
                this.tileDisplayer.addDot(p);
            }
            
            tileDisplayer.TrySlideMove(logicalIndex);
        }
    }
    
    final private PuzzleFrame frame;
    final private PuzzleTileHandler tileDisplayer;
    
    final static private int PRESS_OFFSET_X = -8;
    final static private int PRESS_OFFSET_Y = -50;
}
