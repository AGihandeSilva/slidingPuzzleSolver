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
import com.shinkusoft.javapuzzle2.JavaPuzzle2.PuzzleSolver;
import java.util.Arrays;
import java.util.HashSet;
import com.shinkusoft.javapuzzle2.JavaPuzzle2.ResultObserver;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author Gihan
 */
abstract public class BasePuzzleSolver implements PuzzleSolver, Runnable 
{

    public interface CostMeasurer
    {
        public void calculateMetrics(PuzzleStateMetrics metrics, final int logicalIndex, final int dirAsInt);
        public int getCost();
        public Checksum getChecksum();
    }
    
    @Override
    final public JavaPuzzle2.PuzzleToolsSolverMethod GetMethodDescription()
    {
        return methodDescription;
    }
    
    final public void setMethodDescription(JavaPuzzle2.PuzzleToolsSolverMethod methodDescription)
    {
        this.methodDescription = methodDescription;
    }
    
    @Override
    public boolean Solve()
    {
        invalidateResultReport();
        this.solverHaltRequested = false;
        state = JavaPuzzle2.SolverState.RUNNING;
        getPuzzle().updateDynamicSolverInfo();
        Thread t = new Thread(this);
        t.start();
        return solverSucceeded;
    }
    
    @Override
    public void run()
    {      
        reportGenerated = false;
        setSolverActive(true);
        startTimer();
        executeSolverFlow();
        if (!reportGenerated)
        {
            endTimer();
        }

        setSolverActive(false);
        
        generateSolverReport();
        puzzle.updateDynamicSolverInfo();
    }
    
    public void startTimer()
    {
        elapsedCentisecTime = 0;
        guiCentisecTimer = new Timer();
        guiCentisecTimer.scheduleAtFixedRate(new TimerTask() {
            
            @Override
            public void run() {
                elapsedCentisecTime += 0.01;
                puzzle.updateInProgressValues(elapsedCentisecTime, movesDone);
            }
        }, 0, centisecIntervalInMS);
        
        startTimeInNANOs = System.nanoTime();
    }
    
    public void endTimer()
    {
        final double nanosInOneSecond = Math.pow(10.0f, 9.0f);
        endTimeinNANOs = System.nanoTime();
        timeTakenInSeconds = (endTimeinNANOs - startTimeInNANOs) / nanosInOneSecond;
        guiCentisecTimer.cancel();
    }

    public void generateSolverReport()
    {
        state = solverHaltRequested ? JavaPuzzle2.SolverState.HALTED : 
                            solverSucceeded || (puzzle.calculateCurrentStateCost() == 0) ? 
                                                JavaPuzzle2.SolverState.SOLVED : JavaPuzzle2.SolverState.FAILED;
                        
        resultReport = new PuzzleSolverResult(solverSucceeded, movesDone, numSearchNodes, timeTakenInSeconds);
        if (successObserver != null && !reportGenerated)
        {
            successObserver.generateResultResponse(resultReport);
            reportGenerated = true;
        }
    }
    
    abstract boolean executeSolverFlow();
    
    final public void setPuzzle(PuzzleTileSet puzzle)
    {
        this.puzzle = puzzle;
    }
    
    final public PuzzleTileSet getPuzzle()
    {
        return puzzle;
    }
    
    public PuzzleSolutionState getSolutionState()
    {
        return solutionState;
    }

    public void setSolutionState(PuzzleSolutionState solutionState)
    {
        this.solutionState = solutionState;
    }
    
    private boolean computeFrontierCosts(CostMeasurer costFinder, PuzzleStateMetrics metrics)
    {
        boolean found = false;
        int[] frontierCosts = metrics.getFrontierCosts();
        Checksum[] frontierChecksums = metrics.getFrontierChecksums();
        int[] dirList = metrics.getDirList();
         
        //TODO fix initialization
//        if (frontierChecksums != null)
//        {
//            Arrays.fill(frontierChecksums, JavaPuzzle2.UNSPECIFIED_VALUE);
//        }
        
        for (int dir = 0; dir < JavaPuzzle2.ALL_DIRECTIONS; ++dir)
        {
            final int tileLogicalIndex = dirList[dir]; //wasTileList
            if (tileLogicalIndex != JavaPuzzle2.INIT_LOCATION_VALUE) //TODO revise this
            {
                found = true;
                costFinder.calculateMetrics(metrics, tileLogicalIndex, dir);
                frontierCosts[dir] = costFinder.getCost();
                if (frontierChecksums != null)
                {
                    frontierChecksums[dir] = costFinder.getChecksum();
                }
            }
        }
        return found;
    }
    
