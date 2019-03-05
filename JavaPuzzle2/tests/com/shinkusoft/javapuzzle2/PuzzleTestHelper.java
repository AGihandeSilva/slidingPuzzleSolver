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

import static com.shinkusoft.javapuzzle2.JavaPuzzle2.AddPuzzle;
import java.nio.file.Paths;

/**
 *
 * @author Gihan
 */
public class PuzzleTestHelper
{
    public PuzzleFrame getFrame()
    {
        return frame;
    }

    public void setFrame(PuzzleFrame frame)
    {
        this.frame = frame;
    }

    public PuzzleTileSet getFirstPuzzle()
    {
        return firstPuzzle;
    }

    public void setFirstPuzzle(PuzzleTileSet firstPuzzle)
    {
        this.firstPuzzle = firstPuzzle;
    }
    
    public PuzzleConfigData getDefaultConfig()
    {
        return defaultConfig;
    }

    public String getDefaultPuzzleName()
    {
        return defaultPuzzleName;
    }
    
    public void defaultTestSetup()
    {
        setFrame(null);
        setFirstPuzzle(null);
        JavaPuzzle2.initForTests();
    }
    
    protected void addFirstPuzzleForTests(PuzzleConfigData config, String puzzleName)
    {
        setFrame(null);
        setFirstPuzzle(new PuzzleTileSet(config, getFrame(), puzzleName));
        AddPuzzle(getFrame(), getFirstPuzzle()); 
    }
    
    protected void addFirstPuzzleForTests()
    {
        addFirstPuzzleForTests(defaultConfig, defaultPuzzleName);
    }
    
    public String getImagefilePathName()
    {
        return imagefilePathName;
    }
    
    public PuzzleTileSet makeIMGPuzzleForTests(final int numRows, final int numCols, 
                                            final String puzzleName, final boolean useBitmap, 
                                            final JavaPuzzle2.BitmapImportMode mode)
    {
        String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
        //String bitmapFileName = "./src/images/Cake.JPG";
        String bitmapFileName = getImagefilePathName() + "Cake.JPG";
        //String bitmapFileName = "D:/data/coding/NetBeansProjects/JavaPuzzle2/src/images/Cake.JPG"; //current full path
        PuzzleConfigData secondConfig =  new PuzzleConfigData(numRows, numCols, 
                                                                useBitmap, bitmapFileName, mode);
        
        PuzzleTileSet newPuzzle = new PuzzleTileSet(secondConfig, getFrame(), puzzleName);
       return newPuzzle;
    }
    
    private PuzzleFrame frame;
    private PuzzleTileSet firstPuzzle;
    private final PuzzleConfigData defaultConfig = new PuzzleConfigData();
    private final String defaultPuzzleName = JavaPuzzle2.makeDefaultPuzzleName();
    private final String imagefilePathName = ("./src/com/shinkusoft/images/");

}
