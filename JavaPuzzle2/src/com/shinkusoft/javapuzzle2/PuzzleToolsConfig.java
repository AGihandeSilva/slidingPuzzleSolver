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

import com.shinkusoft.javapuzzle2.JavaPuzzle2.PuzzleToolsSolverMethod;

/**
 *
 * @author Gihan
 */
public class PuzzleToolsConfig
{
    public PuzzleToolsConfig() {}
    
    public PuzzleToolsConfig(final PuzzleToolsSolverMethod method, final int maxSolverMoves, final boolean solverUpdatesGUI)
    {
        this.method = method;
        this.maxSolverTreeSize = maxSolverMoves;
        this.solverUpdatesGUI = solverUpdatesGUI;
    }
    
    public boolean getSolverUpdatesGUI()
    {
        return this.solverUpdatesGUI;
    }
    
    public void setSolverUpdatesGUI(final boolean solverUpdatesGUI)
    {
        this.solverUpdatesGUI = solverUpdatesGUI;
    }
    
    public int getMaxSolverTreeSize()
    {
        return this.maxSolverTreeSize;
    }
    
    public void setMaxSolverTreeSize(final int maxSolverTreeSize)
    {
        this.maxSolverTreeSize = maxSolverTreeSize;
    }
    
    public PuzzleToolsSolverMethod getCurrentSolverMethod()
    {
        return this.method;
    }
    
    public void setCurrentSolverMethod(final PuzzleToolsSolverMethod method)
    {
        this.method = method;
    }
    
    private boolean solverUpdatesGUI = DEFAULT_SOLVER_UPDATES_GUI_SETTING;
    private int     maxSolverTreeSize = DEFAULT_MAX_SOLVER_TREE_SIZE;
    private PuzzleToolsSolverMethod method = DEFAULT_SOLVER_METHOD;
            
    final private static PuzzleToolsSolverMethod DEFAULT_SOLVER_METHOD = PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH;
    final private static boolean DEFAULT_SOLVER_UPDATES_GUI_SETTING = true;
    final private static int DEFAULT_MAX_SOLVER_TREE_SIZE = 10000;
}

