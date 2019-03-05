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

import com.shinkusoft.javapuzzle2.JavaPuzzle2.Checksum;
import java.awt.geom.Point2D;
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class PuzzleTileSetCalculator
{

    public PuzzleTileSetCalculator(int[] P2Lmap, int[] L2Pmap, int numRows, int numCols, final int vacantSlotLogicalIndex)
    {
        this.numRows = numRows;
        this.numCols = numCols;
        numTiles = numRows * numCols;
        this.P2Lmap = P2Lmap;
        this.L2Pmap = L2Pmap;
        this.vacantSlotLogicalIndex = vacantSlotLogicalIndex;
    }
    
    static public PuzzleTileSetCalculator buildNewCalculator(final PuzzleTileSet puzzle)
    {
        PuzzleTileSetCalculator calculator;
        
        PuzzleTileSet.PuzzleState state = puzzle.getStateCopy();
        int[] calculatorP2LMapping = state.getP2LMappingCopy();
        int[] calculatorL2PMapping = new int[calculatorP2LMapping.length + 1]; //zero offset -> non zero indices
        
        Point2D.Double rowColData = puzzle.getRowColData();
        final int calcNumRows = (int)rowColData.getX();
        final int calcNumCols = (int)rowColData.getY();
        final int calcNumTiles = calcNumRows * calcNumCols;
 
        calculator = new PuzzleTileSetCalculator(
                calculatorP2LMapping, 
                calculatorL2PMapping, 
                calcNumRows, 
                calcNumCols, 
                puzzle.getVacantTileLogicalIndex());
        
        //populate L2
        for (int i = 0; i < calcNumTiles; ++i)
        {
            int logicalIndex = calculatorP2LMapping[i];
            calculator.updateL2PEntry(calculatorL2PMapping, logicalIndex, i);
        }
        
        return calculator;
    }
    
    public void PuzzleTileSetSwitchCalculatorMaps(int[] P2Lmap)
    {
        this.P2Lmap = P2Lmap;
    }
        
    
    final public void findMovableTileDirs(int[] result, final int vacantSlotLocation)
    {
        assert(result.length == JavaPuzzle2.ALL_DIRECTIONS);
        assert(P2Lmap.length >= numTiles);
        
        for (int i = 0; i < JavaPuzzle2.ALL_DIRECTIONS; ++i)
        {
            result[i] = JavaPuzzle2.INIT_LOCATION_VALUE;
        }
        
        assert(vacantSlotLogicalIndex >= 0 && vacantSlotLogicalIndex <= numTiles);
        assert(vacantSlotLocation < numTiles);
        
        if (vacantSlotLocation >= numCols)
        {
            result[JavaPuzzle2.PuzzleTileDirection.UP.getIntValue()] = P2Lmap[vacantSlotLocation - numCols];
        }
        if (vacantSlotLocation < numTiles - numCols)
        {
            result[JavaPuzzle2.PuzzleTileDirection.DOWN.getIntValue()] = P2Lmap[vacantSlotLocation + numCols];
        }
        
        if (vacantSlotLocation % numCols != 0)
        {
            result[JavaPuzzle2.PuzzleTileDirection.LEFT.getIntValue()] = P2Lmap[vacantSlotLocation - 1];
        }
        
        if (vacantSlotLocation % numCols != (numCols - 1))
        {
            result[JavaPuzzle2.PuzzleTileDirection.RIGHT.getIntValue()] = P2Lmap[vacantSlotLocation + 1];
        }
    }
    
    
    public int makeMovableTileList(int[] movableTileDirs, int[] movableTiles)
    {
        assert(movableTileDirs !=  null);
        assert(movableTileDirs.length == JavaPuzzle2.ALL_DIRECTIONS);
        assert(movableTiles != null);
        assert(movableTiles.length == movableTileDirs.length);
        
        final int maxIndex = numRows * numCols;
        
        for (int i = 0; i < JavaPuzzle2.ALL_DIRECTIONS; ++i)
        {
            movableTiles[i] = JavaPuzzle2.INIT_LOCATION_VALUE;
        }
        int tilesFound = 0;
        
        for (int p: movableTileDirs)
        {
            if (p != JavaPuzzle2.INIT_LOCATION_VALUE)
            {
                assert(p > 0 && p <= maxIndex);
                assert(p != vacantSlotLogicalIndex);
                movableTiles[tilesFound++] = p;
            }
        }
        return tilesFound;
    }
    
    public int findTileInMovableDirList(int logicalIndex, final int vacantSlotLocation)
    {
      int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];
      int vacantToTileDir = JavaPuzzle2.UNDEFINED_LOCATION;
      
      boolean found = false;
      
      findMovableTileDirs(movableDirList, vacantSlotLocation);
      
      for (JavaPuzzle2.PuzzleTileDirection dir : JavaPuzzle2.PuzzleTileDirection.values())
      {
          if (movableDirList[dir.getIntValue()] == logicalIndex)
          {
              assert(found == false);
              vacantToTileDir = dir.getIntValue();
              found = true;
          }
      }
      
      return vacantToTileDir;
    }
    
    public int findTileInMovableDirList(JavaPuzzle2.PuzzleTileDirection direction, 
                                                    final int vacantSlotLocation)
    {
        int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];
        int vacantToTileDir = JavaPuzzle2.UNDEFINED_LOCATION;
      
        boolean found = false;
      
        findMovableTileDirs(movableDirList, vacantSlotLocation);
        
        int tileIndex = movableDirList[direction.getIntValue()];
        
        return tileIndex;
    }
    

    public Checksum calculatePuzzleStateChecksum()
    {
        Checksum checksum = new PuzzleStringBufferChecksum(numTiles * 2);
        
        for (int i = 0; i < numTiles; ++i)
        {
            checksum.append(P2Lmap[i]);
        }
        
        return checksum;
    }
    
    final protected int getPosAlongRow(int logicalIndex)
    {
        assert(logicalIndex > 0 && logicalIndex <= numTiles);
        final int physicalIndex = L2Pmap[logicalIndex];
        if (physicalIndex == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            return JavaPuzzle2.UNDEFINED_LOCATION;
        }
        assert (numRows != 0);
        
        return getSolutionXPos(physicalIndex);
    }
    
    final protected int getPosDownCol(int logicalIndex)
    {
        assert(logicalIndex > 0 && logicalIndex <= numTiles);
        final int physicalIndex = L2Pmap[logicalIndex];
        if (physicalIndex == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            return JavaPuzzle2.UNDEFINED_LOCATION;
        }
        assert (numRows != 0);
        return getSolutionYPos(physicalIndex);
    }
    
    public int getSolutionYPos(final int physicalIndex)
    {
        return (physicalIndex / numCols);
    }

    public int getSolutionXPos(final int physicalIndex)
    {
        return (physicalIndex % numCols);
    }
    
    /*
    Is the specified Tile adjacent to the vacant slot,
    so that it can swap positions with the vacant slot?
    */
    public boolean PuzzleTileCanMove(int logicalIndex)
    {        
        assert(logicalIndex > 0);
        assert(logicalIndex <= JavaPuzzle2.MAX_TILES);
        if (logicalIndex  == vacantSlotLogicalIndex)
        {
            return false;
        }
        
        if (L2Pmap[logicalIndex] == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            //TODO assert?
            return false;
        }
        
        final int rowPos = getPosAlongRow(logicalIndex);
        final int colPos = getPosDownCol(logicalIndex);
        
        if (rowPos < 0 || colPos < 0)
        {
            return false;
        }
        
        final int vacantSlotRowPos = getPosAlongRow(vacantSlotLogicalIndex);
        final int vacantSlotColPos = getPosDownCol(vacantSlotLogicalIndex);
        
        final int manhattanDistance = Math.abs(rowPos - vacantSlotRowPos) + Math.abs(colPos - vacantSlotColPos);
        
        return (manhattanDistance == 1);
    }
    
    public int calculateCurrentStateCost()
    {
        int cost = 0;
        int tiebreaker = 0;
        for (int i = 0; i < numTiles; ++i)
        {
            int logicalIndex =  i + 1;
            if (logicalIndex == this.vacantSlotLogicalIndex)
            {
                continue;
            }
            final int solutionRowPos = getSolutionXPos(i);
            final int solutionColPos = getSolutionYPos(i);
            final int rowPos = getPosAlongRow(logicalIndex);
            final int colPos = getPosDownCol(logicalIndex);

            final int manhattanDistance = Math.abs(rowPos - solutionRowPos) + Math.abs(colPos - solutionColPos);
            JavaPuzzle2.logger.log(Level.FINEST, "mh li: {0} sr :{1} sc: {2}", new Object[]{logicalIndex, solutionRowPos, solutionColPos});
            JavaPuzzle2.logger.log(Level.FINEST, " r :{0} c: {1}mhDist: {2}", new Object[]{rowPos, colPos, manhattanDistance});
            cost += 10 * manhattanDistance;
            
            //tiebreaker term
            int logicalIndexInSlot =  P2Lmap[i];
            cost += Math.abs(i - logicalIndexInSlot + 1);
            tiebreaker += Math.abs(i - logicalIndexInSlot + 1);
        }
        
        return cost + tiebreaker;
    }
    
    final public int getPhysicalSlotIndex(int[] L2Pmapping, int logicalIndex)
    {
        final int physicalLocation = L2Pmapping[logicalIndex];
        return physicalLocation;
    }
    
    final public int getPhysicalSlotIndex(int logicalIndex)
    {
        return getPhysicalSlotIndex(L2Pmap, logicalIndex);
    }
    
    public void updateState(PuzzleSearchNode nodeWithNewState, int numTiles)
    {
        assert(nodeWithNewState != null);
        int[] newP2LMapping = nodeWithNewState.getState();
        assert(newP2LMapping != null);
        
        assert(P2Lmap.length >= newP2LMapping.length);
        
        System.arraycopy(newP2LMapping, 0, P2Lmap, 0, newP2LMapping.length);
        
        //populate L2
        for (int i = 0; i < numTiles; ++i)
        {
            int logicalIndex = P2Lmap[i];
            updateL2PEntry(L2Pmap, logicalIndex, i);
        }
        
        //debug
        Checksum newCheckSum = this.calculatePuzzleStateChecksum();
        assert(newCheckSum.equals(nodeWithNewState.getChecksum()));
    }
    
    //TODO add support for temp P2L L2P for a prospective move
    protected void updateL2PEntry(int[] L2Pmapping, int logicalIndex, int physicalIndex)
    {
        assert(logicalIndex > 0);
        assert(logicalIndex <= JavaPuzzle2.MAX_TILES);
        assert(physicalIndex >= 0);
        assert(physicalIndex < JavaPuzzle2.MAX_TILES);
        L2Pmapping[logicalIndex] = physicalIndex;
    }
    
    protected void updateL2PEntry(int logicalIndex, int physicalIndex)
    {
        updateL2PEntry(L2Pmap, logicalIndex, physicalIndex);
    }
    
    protected void executeMapChangeForSlide(int[] P2Lmapping, int[] L2Pmapping,
            final int InitialTargetLocation, final int finalTargetSlotPosition, int logicalIndex)
    {
        P2Lmapping[InitialTargetLocation] =  vacantSlotLogicalIndex;
        P2Lmapping[finalTargetSlotPosition] = logicalIndex;
        updateL2PEntry(L2Pmapping, logicalIndex, finalTargetSlotPosition);
        updateL2PEntry(L2Pmapping, vacantSlotLogicalIndex, InitialTargetLocation);
    }
    
    protected void executeMapChangeForSlide( final int InitialTargetLocation, final int finalTargetSlotPosition, int logicalIndex)
    {
        executeMapChangeForSlide(P2Lmap, L2Pmap, InitialTargetLocation, finalTargetSlotPosition, logicalIndex);  
    }
    
    public void executeMapChangeForSlide(int logicalIndex)
    {
        final int InitialTargetLocation = getPhysicalSlotIndex(logicalIndex);
        final int finalTargetSlotPosition = getPhysicalSlotIndex(vacantSlotLogicalIndex);
        
        final boolean canMove = PuzzleTileCanMove(logicalIndex);
        
        assert(canMove);
        
        executeMapChangeForSlide(InitialTargetLocation, finalTargetSlotPosition, logicalIndex);
    }
    
    public int getNumTiles()
    {
        return numTiles;
    }
    
    public int[] getP2LState()
    {
        return P2Lmap;
    }
 
    final private int                   numTiles;
    final private int                   numRows;
    final private int                   numCols;
    private int[]                       P2Lmap;
    final private int[]                 L2Pmap;
    final int                           vacantSlotLogicalIndex;
}
