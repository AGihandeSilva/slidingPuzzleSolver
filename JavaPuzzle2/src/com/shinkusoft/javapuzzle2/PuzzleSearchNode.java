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

/**
 *
 * @author Gihan
 */
public class PuzzleSearchNode
{
    PuzzleSearchNode(int numTiles, PuzzleSearchNode parent, JavaPuzzle2.PuzzleTileDirection slideDir, int pathCost, int stateCost, 
            Checksum checksum)
    {
        state = new int[numTiles];
        this.parent = parent;
        actionSlideDir = slideDir;
        this.pathCost = pathCost;
        this.stateCost = stateCost;
        this.checksum = checksum;
    }

    public int[] getState()
    {
        return state;
    }
    
    public void setState(int[] newState)
    {
        assert(newState != null);
        assert(state.length >= newState.length);
        System.arraycopy(newState, 0, state, 0, newState.length);
    }

    public PuzzleSearchNode getParent()
    {
        return parent;
    }


    public JavaPuzzle2.PuzzleTileDirection getActionSlideDir()
    {
        return actionSlideDir;
    }


    public int getPathCost()
    {
        return pathCost;
    }

    public Checksum getChecksum()
    {
        return checksum;
    }
    

    public int getStateCost()
    {
        return stateCost;
    }
    
    public boolean equals(final PuzzleSearchNode otherNode)
    {
        boolean isEquivalent = true;
        
        if (stateCost != otherNode.stateCost)
        {
            isEquivalent = false;
        }
        
        for (int i = 0 ; isEquivalent && (i < state.length); ++i)
        {
            if (this.state[i] != otherNode.state[i])
            {
                isEquivalent = false;
            }
        }
        
        return isEquivalent;
    }

    public void setStateCost(int stateCost)
    {
        this.stateCost = stateCost;
    }

    public void setChecksum(Checksum checksum)
    {
        this.checksum = checksum;
    }
    
    

    final private int[] state;
    final private PuzzleSearchNode parent;
    final private JavaPuzzle2.PuzzleTileDirection actionSlideDir;
    final private int pathCost;
    private int stateCost;

    private Checksum checksum;


}
