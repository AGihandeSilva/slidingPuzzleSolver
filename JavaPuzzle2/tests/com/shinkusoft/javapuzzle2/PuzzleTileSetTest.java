
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

import java.awt.geom.Point2D;
import static com.shinkusoft.javapuzzle2.JavaPuzzle2.AddPuzzle;
import com.shinkusoft.javapuzzle2.JavaPuzzle2.BitmapImportMode;
import com.shinkusoft.javapuzzle2.JavaPuzzle2.Checksum;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gihan
 */
public class PuzzleTileSetTest extends PuzzleTestBase
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
    }

    
    private int getVacantTileIndex()
    {
        return helper.getFirstPuzzle().getVacantTileLogicalIndex();
    }
 
    
    private void checkCanMoveValues()
    {
        Point2D.Double rowColData = helper.getFirstPuzzle().getRowColData();
        final int numRows = (int)rowColData.getX();
        final int numCols = (int)rowColData.getY();
        final int numTiles = numRows * numCols;
        final int vacantTileIndex = getVacantTileIndex();
        
        assertNotEquals(vacantTileIndex, JavaPuzzle2.UNDEFINED_LOCATION);
        
        for (int i = 1; i <= numTiles; ++i)
        {
            boolean expectedValue = false;
            boolean canMove = helper.getFirstPuzzle().PuzzleTileCanMove(i);
            if (i != vacantTileIndex)
            {    
                int rowPos = helper.getFirstPuzzle().getPosAlongRow(i);
                int colPos = helper.getFirstPuzzle().getPosDownCol(i);
                
                if (Math.abs(rowPos - helper.getFirstPuzzle().getPosAlongRow(vacantTileIndex)) +
                        Math.abs(colPos - helper.getFirstPuzzle().getPosDownCol(vacantTileIndex)) == 1)    
                {
                    expectedValue = true;
                }
            }

            assertEquals(expectedValue, canMove);
        }
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    
    @Test
    public void testBuildDefaultPuzzle()
    {
        assertEquals(0, JavaPuzzle2.getNumberOfPuzzles());
        helper.addFirstPuzzleForTests();
        
        assertTrue(helper.getFirstPuzzle().isSolved());
        assertEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        
        assertEquals(helper.getDefaultPuzzleName(), helper.getFirstPuzzle().getName());
        
        Point2D.Double rowColData = helper.getFirstPuzzle().getRowColData();
        
        assertTrue(helper.getDefaultConfig().numRows > 0);
        assertTrue(helper.getDefaultConfig().numCols > 0);
        assertEquals(helper.getDefaultConfig().numRows, (int)rowColData.getX());
        assertEquals(helper.getDefaultConfig().numCols, (int)rowColData.getY());
        
        assertEquals(helper.getFirstPuzzle().getHistory().getLength(), 1);
        assertFalse(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertFalse(helper.getFirstPuzzle().getHistory().redoIsPossible());
        assertEquals(1, JavaPuzzle2.getNumberOfPuzzles());
    }
    
    @Test
    public void testDoDefaultShuffleOperation()
    {
        helper.addFirstPuzzleForTests();
        
        assertTrue(helper.getFirstPuzzle().isSolved());
        assertEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        final Checksum solvedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        helper.getFirstPuzzle().ExecuteShuffleFlow();
        final int movesAfterShuffle = helper.getFirstPuzzle().getHistory().getLength();
        assertTrue(movesAfterShuffle > 1);
        //theoretically possible that the following may not be true....
        assertFalse(helper.getFirstPuzzle().isSolved());
        assertNotEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        final Checksum shuffledCheckSum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        assertFalse(solvedChecksum.equals(shuffledCheckSum));
        
        assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertFalse(helper.getFirstPuzzle().getHistory().redoIsPossible());
        
        final int undosToRevertShuffle = movesAfterShuffle - 1;
        
        //full undo
        helper.getFirstPuzzle().executeUndo();
        Checksum currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        assert(helper.getFirstPuzzle().isSolved() || !(currentChecksum.equals(solvedChecksum)));
        
        for (int i = 0; i < undosToRevertShuffle - 2; ++i)
        {
            assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
            assertTrue(helper.getFirstPuzzle().getHistory().redoIsPossible());
            helper.getFirstPuzzle().executeUndo();
            currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
            assert(helper.getFirstPuzzle().isSolved() || !(currentChecksum.equals(solvedChecksum)));
        }
        assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertTrue(helper.getFirstPuzzle().getHistory().redoIsPossible());
            
        helper.getFirstPuzzle().executeUndo();
        assertFalse(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertTrue(helper.getFirstPuzzle().getHistory().redoIsPossible());
        assertTrue(helper.getFirstPuzzle().isSolved());
        assertEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        assertTrue(solvedChecksum.equals(currentChecksum));
        
        //full redo
        helper.getFirstPuzzle().executeRedo();
        currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        assert(helper.getFirstPuzzle().isSolved() || (!currentChecksum.equals(solvedChecksum)));
        
        for (int i = 0; i < undosToRevertShuffle - 2; ++i)
        {
            assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
            assertTrue(helper.getFirstPuzzle().getHistory().redoIsPossible());
            helper.getFirstPuzzle().executeRedo();
            currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
            assert(helper.getFirstPuzzle().isSolved() || !(currentChecksum.equals(solvedChecksum)));
        }
        assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertTrue(helper.getFirstPuzzle().getHistory().redoIsPossible());
        
        helper.getFirstPuzzle().executeRedo();
        assertTrue(helper.getFirstPuzzle().getHistory().undoIsPossible());
        assertFalse(helper.getFirstPuzzle().getHistory().redoIsPossible());
        assertFalse(helper.getFirstPuzzle().isSolved());
        assertNotEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        currentChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        assertTrue(shuffledCheckSum.equals(currentChecksum));
    }
    
    @Test
    public void testCheckRowColData()
    {
        helper.addFirstPuzzleForTests();
        
        Point2D.Double rowColData = helper.getFirstPuzzle().getRowColData();
        assertEquals((int)rowColData.getX(), helper.getDefaultConfig().numRows);
        assertEquals((int)rowColData.getY(), helper.getDefaultConfig().numCols);
    }
  
    @Test
    public void testCheckSlideCanMove()
    {
        helper.addFirstPuzzleForTests();
        
        checkCanMoveValues();
        
        int vacantTileIndex = getVacantTileIndex();
        assert(vacantTileIndex > 0 && 
                vacantTileIndex <= (helper.getDefaultConfig().numRows * helper.getDefaultConfig().numCols) );
    }
    
    @Test
    public void testDefaultSlideNorth()
    {
        helper.addFirstPuzzleForTests();
        final Checksum solvedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        boolean moved = helper.getFirstPuzzle().PuzzleTileSlide(JavaPuzzle2.PuzzleTileDirection.UP, false);
        
        final Checksum movedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        assertTrue(moved);
        assertFalse(solvedChecksum.equals(movedChecksum));
        assertFalse(helper.getFirstPuzzle().isSolved());
        assertNotEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        assertEquals(2, helper.getFirstPuzzle().getHistory().getLength());
    }
    
    @Test
    public void testDefaultSlideEast()
    {
        helper.addFirstPuzzleForTests();
        final Checksum solvedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        boolean moved = helper.getFirstPuzzle().PuzzleTileSlide(JavaPuzzle2.PuzzleTileDirection.RIGHT, false);
        
        final Checksum movedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        assertFalse(moved);
        assertTrue(solvedChecksum.equals(movedChecksum));
        assertTrue(helper.getFirstPuzzle().isSolved());
        assertEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        assertEquals(1, helper.getFirstPuzzle().getHistory().getLength());
    }
    
    @Test
    public void testDefaultSlideWest()
    {
        helper.addFirstPuzzleForTests();
        final Checksum solvedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        boolean moved = helper.getFirstPuzzle().PuzzleTileSlide(JavaPuzzle2.PuzzleTileDirection.LEFT, false);
        
        final Checksum movedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        assertTrue(moved);
        assertFalse(solvedChecksum.equals(movedChecksum));
        assertFalse(helper.getFirstPuzzle().isSolved());
        assertNotEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        assertEquals(2, helper.getFirstPuzzle().getHistory().getLength());
    }
    
    @Test
    public void testDefaultSlideSouth()
    {
        helper.addFirstPuzzleForTests();
        final Checksum solvedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        boolean moved = helper.getFirstPuzzle().PuzzleTileSlide(JavaPuzzle2.PuzzleTileDirection.DOWN, false);
        
        final Checksum movedChecksum = helper.getFirstPuzzle().calculatePuzzleStateChecksum();
        
        assertFalse(moved);
        assertTrue(solvedChecksum.equals(movedChecksum));
        assertTrue(helper.getFirstPuzzle().isSolved());
        assertEquals(0, helper.getFirstPuzzle().calculateCurrentStateCost());
        assertEquals(1, helper.getFirstPuzzle().getHistory().getLength());
    }
    
    @Test
    public void testAddCustom4x4Puzzle()
    {
        helper.addFirstPuzzleForTests();
        assertNull(helper.getFrame());
        
        PuzzleTileSet secondPuzzle = helper.makeIMGPuzzleForTests(4, 4, 
                                            "Custom1", true, 
                                            BitmapImportMode.CROP);
        assertTrue(secondPuzzle.getBitmapImported());
        AddPuzzle(helper.getFrame(), secondPuzzle);
        
        assertEquals(2, JavaPuzzle2.getNumberOfPuzzles()); 
        assertTrue(secondPuzzle.isSolved());
        assertEquals(0, secondPuzzle.calculateCurrentStateCost());
        final Point2D.Double rowColData = secondPuzzle.getRowColData();
        assertEquals(4, (int)rowColData.getX());
        assertEquals(4, (int)rowColData.getY());
    }
    
    @Test
    public void testAddCustom4x4PuzzleBitmapImportFails()
    {
        helper.addFirstPuzzleForTests();
        assertNull(helper.getFrame());
        //String badBitmapFileName = "./src/images/IMG_not_there_0014.JPG";
        String badBitmapFileName = helper.getImagefilePathName() + "IMG_not_there_0014.JPG";
        PuzzleConfigData secondConfig =  new PuzzleConfigData(4, 4, true, badBitmapFileName, BitmapImportMode.CROP);
        String secondPuzzleName = "Custom1";
        //bit import fails
        PuzzleTileSet secondPuzzle = new PuzzleTileSet(secondConfig, helper.getFrame(), secondPuzzleName);
        
        assertFalse(secondPuzzle.getBitmapImported()); 
        AddPuzzle(helper.getFrame(), secondPuzzle);
        
        //another puzzle, bit import ok
        PuzzleTileSet thirdPuzzle = helper.makeIMGPuzzleForTests(4, 4, 
                                            "Custom2", true, 
                                            BitmapImportMode.CROP);
        assertTrue(thirdPuzzle.getBitmapImported());
        AddPuzzle(helper.getFrame(), thirdPuzzle);
        
        assertEquals(3, JavaPuzzle2.getNumberOfPuzzles());
    }
    
    @Test
    public void testPuzzleStateRestoredUponSwitch()
    {
        //TODO
    }
    
    @Test
    public void testAddCustom4x6Puzzle()
    {
        helper.addFirstPuzzleForTests();
        assertNull(helper.getFrame());
        
        PuzzleTileSet secondPuzzle = helper.makeIMGPuzzleForTests(4, 6, 
                                            "Custom1", true, 
                                            BitmapImportMode.CROP);
        assertTrue(secondPuzzle.getBitmapImported());
        AddPuzzle(helper.getFrame(), secondPuzzle);
        
        assertEquals(2, JavaPuzzle2.getNumberOfPuzzles()); 
        assertTrue(secondPuzzle.isSolved());
        assertEquals(0, secondPuzzle.calculateCurrentStateCost());
        final Point2D.Double rowColData = secondPuzzle.getRowColData();
        assertEquals(4, (int)rowColData.getX());
        assertEquals(6, (int)rowColData.getY());
        assertTrue(secondPuzzle.getBitmapImported());
    }
    
    private void shuffleCustom4x6Puzzle(PuzzleChecksumPair puzzleInfo)
    {
        //reuse previous test to make a custom 4x6 puzzle
        testAddCustom4x6Puzzle();
        
        PuzzleTileSet firstPuzzle = JavaPuzzle2.getPuzzle(0);
        assertNotNull(firstPuzzle);
        final Point2D.Double firstRowColData = firstPuzzle.getRowColData();
        assertEquals(4, (int)firstRowColData.getX());
        assertEquals(4, (int)firstRowColData.getY());
        assertTrue(firstPuzzle.isSolved());
        assertEquals(0, firstPuzzle.calculateCurrentStateCost());
        Checksum firstChecksum = firstPuzzle.calculatePuzzleStateChecksum();
        
        PuzzleTileSet secondPuzzle = JavaPuzzle2.getPuzzle(1);
        assertNotNull(secondPuzzle);
        final Point2D.Double secondRowColData = secondPuzzle.getRowColData();
        assertEquals(4, (int)secondRowColData.getX());
        assertEquals(6, (int)secondRowColData.getY());
        assertTrue(secondPuzzle.isSolved());
        assertEquals(0, secondPuzzle.calculateCurrentStateCost());
        puzzleInfo.checksum = secondPuzzle.calculatePuzzleStateChecksum();
        
        assertFalse(firstChecksum.equals(puzzleInfo.checksum));
        
        secondPuzzle.ExecuteShuffleFlow();
        
        puzzleInfo.puzzle = secondPuzzle;
    }
    
    @Test
    public void testShuffleCustom4x6Puzzle_reset()
    {
        PuzzleChecksumPair puzzleInfo = new PuzzleChecksumPair(null, null);
        shuffleCustom4x6Puzzle(puzzleInfo);
        final PuzzleTileSet secondPuzzle = puzzleInfo.puzzle;
        assertNotNull(secondPuzzle);
        assertFalse(secondPuzzle.isSolved());
        assertNotEquals(0, secondPuzzle.calculateCurrentStateCost());
        Checksum secondShuffledChecksum = secondPuzzle.calculatePuzzleStateChecksum();
        assertFalse(puzzleInfo.checksum.equals(secondShuffledChecksum));
        
        secondPuzzle.resetPuzzle();
        assertTrue(secondPuzzle.isSolved());
        assertEquals(0, secondPuzzle.calculateCurrentStateCost());
        Checksum secondResetChecksum = secondPuzzle.calculatePuzzleStateChecksum();
        assertTrue(puzzleInfo.checksum.equals(secondResetChecksum));
    }
    
    @Test
    public void testShuffleCustom4x6Puzzle_multipleUndo()
    {
        PuzzleChecksumPair puzzleInfo = new PuzzleChecksumPair(null, null);
        shuffleCustom4x6Puzzle(puzzleInfo);
        final PuzzleTileSet secondPuzzle = puzzleInfo.puzzle;
        assertNotNull(secondPuzzle);
        assertFalse(secondPuzzle.isSolved());
        assertNotEquals(0, secondPuzzle.calculateCurrentStateCost());
        Checksum secondShuffledChecksum = secondPuzzle.calculatePuzzleStateChecksum();
        assertFalse(puzzleInfo.checksum.equals(secondShuffledChecksum));
        
        int undoLength = secondPuzzle.getHistory().getLength() -  1;
        
        for (int i = 0; i < undoLength; ++i)
        {
            assertFalse(secondPuzzle.isSolved());
            assertNotEquals(0, secondPuzzle.calculateCurrentStateCost());
            secondPuzzle.executeUndo();
        }
        
        assertTrue(secondPuzzle.isSolved());
        assertEquals(0, secondPuzzle.calculateCurrentStateCost());
        Checksum secondResetChecksum = secondPuzzle.calculatePuzzleStateChecksum();
        assertTrue(puzzleInfo.checksum.equals(secondResetChecksum));
        
        for (int i = 0; i < undoLength; ++i)
        {
            secondPuzzle.executeRedo();
        }
         Checksum secondRedoneChecksum = secondPuzzle.calculatePuzzleStateChecksum();
         assertTrue(secondShuffledChecksum.equals(secondRedoneChecksum));
    }
    
    @Test
    public void testDefaultSolverOnAlreadySolvedPuzzle()
    {
        helper.addFirstPuzzleForTests();
        assertNull(helper.getFrame());
        assertTrue(helper.getFirstPuzzle().isSolved());
        final int InitialHistoryLength = helper.getFirstPuzzle().getHistory().getLength();
        
        boolean result = JavaPuzzle2.Solve(helper.getFirstPuzzle());
        assertEquals(InitialHistoryLength, helper.getFirstPuzzle().getHistory().getLength());
        
        assertTrue(helper.getFirstPuzzle().isSolved());
    }
    
    private class PuzzleChecksumPair
    {
       public PuzzleChecksumPair(PuzzleTileSet puzzle, Checksum checksum)
       {
           this.puzzle = puzzle;
           this.checksum = checksum;
       }
       public PuzzleTileSet puzzle;
       public Checksum checksum;
    }
}
