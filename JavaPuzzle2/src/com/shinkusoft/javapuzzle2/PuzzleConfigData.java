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
import com.shinkusoft.javapuzzle2.JavaPuzzle2.*;

/**
 *
 * @author Gihan
 */
public class PuzzleConfigData implements Cloneable
{
    public PuzzleConfigData()
    {
        numRows = DEFAULT_NUM_ROWS;
        numCols = DEFAULT_NUM_COLS;
        useBitmap = false;
        bitmapFilename = null;
        importMode = BitmapImportMode.SCALE;
    }
    
    public PuzzleConfigData(int numRows, int numCols, boolean useBitmap, String bitmapFilename, BitmapImportMode mode)
    {
        this.numRows = numRows;
        this.numCols = numCols;
        this.importMode = mode;
        
        if (useBitmap)
        {
            assert(bitmapFilename != null);
            this.bitmapFilename = bitmapFilename;
        }
        else
        {
            this.bitmapFilename = null;
        }
        
        this.useBitmap = useBitmap;
        
        //TODO assert file exists? or just switch off bitmap mode?
    }
    
    @Override
    public PuzzleConfigData clone() throws CloneNotSupportedException
    {
        return (PuzzleConfigData) super.clone();
    }
    
    final public int numRows;
    final public int numCols;
    final public boolean useBitmap;
    final public String bitmapFilename;
    final public BitmapImportMode importMode;
    
    static final private int DEFAULT_NUM_ROWS = 4;
    static final private int DEFAULT_NUM_COLS = 4;
}
