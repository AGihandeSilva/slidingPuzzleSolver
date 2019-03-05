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
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;


/**
 *
 * @author Gihan
 */
public class UniformCostSearchSolver extends BasePuzzleSolver
{
    UniformCostSearchSolver()
    {
        setMethodDescription(JavaPuzzle2.PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH);
        super.setSolutionState(new PuzzleSolutionState(
                new TreeMap<>(),
                getPuzzle()));
    }

    @Override
    public boolean executeSolverFlow()
    {
        final PuzzleTileSet puzzle = getPuzzle();
        assert(puzzle != null);
        final int numTiles = puzzle.getNumTiles();
        final int vacantSlotLogicalIndex = puzzle.getVacantTileLogicalIndex();

        PuzzleTileSetCalculator calculator = PuzzleTileSetCalculator.buildNewCalculator(puzzle);
        
        PuzzleStateMetrics metrics = new PuzzleStateMetrics(numTiles);
        
        int[] frontierCosts = metrics.getFrontierCosts();
        Checksum[] frontierChecksums = metrics.getFrontierChecksums();
        int[] dirList = metrics.getDirList();
        
        HashSet<Checksum> exploredSet = getExploredSet();
        assert(exploredSet != null);
        exploredSet.clear();
        
        int currentCost = calculator.calculateCurrentStateCost();
        int pathCost = 0;
        Checksum currentChecksum = calculator.calculatePuzzleStateChecksum();
        
        PuzzleSearchNode root = new PuzzleSearchNode(numTiles, null, JavaPuzzle2.PuzzleTileDirection.UP, 
                pathCost, currentCost, currentChecksum);
        
        TreeMap<Integer, LinkedList<PuzzleSearchNode>> priorityQueue = null;
        if (getSolutionState().getSearchTree() instanceof TreeMap)
        {
            priorityQueue = (TreeMap<Integer, LinkedList<PuzzleSearchNode>>)getSolutionState().getSearchTree();
        }
        assert(priorityQueue != null);
        priorityQueue.clear();
        LinkedList<PuzzleSearchNode> newList = new LinkedList<>();
        priorityQueue.put(currentCost, newList);
        newList.add(root);
        exploredSet.add(currentChecksum);

        final int maxMoves =  JavaPuzzle2.getToolsConfigData().getMaxSolverTreeSize();
        int movesDone = 0;
        
        UCSCoster coster  = new UCSCoster();
        PuzzleSearchNode node;
        
        boolean notYetHalted = true;
        
        while (currentCost > 0 && ++movesDone <= maxMoves && notYetHalted)
        {
            if (priorityQueue.isEmpty())
            {
                JavaPuzzle2.logger.fine("Exit: Queue Empty!");
                return false;
            }
            LinkedList<PuzzleSearchNode> nodeList = priorityQueue.firstEntry().getValue();
            node = nodeList.removeFirst(); // fix this!
            assert(node != null);
            if (nodeList.isEmpty())
            {
                List<PuzzleSearchNode> removedList = priorityQueue.remove(node.getStateCost()); 
                assert(removedList == nodeList);
            }
            
            if (node.getParent() != null)
            {
                calculator.updateState(node, numTiles);

                JavaPuzzle2.logger.log(Level.FINER, "*** UCS sliding dir:{0}", node.getActionSlideDir().toString());
                currentCost = calculator.calculateCurrentStateCost();
            }
            
            if (currentCost == 0)
            {
                JavaPuzzle2.logger.fine("Exit: currentCost zero!");
                
                setSolverSucceeded(true);
                if (isGUIflow)
                {
                    this.setMovesDone(getSlideSequenceSize(node));
                    endTimer();
                    generateSolverReport();
                }
                ExecuteSolutionSequence(calculator, node, numTiles);
                assert(getPuzzle().isSolved() || JavaPuzzle2.solverWasHalted());
                break;
            }
            coster.setCurrentCost(currentCost);
            coster.setCalculator(calculator);
            int vacantSlotLocation = calculator.getPhysicalSlotIndex(vacantSlotLogicalIndex);
            if (!collectProblemFrontierCosts(calculator, coster, metrics, vacantSlotLocation))
            {
                JavaPuzzle2.logger.fine("Exit: new frontier empty!");
                return false;
            }
            
            for (int dir = 0; dir < JavaPuzzle2.ALL_DIRECTIONS; ++dir)
            {
                LinkedList<PuzzleSearchNode> list;
                 if (frontierCosts[dir] < 0)
                 {
                     //assert(frontierChecksums[dir] < 0); //TODO initial value
                     continue;
                 }
                 
                PuzzleSearchNode newNode = new PuzzleSearchNode(numTiles, node, JavaPuzzle2.PuzzleTileDirection.getValue(dir),
                            node.getPathCost() + 1, frontierCosts[dir], frontierChecksums[dir]);
                
                newNode.setState(metrics.getP2Lmap(dir));
                 
                if (exploredSet.contains(frontierChecksums[dir]))
                {
                    //check if it's an identical state via a shorter path
                    list = priorityQueue.get(frontierCosts[dir]);
                    if (list != null)
                    {
                        ListIterator<PuzzleSearchNode> iter = list.listIterator();
                        while(iter.hasNext())
                        {
                            PuzzleSearchNode previousNode = iter.next();

                            if (newNode.equals(previousNode) &&
                                    newNode.getPathCost() < previousNode.getPathCost())
                            {
                                assert(frontierChecksums[dir].toString().equals(previousNode.getChecksum().toString()));
                                iter.set(newNode);
                                break;
                            }
                            else if (!newNode.equals(previousNode))
                            {
                                final boolean checksumsDiffer = !newNode.getChecksum().toString().equals(previousNode.getChecksum().toString());
                                assert(checksumsDiffer);
                            }
                        }
                    }
                }
                else
                {
                    //add new state node to queue
                    exploredSet.add(frontierChecksums[dir]);
                    JavaPuzzle2.logger.log(Level.FINER, "*** UCS added tile{0} Cost: {1} dir: {2} chks: {3}", new Object[]{dirList[dir], frontierCosts[dir], JavaPuzzle2.PuzzleTileDirection.getValue(dir).toString(), frontierChecksums[dir]});

                    list = priorityQueue.get(frontierCosts[dir]);
                    if (list == null)
                    {
                       list = new LinkedList<>();
                       priorityQueue.put(frontierCosts[dir], list);
                    }
                    list.add(newNode);
                }
            }
            this.setMovesDone(movesDone);
            currentCost = calculator.calculateCurrentStateCost();
            
            notYetHalted = !super.haltWasRequested();
        }
        setSolverSucceeded(getPuzzle().isSolved());
        setNumSearchNodes(exploredSet.size());
        return(didSolverSucceed());
    }
    