    public boolean collectProblemFrontierCosts(PuzzleTileSetCalculator calculator, CostMeasurer costFinder, PuzzleStateMetrics metrics, final int vacantSlotLocation)
    {
        int[] frontierCosts = metrics.getFrontierCosts();
        int[] dirList = metrics.getDirList();
        int[] tileList = metrics.getTileList();
        
        boolean found = false;
        Arrays.fill(frontierCosts, JavaPuzzle2.UNDEFINED_LOCATION);
        calculator.findMovableTileDirs(dirList, vacantSlotLocation);
        calculator.makeMovableTileList(dirList, tileList);
        
        return computeFrontierCosts(costFinder, metrics);
    }
    
    public boolean collectProblemFrontierCosts(CostMeasurer costFinder, PuzzleStateMetrics metrics)
    {
        int[] frontierCosts = metrics.getFrontierCosts();
        int[] dirList = metrics.getDirList();
        int[] tileList = metrics.getTileList();
        
        boolean found = false;
        Arrays.fill(frontierCosts, JavaPuzzle2.UNDEFINED_LOCATION);
        getPuzzle().findMovableTileDirs(dirList);
        getPuzzle().makeMovableTileList(dirList, tileList);
        
        return computeFrontierCosts(costFinder, metrics);
    }
    
    public void slideTile(int tileToMove)
    {
        getPuzzle().PuzzleTileSlide(tileToMove, true);
        //TODO add this as an option
        //if (add to history option)
        {
            getPuzzle().getHistory().saveEditClearRedoList();
        }
    }
    
    @Override
    public boolean isSolverActive()
    {
        return solverActive;
    }

    final public void setSolverActive(boolean solverActive)
    {
        this.solverActive = solverActive;
        assert(puzzle != null);
        puzzle.setPuzzleUnderSolverOperations(solverActive);
    }

    public boolean didSolverSucceed()
    {
        return solverSucceeded;
    }

    public void setSolverSucceeded(boolean solverSucceeded)
    {
        this.solverSucceeded = solverSucceeded;
    }
    
    public void refreshPuzzleSolverState()
    {
        assert(puzzle != null);
        
        if (state != JavaPuzzle2.SolverState.HALTED)
        {
            if (puzzle.isSolved()) {
                state = JavaPuzzle2.SolverState.SOLVED;
            }
        }
    }
    
    void setResultListener(ResultObserver observer)
    {
        this.successObserver = observer;
    }

    public HashSet<Checksum> getExploredSet()
    {
        return exploredSet;
    }
    
    private void invalidateResultReport()
    {
       resultReport = null; 
    }

    @Override
    public PuzzleSolverResult getResultReport()
    {
        return resultReport;
    }

    public void setResultReport(PuzzleSolverResult resultReport)
    {
        this.resultReport = resultReport;
    }
    
    @Override
    public int getMovesDone()
    {
        return movesDone;
    }

    public void setMovesDone(int movesDone)
    {
        this.movesDone = movesDone;
    }

    public int getNumSearchNodes()
    {
        return numSearchNodes;
    }

    public void setNumSearchNodes(int numSearchNodes)
    {
        this.numSearchNodes = numSearchNodes;
    }
    
    public void requestSolverHalt()
    {
        solverHaltRequested = true;
    }
    
    @Override
    public boolean haltWasRequested()
    {
        return solverHaltRequested;
    }
    
    public JavaPuzzle2.SolverState getState()
    {
        return state;
    }
    
    public void clearSolverState()
    {
        if (state != JavaPuzzle2.SolverState.HALTED)
        {
            state = JavaPuzzle2.SolverState.IDLE;
        }
    }
    
    public boolean isSolverIdle()
    {
        return (this.state == JavaPuzzle2.SolverState.IDLE);
    }

    private PuzzleTileSet puzzle;
    private JavaPuzzle2.PuzzleToolsSolverMethod methodDescription;
    private PuzzleSolutionState  solutionState;
    final private HashSet<Checksum> exploredSet = new HashSet<>();
    
    private ResultObserver successObserver = null;
    
    private boolean solverHaltRequested = false;
    
    private JavaPuzzle2.SolverState state = JavaPuzzle2.SolverState.IDLE;
    
    private boolean solverActive = false;
    private boolean solverSucceeded = false;
    private PuzzleSolverResult resultReport;
    private int movesDone = 0;
    private int numSearchNodes = 0;
    private boolean reportGenerated = false;
    private double timeTakenInSeconds = 0;
    private long startTimeInNANOs;
    private long endTimeinNANOs;
    
    private Timer guiCentisecTimer;
    final private int centisecIntervalInMS = 10;
    private double elapsedCentisecTime;

}
