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
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class GreedyPuzzleSolver extends BasePuzzleSolver
{   
    GreedyPuzzleSolver()
    {
        setMethodDescription(JavaPuzzle2.PuzzleToolsSolverMethod.PURE_GREEDY_SEARCH);
        super.setSolutionState(new PuzzleSolutionState(
                null,
                getPuzzle()));
    }  

    @Override
    public boolean executeSolverFlow()
    {
        assert(getPuzzle() != null);
        
        PuzzleStateMetrics metrics = new PuzzleStateMetrics(getPuzzle().getNumTiles());
        
        int[] frontierCosts = metrics.getFrontierCosts();
        int[] dirList = metrics.getDirList();

        final int maxMoves =  JavaPuzzle2.getToolsConfigData().getMaxSolverTreeSize();
        
        int currentCost = getPuzzle().calculateCurrentStateCost();
        int moves = 0;
        
        GreedyCoster coster  = new GreedyCoster();
        
        while (currentCost > 0 && ++moves <= maxMoves )
        {
            coster.setCurrentCost(currentCost);
            collectProblemFrontierCosts(coster, metrics);

            int minCost = currentCost;
            int tileToMove = JavaPuzzle2.UNDEFINED_LOCATION;
            int moveDir = JavaPuzzle2.UNSPECIFIED_VALUE;

            for (int dir = 0; dir < JavaPuzzle2.ALL_DIRECTIONS; ++dir)
            {
                if (frontierCosts[dir] >= 0 && frontierCosts[dir] <= minCost)
                {
                    tileToMove = dirList[dir];
                    assert (tileToMove != JavaPuzzle2.INIT_LOCATION_VALUE);
                    minCost = frontierCosts[dir];
                    moveDir = dir;
                }
            }
            
            JavaPuzzle2.logger.log(Level.FINE, "Next move: Initial cost: {0} moving Tile: {1} Dir: {2} new cost: {3}",
             new Integer[]{currentCost, tileToMove, moveDir, minCost});
            
//            JavaPuzzle2.logger.fine("Next move: Initial cost: " + currentCost + " moving Tile: " + tileToMove +
//                                            "Dir: " + moveDir + " new cost: " + minCost);

            if (tileToMove != JavaPuzzle2.UNDEFINED_LOCATION)
            {
                slideTile(tileToMove);
            }
            else
            {
                generateResults(moves);
                return false;
            }

            currentCost = getPuzzle().calculateCurrentStateCost();
            assert(currentCost == minCost);
            
            if (super.haltWasRequested()) {
                return false;
            }
        }
        setNumSearchNodes(this.getMovesDone());
        generateResults(moves);
        
        return(didSolverSucceed());
    }
    
    private void generateResults(int moves)
    {
        setMovesDone(moves);
        setSolverSucceeded(getPuzzle().isSolved());
        endTimer();
        if (JavaPuzzle2.isUsingGUIflow())
        {
            generateSolverReport();
        }
        getPuzzle().updateDynamicSolverInfo();
    }

    private class GreedyCoster implements CostMeasurer
    {
        @Override
        public void calculateMetrics(PuzzleStateMetrics metrics, int logicalIndex, int dirAsInt)
        {
            //try slide move (map only)
            getPuzzle().calculator.executeMapChangeForSlide(logicalIndex);
            measuredCost = getPuzzle().calculateCurrentStateCost();
            assert (currentCost != measuredCost);
            //slide it back
            getPuzzle().calculator.executeMapChangeForSlide(logicalIndex);
            int revertCost = getPuzzle().calculateCurrentStateCost();
            assert (currentCost == revertCost);
        }

        @Override
        public int getCost()
        {
            return measuredCost;
        }
        
        
        @Override
        public Checksum getChecksum()
        {
            //not needed for greedy flow
            return null;
        }

        public void setCurrentCost(int currentCost)
        {
            this.currentCost = currentCost;
        }
                
        private int currentCost;
        
        private int measuredCost = JavaPuzzle2.UNSPECIFIED_VALUE;
    }  
}