    private ArrayList<JavaPuzzle2.PuzzleTileDirection> getSlideSequence(PuzzleSearchNode node)
    {
        ArrayList<JavaPuzzle2.PuzzleTileDirection> slideSequence = new ArrayList<>();
        
        int pathLength = node.getPathCost();
        
        while(--pathLength >= 0 && node != null)
        {
            JavaPuzzle2.PuzzleTileDirection direction  = node.getActionSlideDir();
            slideSequence.add(direction);
            node = node.getParent();
        }
        
        return slideSequence;
    }
    
    private int getSlideSequenceSize(PuzzleSearchNode node)
    {
        ArrayList<JavaPuzzle2.PuzzleTileDirection> slideSequence = getSlideSequence(node);
        int result =  slideSequence.size();
        slideSequence.clear();
        
        return result;
    }
    
    private void ExecuteSolutionSequence(PuzzleTileSetCalculator calculator, PuzzleSearchNode node, final int numTiles)
    {
        calculator.updateState(node, numTiles);
        int currentCost = calculator.calculateCurrentStateCost();
        assert(currentCost == 0);
        
        int GUImovesDone = 0;
        setMovesDone(GUImovesDone);
        
        solutionPathCost =  node.getPathCost();
        
        ArrayList<JavaPuzzle2.PuzzleTileDirection> slideSequence = getSlideSequence(node);

        while (!slideSequence.isEmpty() && !JavaPuzzle2.solverWasHalted())
        {
            JavaPuzzle2.PuzzleTileDirection direction  = slideSequence.get(slideSequence.size() - 1);
            slideSequence.remove(slideSequence.size() - 1);
            boolean slid = getPuzzle().PuzzleTileSlide(direction, true);
            assert(slid);
            ++GUImovesDone;
            
            if (isGUIflow)
            {
                JavaPuzzle2.addDelay(GUIdelayInNANOs); //TODO expose this as a GUI parameter
                getPuzzle().updateDynamicSolverInfo();
            }
        }
        
        refreshPuzzleSolverState();
        if (isGUIflow)
        {
            //TODO calling this again seems inelegant
            getPuzzle().updateDynamicSolverInfo();
        }
        
        setMovesDone(GUImovesDone);
    }
    
    @Override
    public int getMovesDone()
    {
        if (getResultReport() == null)
        {
            return super.getMovesDone();
        }
        else
        {
            return getResultReport().numMoves;
        }
    }
    
    private int solutionPathCost = 0;
    
    final private boolean isGUIflow = JavaPuzzle2.isUsingGUIflow();
    
    private final long GUIdelayInNANOs = 200000000;

    private class UCSCoster implements CostMeasurer
    {
        public void setCurrentCost(int currentCost)
        {
            this.currentCost = currentCost;
        }

        
        @Override
        public void calculateMetrics(PuzzleStateMetrics metrics, int logicalIndex, int dirAsInt)
        {
            assert(calculator !=  null);
            //try slide move (map only)
            calculator.executeMapChangeForSlide(logicalIndex);
            measuredCost = calculator.calculateCurrentStateCost(); //TODO count the path length to this point (parent length + 1)
            //assert (currentCost != measuredCost); //not necessarily true, even with tiebreaker?
            
            int[] P2LmapRecord = metrics.getP2Lmap(dirAsInt);

            System.arraycopy(calculator.getP2LState(), 0, P2LmapRecord, 0, P2LmapRecord.length);
            
            measuredChecksum = calculator.calculatePuzzleStateChecksum();
//          //slide it back //TODO optimize this!
            calculator.executeMapChangeForSlide(logicalIndex);
            int revertCost = calculator.calculateCurrentStateCost();
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
            return measuredChecksum;
        }
        
        public void setCalculator(PuzzleTileSetCalculator calculator)
        {
            this.calculator = calculator;
        }
         
        
        private int currentCost;
        private int measuredCost;
        private Checksum measuredChecksum;
        private PuzzleTileSetCalculator calculator;

    } 
}
