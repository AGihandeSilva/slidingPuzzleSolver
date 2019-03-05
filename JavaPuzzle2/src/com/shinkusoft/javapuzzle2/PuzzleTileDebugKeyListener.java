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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class PuzzleTileDebugKeyListener extends KeyAdapter{
    
    public PuzzleTileDebugKeyListener(PuzzleTileHandler tileDisplayer, PuzzleFrame frame)
    {
        super();
        this.tileDisplayer = tileDisplayer;
        this.frame = frame;
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        final String s = KeyEvent.getKeyText(keyCode);
        
        final int foundLogicalIndex = findIndex(s);
  
        final Rectangle2D.Double foundPhysicalIndex = tileDisplayer.getPhysicalLocation(foundLogicalIndex);
        
        JavaPuzzle2.logger.log(Level.FINE, "keyPress: {0} index: {1} {2} {3}", new Object[]{s, foundLogicalIndex, foundPhysicalIndex.getX(), foundPhysicalIndex.getY()});
        
        if (foundLogicalIndex != JavaPuzzle2.UNDEFINED_LOCATION)
        {
            tileDisplayer.TrySlideMove(foundLogicalIndex);
        }
    }
    
    private int findIndex(String keyText)
    {
        //TODO : make a more efficient implementation
        int index = 1;
        for (String s: letters)
        {
            if (s.equals(keyText))
            {
                return(index);
            }
            ++index;
        }
        return JavaPuzzle2.UNDEFINED_LOCATION;
    }
    
    final private PuzzleTileHandler tileDisplayer;
    final private PuzzleFrame frame;
    final private String letters[] = { "1", "2", "3", "4", "5", "6", "7", "8",
                                    "9", "A", "B", "C", "D", "E", "F", "G" };
};

