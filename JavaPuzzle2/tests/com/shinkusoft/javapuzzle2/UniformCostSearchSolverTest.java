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
public class UniformCostSearchSolverTest extends PuzzleTestBase
{
  
    @Before
    public void setUp()
    {
        super.setUp();
        boolean setSolverOK = JavaPuzzle2.setCurrentSolverMethodValue(JavaPuzzle2.PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH);
        assertTrue(setSolverOK);
        assertEquals(JavaPuzzle2.PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH.getIntValue(),
                JavaPuzzle2.getToolsConfigData().getCurrentSolverMethod().getIntValue());
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testSetupFlow()
    {
        assertEquals(JavaPuzzle2.PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH.getIntValue(),
                JavaPuzzle2.getToolsConfigData().getCurrentSolverMethod().getIntValue());
    }
    
    @Test
    public void testUCSSolverOnAlreadySolvedPuzzle()
    {
        sharedCode.testSolverOnAlreadySolvedPuzzle(helper);
    }
    
    @Test
    public void testUCSSolverFourMovesRequired()
    {
        sharedCode.testSolverFourMovesRequired(helper);
    }
    
    @Test
    public void testUCSSolverNineMovesRequired()
    {
        sharedCode.testSolverNineMovesRequired(helper);
    }
    
    @Test
    public void testUCSAttemptShuffleOperationSolution()
    {
        sharedCode.testExecuteSolverOnDefaultShuffleFlow(helper, true);
    }
    
    final private SharedPuzzleTestCode  sharedCode = new SharedPuzzleTestCode();
}
