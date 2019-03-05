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

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Gihan
 */
public class PuzzleTile implements Comparable<PuzzleTile>{

    /**
     *
     * @param newIndex
     */
    private PuzzleTile(int newIndex, PuzzleTileSet tileSet)
    {
        this(newIndex, tileSet, false);
    }
    
    public PuzzleTile(int newIndex, PuzzleTileSet tileSet, boolean isVacant) {
        logicalIndex = newIndex;
        tileSetOwner = tileSet;
        this.isVacant = isVacant;
        bitmap = null;
        SetPhysicalSlotIndex(newIndex - 1);
        tileSetOwner.updateL2PEntry(logicalIndex, newIndex - 1);
    }
    
    @Override
    public int compareTo(PuzzleTile other)
    {
        return logicalIndex - other.logicalIndex;
    }
    
    public void setBitmap(BufferedImage bitmap)
    {
        this.bitmap = bitmap;
    }
    
    public void SetGraphicsImage(String filename)
    {
        try
        {
            BufferedImage i = ImageIO.read(new File(filename));
            bitmap = i;
        }
        
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Rectangle2D.Double GetLocation() {
            assert(tileLocation != null);
            return tileLocation;
    }
    
    public BufferedImage GetBitmap()
    {
        return bitmap;
    }
    
    public int GetLogicalIndex()
    {
        return logicalIndex;
    }
    public int GetPhysicalSlotIndex()
    {
        return physicalSlotIndex;
    }
    
    final public void SetPhysicalSlotIndex(int physicalSlotIndex)
    {
        this.physicalSlotIndex = physicalSlotIndex;
        tileLocation = tileSetOwner.getPhysicalLocation(physicalSlotIndex);
    }
    
    public boolean isVacant()
    {
        return isVacant;
    }

    final private int logicalIndex;                 // 1-offset (e.g. numbers on tiles)
    final private PuzzleTileSet tileSetOwner;       
    final private boolean isVacant;
    private int physicalSlotIndex;                  // zero-offset
    private BufferedImage bitmap;
    private Rectangle2D.Double  tileLocation;
    
    static final private int DEFAULT_FONT_SIZE = 24;
    static final private int DEFAULT_Y_OFFSET = 100;
    static final private int DEFAULT_TILE_SEPARATION = 100;
    static final private int DEFAULT_TILE_SIZE = DEFAULT_TILE_SEPARATION - 5;
}
