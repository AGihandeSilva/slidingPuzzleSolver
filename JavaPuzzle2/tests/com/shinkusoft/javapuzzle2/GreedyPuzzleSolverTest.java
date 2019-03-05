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
public class GreedyPuzzleSolverTest
{
    
    public GreedyPuzzleSolverTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        helper.defaultTestSetup();
        JavaPuzzle2.getToolsConfigData().setCurrentSolverMethod(JavaPuzzle2.PuzzleToolsSolverMethod.PURE_GREEDY_SEARCH);
        assertEquals(JavaPuzzle2.PuzzleToolsSolverMethod.PURE_GREEDY_SEARCH.getIntValue(),
                JavaPuzzle2.getToolsConfigData().getCurrentSolverMethod().getIntValue());
    }
    
    @After
    public void tearDown()
    {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testGreedySolverOnAlreadySolvedPuzzle()
    {
        sharedCode.testSolverOnAlreadySolvedPuzzle(helper);
    }

    @Test
    public void testGreedySolverOneMoveDOWNrequired()
    {
        int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];

        helper.addFirstPuzzleForTests();
        
        final PuzzleTileSet  puzzle = helper.getFirstPuzzle();
        
        assertNull(helper.getFrame());
        assertTrue(puzzle.isSolved());

        puzzle.findMovableTileDirs(movableDirList);
        
        assertTrue(movableDirList[JavaPuzzle2.PuzzleTileDirection.UP.getIntValue()] != JavaPuzzle2.INIT_LOCATION_VALUE);
        
        final int InitialHistoryLength = puzzle.getHistory().getLength();
        
        boolean slideResult = puzzle.PuzzleTileSlide(JavaPuzzle2.PuzzleTileDirection.UP, true);
        
        assertEquals(InitialHistoryLength + 1, puzzle.getHistory().getLength());
        
        assertFalse(puzzle.isSolved());
         
        //try to solve it!
        JavaPuzzle2.Solve(puzzle);

        boolean result = sharedCode.WaitForReportResult(helper);
 
        assertTrue(result);
        assertTrue(puzzle.isSolved());
        
        assertEquals(InitialHistoryLength + 2, puzzle.getHistory().getLength());
        
        assertTrue(slideResult);
    }
    
    @Test
    public void testGreedySolverFourMovesRequired()
    {
        sharedCode.testSolverFourMovesRequired(helper);
    }
    
//    @Test
//    public void testGreedySolverNineMovesRequired()
//    {
//        sharedCode.testSolverNineMovesRequired(helper);
//    }
    
    @Test
    public void testUCSAttemptShuffleOperationSolution()
    {
        sharedCode.testExecuteSolverOnDefaultShuffleFlow(helper, false);
    }

    final private PuzzleTestHelper helper = new PuzzleTestHelper();
    final private SharedPuzzleTestCode  sharedCode = new SharedPuzzleTestCode();
}
