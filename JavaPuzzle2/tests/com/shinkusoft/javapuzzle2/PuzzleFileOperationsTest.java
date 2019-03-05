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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gihan
 */
public class PuzzleFileOperationsTest extends PuzzleTestBase
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
    }

    @Test
    public void testAttemptToCloseDefaultPuzzle()
    {
        helper.addFirstPuzzleForTests();
        assertEquals(1, JavaPuzzle2.getNumberOfPuzzles());
        boolean removed = JavaPuzzle2.ClosePuzzle(helper.getFrame(), 0);
        assertEquals(1, JavaPuzzle2.getNumberOfPuzzles());
        
        assertFalse(removed);
    }
    
    @Test
    public void testAttemptToCloseDefaultPuzzleAfterAddingNewPuzzle()
    {
        helper.addFirstPuzzleForTests();
        assertEquals(1, JavaPuzzle2.getNumberOfPuzzles());
        
        PuzzleTileSet secondPuzzle = helper.makeIMGPuzzleForTests(4, 4, 
                                            "Custom2", true, 
                                            JavaPuzzle2.BitmapImportMode.CROP);
        
        assertTrue(secondPuzzle.getBitmapImported()); 
        AddPuzzle(helper.getFrame(), secondPuzzle);
        assertEquals(2, JavaPuzzle2.getNumberOfPuzzles());
        
        boolean removed = JavaPuzzle2.ClosePuzzle(helper.getFrame(), 0);
        assertEquals(1, JavaPuzzle2.getNumberOfPuzzles());
        
        assertTrue(removed);
    }
}
