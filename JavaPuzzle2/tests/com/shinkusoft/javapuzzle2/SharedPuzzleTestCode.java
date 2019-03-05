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

import static org.junit.Assert.*;

/**
 *
 * @author Gihan
 */
public class SharedPuzzleTestCode
{
    public void executeGoodSlide(PuzzleTileSet puzzle, int[] movableDirList, int InitialHistoryLength, JavaPuzzle2.PuzzleTileDirection direction, boolean expectSolved)
    {
        puzzle.findMovableTileDirs(movableDirList);
        
        assertTrue(movableDirList[direction.getIntValue()] != JavaPuzzle2.INIT_LOCATION_VALUE);

        boolean slideResult = puzzle.PuzzleTileSlide(direction, true);
        
        assertTrue(slideResult);
        
        assertEquals(InitialHistoryLength + 1, puzzle.getHistory().getLength());
        
        assertTrue(puzzle.isSolved() == expectSolved);
        
        assertTrue(JavaPuzzle2.getCurrentSolverState() == JavaPuzzle2.SolverState.SOLVED || 
                  !expectSolved);
    }
    
    public void testSolverOnAlreadySolvedPuzzle(PuzzleTestHelper helper)
    {
        helper.addFirstPuzzleForTests();
        assertNull(helper.getFrame());
        assertTrue(helper.getFirstPuzzle().isSolved());
        final int InitialHistoryLength = helper.getFirstPuzzle().getHistory().getLength();
        
        JavaPuzzle2.Solve(helper.getFirstPuzzle());
        boolean result = WaitForReportResult(helper);
        assertEquals(InitialHistoryLength, helper.getFirstPuzzle().getHistory().getLength());
        
        assertTrue(helper.getFirstPuzzle().isSolved());
    }
    
    public void testSolverFourMovesRequired(PuzzleTestHelper helper)
    {
        helper.addFirstPuzzleForTests();
        
        int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];

        final PuzzleTileSet puzzle = helper.getFirstPuzzle();

        assertNull(helper.getFrame());
        assertTrue(puzzle.isSolved());

        final int InitialHistoryLength = puzzle.getHistory().getLength();
        executeGoodSlide(puzzle, movableDirList, InitialHistoryLength, JavaPuzzle2.PuzzleTileDirection.LEFT, false);
        executeGoodSlide(puzzle, movableDirList, InitialHistoryLength + 1, JavaPuzzle2.PuzzleTileDirection.UP, false);
        executeGoodSlide(puzzle, movableDirList, InitialHistoryLength + 2, JavaPuzzle2.PuzzleTileDirection.UP, false);
        executeGoodSlide(puzzle, movableDirList, InitialHistoryLength + 3, JavaPuzzle2.PuzzleTileDirection.UP, false);
        
        JavaPuzzle2.Solve(puzzle);
        boolean result = WaitForReportResult(helper);
        assertTrue(result);
        assertTrue(puzzle.isSolved());
    }
    
    public void testSolverNineMovesRequired(PuzzleTestHelper helper)
    {
        helper.addFirstPuzzleForTests();
        
        int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];

        final PuzzleTileSet puzzle = helper.getFirstPuzzle();

        assertNull(helper.getFrame());
        assertTrue(puzzle.isSolved());

        int historyLength = puzzle.getHistory().getLength();
        executeGoodSlide(puzzle, movableDirList, historyLength, JavaPuzzle2.PuzzleTileDirection.LEFT, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.UP, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.RIGHT, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.UP, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.LEFT, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.DOWN, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.LEFT, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.UP, false);
        executeGoodSlide(puzzle, movableDirList, ++historyLength, JavaPuzzle2.PuzzleTileDirection.RIGHT, false);
        
        JavaPuzzle2.Solve(puzzle);
        
        boolean result = WaitForReportResult(helper);
        assertTrue(result);
        assertTrue(puzzle.isSolved());
    }

    public boolean WaitForReportResult(PuzzleTestHelper helper)
    {
        boolean result;
        PuzzleSolverResult report = null;
        boolean solverIsActive = false;
        final PuzzleTileSet puzzle = helper.getFirstPuzzle();
        assert(puzzle != null);
        do
        {
            JavaPuzzle2.addDelay(1000000);
            report = JavaPuzzle2.getCurrentSolver().getResultReport();     
        } while (report == null);
        solverIsActive = puzzle.isPuzzleUnderSolverOperations();
        assertFalse(solverIsActive);
        result = report.result;
        return result;
    }
    
    public void testExecuteSolverOnDefaultShuffleFlow(PuzzleTestHelper helper, boolean expectedSolverResult)
    {
        helper.addFirstPuzzleForTests();
        helper.getFirstPuzzle().ExecuteShuffleFlow();
        
        JavaPuzzle2.Solve(helper.getFirstPuzzle());
        
        boolean result = WaitForReportResult(helper);
        
        assertTrue(result == expectedSolverResult);  
    }
}
