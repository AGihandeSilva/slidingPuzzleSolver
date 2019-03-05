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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Gihan
 */
public class PuzzleTileSet {
    
    public PuzzleTileSet(PuzzleConfigData puzzleConfig, PuzzleFrame frame, String puzzleName)
    {
        state = new PuzzleState(puzzleConfig);
        this.numRows        = puzzleConfig.numRows;
        assert(state.getConfig().numRows == this.numRows);
        this.numCols        = puzzleConfig.numCols;
        assert(state.getConfig().numCols == this.numCols);
        this.bitmapFilename = puzzleConfig.bitmapFilename;
        this.frame          = frame;
        this.puzzleName     = puzzleName;
        this.importMode     = puzzleConfig.importMode;
        
        this.puzzleUnderSolverOperations = false;
        
        random = new Random(RANDOM_SEED);
        
        this.bitmapImported = false; //set this later if image creation is successful
        
        int TileIndex = 1;
        
        puzzleTiles = new  TreeMap<>();
        
        locationOfPhysicalSlot = new  Rectangle2D.Double[JavaPuzzle2.MAX_TILES];

        P2LMapping = state.getP2LMapping();
        assert(P2LMapping != null);
        
        L2PMapping = new int[P2LMapping.length + 1]; //zero offset -> non-zero indices

        //debug
        dots = new ArrayList<>();
        
        final int initVal = JavaPuzzle2.INIT_LOCATION_VALUE;
        
        for (int i = 0; i < P2LMapping.length; ++i)
        {
            locationOfPhysicalSlot[i] = new Rectangle2D.Double(initVal, initVal, initVal, initVal);
            P2LMapping[i] = JavaPuzzle2.INIT_LOCATION_VALUE;
        }

        vacantSlotLogicalIndex = numRows * numCols;
        this.calculator = new PuzzleTileSetCalculator(P2LMapping, L2PMapping, numRows, numCols, vacantSlotLogicalIndex);
        
        //try to scale tiles to match the screen and puzzle sizes
        tileSeparation = calculateTileSeparation();
        tileSize       = tileSeparation - DEFAULT_TILE_GROOVE_WIDTH;
        tileScalingFactor = (float)tileSeparation / (float)DEFAULT_TILE_SEPARATION;
        //textOffset = (int)((float)DEFAULT_TEXT_OFFSET * tileScalingFactor);
        //JavaPuzzle2.logger.fine("recalculated Text Offset: " + textOffset);
        //not scaling the small offset for multi-digit tile labels seems to work best....
        textOffset = DEFAULT_TEXT_OFFSET;
        
        for (int row = 0; row < numRows; ++row)
        {
            for (int col = 0; col < numCols; ++col)
            {
                boolean isVacant = false;
                if (TileIndex == vacantSlotLogicalIndex)
                {
                    isVacant = true;
                }
                PuzzleTile tile = new PuzzleTile(TileIndex, this, isVacant);

                PuzzleTile oldVal = puzzleTiles.putIfAbsent(TileIndex, tile);
                assert(oldVal == null);
    
                createPhysicalLocationMap(TileIndex);
                ++TileIndex;
            }
        }
        
        if (puzzleConfig.useBitmap)
        {
            importBitmap(bitmapFilename);
        }

        this.history = new PuzzleTileSetHistory(this, getStateCopy());
        propagateStateChange();
        assert(puzzleIsSolved == true); //this may need changing depending on load/save flow
    }

    private int calculateTileSeparation()
    {
        final float puzzleScalingFactor = 1.75f;
        int separation = DEFAULT_TILE_SEPARATION;
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension displaySize = kit.getScreenSize();
        int displayWidth = displaySize.width;
        int displayHeight = displaySize.height;
        JavaPuzzle2.logger.log(Level.FINE, "Display Width: {0} Display Height: {1}", new Object[]{displayWidth, displayHeight});
        
        JavaPuzzle2.logger.log(Level.FINE, "Original Tile separation {0}", separation);
        
        final float xRatio = ((DEFAULT_X_OFFSET + (separation * numCols)) * puzzleScalingFactor) / displayWidth;
        final float yRatio = ((DEFAULT_Y_OFFSET + (separation * numRows)) * puzzleScalingFactor) / displayHeight;
        
        JavaPuzzle2.logger.log(Level.FINE, "xRatio: {0} yRatio: {1}", new Object[]{xRatio, yRatio});
        
        if (xRatio >= 1.0f || yRatio >= 1.0f)
        {
            separation /=  Math.max(xRatio, yRatio);
            JavaPuzzle2.logger.log(Level.FINE, "Recalculated Tile separation {0}", separation);
        }
        
        return separation;
    }
    
    final public PuzzleState getStateCopy()
    {
        PuzzleState newCopy = new PuzzleState(state.getConfig());
        assert(newCopy.P2LMapping !=  null);
        newCopy.P2LMapping = Arrays.copyOf(P2LMapping, P2LMapping.length);
        
        return newCopy;
    }
    
    public PuzzleTileSetHistory getHistory()
    {
        return history;
    }
    
    public boolean Solve()
    {
        boolean result = false;
        
        try
        {
            result = JavaPuzzle2.Solve(this, frame);
        }
        
        catch (java.lang.OutOfMemoryError e)
        {
            JOptionPane.showMessageDialog(frame,
                    "The solver ran out of memory",
                    e.getMessage(),
                    JOptionPane.INFORMATION_MESSAGE);
            
            result = false;
        }
        return result;
    }
    
    public boolean isSolved()
    {
        return this.puzzleIsSolved;
    }
    
    public void restoreState(PuzzleState restoreState)
    {
        if (state == restoreState)
        {
            return;
        }
        assert(restoreState != null);
        assert(state != null);
        assert((restoreState.config.bitmapFilename == null && state.config.bitmapFilename == null)
                || restoreState.config.bitmapFilename.equals(state.config.bitmapFilename));
        assert(restoreState.config.useBitmap == state.config.useBitmap);
        assert(restoreState.config.importMode == state.config.importMode);
        assert(restoreState.config.numCols ==  state.config.numCols);
        assert(restoreState.config.numRows == state.config.numRows);
        
        assert(restoreState.P2LMapping != null);
        assert(state.P2LMapping != null);
        
        assert(restoreState.P2LMapping.length == state.P2LMapping.length);
        
        state.P2LMapping = Arrays.copyOf(restoreState.P2LMapping, restoreState.P2LMapping.length);
        switchToNewMap();
        
        rebuildTileLocationsFromP2L();
        //TODO use a partial redraw if possible
        forceFullRedraw();
    }
    
    private void switchToNewMap()
    {
        P2LMapping = state.P2LMapping;
        calculator.PuzzleTileSetSwitchCalculatorMaps(P2LMapping);
    }
    
    public void resetPuzzle()
    {
        history.reset();
    }
    
    public void executeUndo()
    {
        history.undo();
    }
    
    public void executeRedo()
    {
        history.redo();
    }
    
    final public void propagateStateChange()
    {
        assert(P2LMapping != null);
        final int numberOfTiles = numCols * numRows;
        
        puzzleIsSolved = true;
        
        for (int i  = 0; i < numberOfTiles && puzzleIsSolved; ++i)
        {
            //physical locations are zero offset, logical indices start at 1
            if (P2LMapping[i] != i + 1)
            {
                puzzleIsSolved = false;
            }
        }
        
        if (!puzzleIsSolved)
        {
            assert(history.getLength() > 1);     
        }

        calculateCurrentStateCost();
        calculatePuzzleStateChecksum();
        
        updateGUI();
    }

    private void updateGUI()
    {
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
            return;
        }
        
        frame.updatePuzzleRelatedInfo(this);
    }
    
    //TODO relocate this?
    public void updateDynamicSolverInfo()
    {
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
            return;
        }
        frame.updateSolverRelatedInfo(PuzzleFrame.SolverInfoDialogUpdateMode.SOLVER_MOVES);
        frame.updateSolverRelatedInfo(PuzzleFrame.SolverInfoDialogUpdateMode.SOLVER_STATE);
    }
    
    public Checksum calculatePuzzleStateChecksum()
    {
        return calculator.calculatePuzzleStateChecksum();
    }
    
    public int calculateCurrentStateCost()
    {
        currentCost = calculator.calculateCurrentStateCost();
        return currentCost;
    }
    public int getCurrentSolutionCost()
    {
        return currentCost;
    }
    
    public Point2D.Double getRowColData()
    {
        return new Point2D.Double(numRows, numCols);
    }
    
    public Rectangle2D.Double getTileLocation(int logicalIndex)
    {
        Rectangle2D.Double location = null;
        PuzzleTile puzzleTile = getPuzzleTile(logicalIndex);
        
        if (puzzleTile != null)
        {
            final int index = puzzleTile.GetPhysicalSlotIndex();
            location = locationOfPhysicalSlot[index];
        }
        
        return location;
    }
    
    public Rectangle2D.Double getPlotAreaData()
    {
        return new Rectangle2D.Double(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET,
        numCols * tileSeparation, numRows * tileSeparation);
    }
    
    public void setBitmap(int logicalIndex, BufferedImage tileImage)
    {
       PuzzleTile targetTile =  getPuzzleTile(logicalIndex);
       
       if (targetTile != null)
       {
           targetTile.setBitmap(tileImage);
       }
       //else...?
    }
    
    final public void importBitmap(final String bitmapFilename)
    {
        PuzzleTileBitmapImporter importer = new PuzzleTileBitmapImporter();
        importer.importBitmap(state.getConfig(), this, bitmapFilename);
    }
    
    public void registerImportedBitmap()
    {
        assert(bitmapImported == false);
        bitmapImported = true;
    }
    
    protected boolean getBitmapImported()
    {
        return bitmapImported;
    }
    
    public void paintAllTiles(Graphics g)
    {
        assert(g != null);
        redrawTiles(g, JavaPuzzle2.UNDEFINED_LOCATION, false);
    }
    
    public void redrawTiles(Graphics g, int logicalTargetIndex, final boolean partialRedraw)
    {
        JavaPuzzle2.logger.entering("PuzzleTileSet", "paintTiles");
        JavaPuzzle2.logger.log(Level.FINER, "PartialRedraw: {0}", partialRedraw);
        
        assert(g != null);
        Font f = new Font("Sans Serif", Font.BOLD, DEFAULT_FONT_SIZE);
        g.setFont(f);
        Graphics2D g2 = (Graphics2D) (g);
        
        //make the default tile index rendering smoother
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        setPuzzleBackgroundAndScale(g2, partialRedraw);
        
        final Color defaultTileColour = new Color(0.1F, 0.3F, 0.7F);
        
        for (PuzzleTile p : puzzleTiles.values())
        {
            Rectangle2D.Double location = p.GetLocation();
            assert(location != null);

            if (p.isVacant())
            {
                g2.setPaint(Color.BLACK);
                g2.fill(p.GetLocation());
                continue;
            }
            
            final int logicalIndex =  p.GetLogicalIndex();
            
            if (partialRedraw &&
                logicalIndex != logicalTargetIndex)
            {
                continue;
            }
            
            final int xCorrectionForStringLength = textOffset * (1 + (int)Math.log10((double)p.GetLogicalIndex()));
            int xpos = (int)location.getCenterX() - xCorrectionForStringLength;
            int ypos = (int)location.getCenterY() + textOffset;
            
            BufferedImage tileBitmap = getBitmap(logicalIndex);
            
            if (tileBitmap == null)
            {
                g2.setPaint(defaultTileColour);
                g2.fill(location);
                g2.setPaint(Color.lightGray);
                
                
                String indexString = String.format("%d", logicalIndex);
                g.drawString (indexString, xpos, ypos);
            }
            else
            {
                g.drawImage(tileBitmap, (int)location.getX(), (int)location.getY(), null);
            }
        }
        
        //debug
        if (JavaPuzzle2.DEBUG_DRAW_DOTS_AT_PRESS_POS)
        {
            drawDots(g);
        }
    }
    
    private void setPuzzleBackgroundAndScale(Graphics2D g2, boolean partialRedraw)
    {
        final float frameScalingFactor = 1.5f;
        final Rectangle2D.Double background = new Rectangle2D.Double(
                DEFAULT_X_OFFSET - DEFAULT_TILE_GROOVE_WIDTH,
                DEFAULT_Y_OFFSET - DEFAULT_TILE_GROOVE_WIDTH,
                (tileSeparation * numCols) + DEFAULT_TILE_GROOVE_WIDTH,
                (tileSeparation * numRows) + DEFAULT_TILE_GROOVE_WIDTH);
        
        if (!partialRedraw && g2 != null)
        {
            final Color grooveColour = new Color(0.2F, 0.2F, 0.2F);
            g2.setPaint(grooveColour);
            g2.fill(background);
        }

        if (frame !=  null)
        {
            frame.resizeForPuzzle(background, frameScalingFactor);
        }
    }
    
    public BufferedImage setSourcePicture(String filename)
    {
        try
        {
            BufferedImage i = ImageIO.read(new File(filename));
            srcPicture = i;
        }
        
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return srcPicture;
    }
    
    final public String getName()
    {
        return this.puzzleName;
    }
    
    final public String getSourceDescription()
    {
        final String defaultSourceDesc = "<numbers>";
        String result;
        
        if (!bitmapImported)
        {
            return defaultSourceDesc;
        }
        else
        {
            result = "<bitmap> " + importMode.getDescriptor() + " source: " + this.bitmapFilename;
        }
        
        return result;
    }
    
    final public PuzzleTile getPuzzleTile(int logicalIndex)
    {
        PuzzleTile result = puzzleTiles.get(logicalIndex);
        return result;
    }
    
    final public Rectangle2D.Double getPhysicalLocation(int physicalIndex)
    {
        assert(physicalIndex >= 0 || physicalIndex == JavaPuzzle2.UNDEFINED_LOCATION);
        assert(physicalIndex <= JavaPuzzle2.MAX_TILES);
        
        if (physicalIndex == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            final int initVal = JavaPuzzle2.INIT_LOCATION_VALUE;
            return new Rectangle2D.Double(initVal, initVal, initVal, initVal);
        }
        
        return locationOfPhysicalSlot[physicalIndex];
    }
    

    
    /*
    Is the specified Tile adjacent to the vacant slot,
    so that it can swap positions with the vacant slot?
    */
    public boolean PuzzleTileCanMove(int logicalIndex)
    {        
        return calculator.PuzzleTileCanMove(logicalIndex);
    }
    
    public void ExecuteShuffleFlow()
    {
        ShuffleMoveTiles(100, true, false); //TODO: allow user to change settings
        
        //TODO add the individual moves to History (or not, as set by user)
        getHistory().saveEditClearRedoList();
        propagateStateChange();
    }
    
    //TODO sort out heirarchy of boolean arguments here
    private void ShuffleMoveTiles(final int numberOfMoves, final boolean displayMoves, final boolean doAnimatedSlide)
    {
        int[] movableDirList = new int[JavaPuzzle2.ALL_DIRECTIONS];
        int[] movableTiles = new int[JavaPuzzle2.ALL_DIRECTIONS];
        
        int numMoves = 0;
        
        while (numMoves++ < numberOfMoves)
        {
            final int vacantSlotLocation = getPhysicalSlotIndex(vacantSlotLogicalIndex);
            calculator.findMovableTileDirs(movableDirList, vacantSlotLocation);
            
            final int tilesFound = calculator.makeMovableTileList(movableDirList, movableTiles);
            
            int tileListIndex = random.nextInt(tilesFound);
            assert(tileListIndex >= 0 && tileListIndex < tilesFound);
            
            JavaPuzzle2.logger.log(Level.FINER, "Random dirAsInt value:{0}", tileListIndex);
            
            //for debug information only
            JavaPuzzle2.PuzzleTileDirection randomDir = JavaPuzzle2.PuzzleTileDirection.getValue(tileListIndex);
            
            int logicalIndex = movableTiles[tileListIndex];
            assert(logicalIndex != JavaPuzzle2.UNDEFINED_LOCATION);
            PuzzleTileSlide(logicalIndex, doAnimatedSlide);

            //TODO add this as an option
            //if (add to history option)
            {
                history.saveEditClearRedoList();
            }
        }
    }

    final public void findMovableTileDirs(int[] result)
    {
        final int vacantSlotLocation = getPhysicalSlotIndex(vacantSlotLogicalIndex);
        calculator.findMovableTileDirs(result, vacantSlotLocation);
    }
    
    public int makeMovableTileList(int[] movableTileDirs, int[] movableTiles)
    {
        return calculator.makeMovableTileList(movableTileDirs, movableTiles);
    }

    public boolean PuzzleTileSlide(final JavaPuzzle2.PuzzleTileDirection direction, boolean doAnimatedSlide)
    {
        final int vacantSlotLocation = getPhysicalSlotIndex(vacantSlotLogicalIndex);
        final int logicalIndex = calculator.findTileInMovableDirList(direction, vacantSlotLocation);
        if (logicalIndex != JavaPuzzle2.INIT_LOCATION_VALUE)
        {
            executeSlide(direction.getIntValue(), logicalIndex, doAnimatedSlide);
            history.saveEditClearRedoList();
            return true;
        }
        
        return false;
    }
    
    public boolean PuzzleTileSlide(int logicalIndex, boolean doAnimatedSlide)
    {
        //double check
        final int vacantSlotLocation = getPhysicalSlotIndex(vacantSlotLogicalIndex);
        int movableDirection = calculator.findTileInMovableDirList(logicalIndex, vacantSlotLocation);
        
        if (!calculator.PuzzleTileCanMove(logicalIndex))
        {
            //TODO beep? assert?
            assert(movableDirection == JavaPuzzle2.UNDEFINED_LOCATION);
            return false;
        }
        else
        { 
            executeSlide(movableDirection, logicalIndex, doAnimatedSlide);
            return true;
        }
    }

    private void executeSlide(int movableDirection, int logicalIndex, boolean doAnimatedSlide)
    {
        //triple check!
        assert(calculator.PuzzleTileCanMove(logicalIndex));
        
        assert(movableDirection != JavaPuzzle2.UNDEFINED_LOCATION);
        //move stuff here!
        JavaPuzzle2.logger.log(Level.FINER, "Will call slider with tile index: {0}", logicalIndex);
        
        PuzzleTile tileToSlide = puzzleTiles.get(logicalIndex);
        assert(tileToSlide !=  null);
        final int InitialTargetLocation = tileToSlide.GetPhysicalSlotIndex();
        final int finalTargetSlotPosition = GetPhysicalVacantSlotIndex();
        tileToSlide.SetPhysicalSlotIndex(finalTargetSlotPosition);
        PuzzleTile vacantTile = puzzleTiles.get(vacantSlotLogicalIndex);
        assert(vacantTile != null && vacantTile != tileToSlide);
        vacantTile.SetPhysicalSlotIndex(InitialTargetLocation);
        
        executeMapChangeForSlide(InitialTargetLocation, finalTargetSlotPosition, logicalIndex);
        
        JavaPuzzle2.logger.log(Level.FINER, "Slide animation :{0}", doAnimatedSlide);
        
        if (doAnimatedSlide)
        {
            //TODO: add animation here
        }
        
        JavaPuzzle2.logger.log(Level.FINER, "Slid {0}", logicalIndex);
        
        forcePartialRedraw(logicalIndex);
    }

    //TODO move this to calculator and make it work for map array parameters
    public void executeMapChangeForSlide(final int InitialTargetLocation, final int finalTargetSlotPosition, int logicalIndex)
    {
        calculator.executeMapChangeForSlide(InitialTargetLocation, finalTargetSlotPosition, logicalIndex);
    }
    
    final public int getPhysicalSlotIndex(int logicalIndex)
    {
        assert(logicalIndex > 0);
        assert(logicalIndex <= JavaPuzzle2.MAX_TILES);
        
        //TODO: implement this! Not done yet!
        PuzzleTile tileToCheck = puzzleTiles.get(logicalIndex);
        if (tileToCheck == null)
        {
            //doesn't exist (maybe vacant), definitely can't move
            assert(logicalIndex  == vacantSlotLogicalIndex);
            return JavaPuzzle2.UNDEFINED_LOCATION;
        }
        final int physicalLocationFromTile = tileToCheck.GetPhysicalSlotIndex();
        final int physicalLocation = L2PMapping[logicalIndex];
        assert(physicalLocation == physicalLocationFromTile);
//        
//        //System.out.printf("from API: physical location:%d\n", physicalLocation);
        return physicalLocation;
    }
    
    private void rebuildTileLocationsFromP2L()
    {
        final int numTiles = numRows * numCols;
     
        for (int i = 0; i < numTiles; ++i)
        {
            int logicalIndex =  P2LMapping[i];
            assert(logicalIndex > 0 && logicalIndex <= numTiles);
            
            if (logicalIndex == numTiles)
            {
                this.vacantSlotLogicalIndex = logicalIndex;
            }
            PuzzleTile p = this.getPuzzleTile(logicalIndex);
            assert (p != null);
            p.SetPhysicalSlotIndex(i);
            updateL2PEntry(logicalIndex, i);
        }
    }
    
    public void updateL2PEntry(int logicalIndex, int physicalIndex)
    {
        calculator.updateL2PEntry(logicalIndex, physicalIndex);
    }
    
    final public int getPhysicalSlotIndexForPoint(Point2D p)
    {
        for (int physicalSlotIndex = 0; physicalSlotIndex < locationOfPhysicalSlot.length; ++physicalSlotIndex )
        {
            Rectangle2D.Double r = locationOfPhysicalSlot[physicalSlotIndex];
            if (r != null && r.contains(p.getX(), p.getY()))
            {
                JavaPuzzle2.logger.log(Level.FINE, "Slot parameters: minx: {0} miny: {1} maxx: {2} maxy: {3}", new Object[]{r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY()});
                
                return physicalSlotIndex;
            }
        }
        
        return JavaPuzzle2.UNDEFINED_LOCATION;
    }
    
    final public int getLogicalIndexFromPhysicalSlotIndex(int physicalSlotIndex)
    {
        if (physicalSlotIndex == JavaPuzzle2.UNDEFINED_LOCATION)
        {
            return JavaPuzzle2.UNDEFINED_LOCATION;
        }
        assert(physicalSlotIndex < JavaPuzzle2.MAX_TILES);
        
        return P2LMapping[physicalSlotIndex];
    }
    
    final public int getTileSize()
    {
        return tileSize;
    }
    
    final public int getVacantTileLogicalIndex()
    {
        return vacantSlotLogicalIndex;
    }
    
    private int GetPhysicalVacantSlotIndex()
    {
        return (getPhysicalSlotIndex(vacantSlotLogicalIndex));
    }
    
    public int getNumTiles()
    {
        return calculator.getNumTiles();
    }
    
    final protected int getPosAlongRow(int logicalIndex)
    {
        return calculator.getPosAlongRow(logicalIndex);
    }
   
    final protected int getPosDownCol(int logicalIndex)
    {
        return calculator.getPosDownCol(logicalIndex);
    }
    
    private void createPhysicalLocationMap(int tileIndex)
    {
        assert (tileIndex > 0);
        final int tilePosIndex = tileIndex - 1;
        int xpos = (calculator.getSolutionXPos(tilePosIndex) * tileSeparation) + DEFAULT_X_OFFSET;
        int ypos = (calculator.getSolutionYPos(tilePosIndex) * tileSeparation) + DEFAULT_Y_OFFSET;

        locationOfPhysicalSlot[tilePosIndex].setRect(xpos, ypos, tileSize, tileSize);
        P2LMapping[tilePosIndex] = tileIndex;
    }
    
    private BufferedImage getBitmap(int logicalIndex)
    {
        BufferedImage bitmap = null;
        PuzzleTile targetTile = getPuzzleTile(logicalIndex);

        if (targetTile != null)
        {
            bitmap = targetTile.GetBitmap();
        }

        return bitmap;
    }
    
    private void forceFullRedraw()
    {
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
            return;
        }
        Graphics g = frame.getPuzzleGraphics();
        
        if (g != null)
        {
            paintAllTiles(g);
        }
    }
    
    private void forcePartialRedraw(int logicalIndex)
    {
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
            return;
        }

        if (!frame.compareCurrentPuzzle(this))
        {
            //GUI has been switched to a different puzzle
            //TODO handle multiple puzzle displays instead?
            return;
        }

        Graphics g = frame.getPuzzleGraphics();  
        
        if (g != null)
        {
            //partial redraw (only affected tile locations)
            redrawTiles(g, logicalIndex, true);
        }
    }
    
    /**
     * Debug functions
     */
    public void addDot(Point2D p)
    {
        assert(JavaPuzzle2.DEBUG_DRAW_DOTS_AT_PRESS_POS);

        final int w = 2;
        final int h = 2;
        
        Rectangle2D newDot = new Rectangle2D.Double(p.getX(), p.getY(), w, h);
        dots.add(newDot);
    }
    
    private void drawDots(Graphics g)
    {
        assert(JavaPuzzle2.DEBUG_DRAW_DOTS_AT_PRESS_POS);

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(Color.RED);
        
        dots.forEach((e) -> { g2.fill(e); });
    }

    public boolean isPuzzleUnderSolverOperations()
    {
        return puzzleUnderSolverOperations;
    }

    public void setPuzzleUnderSolverOperations(final boolean puzzleUnderSolverOperations)
    {
        this.puzzleUnderSolverOperations = puzzleUnderSolverOperations;
        
        final boolean disableSolverRelatedItems = !puzzleUnderSolverOperations;
        
        if (frame != null)
        {
            frame.setSolverMenuEnable(disableSolverRelatedItems);
            frame.setOperationsEnable(disableSolverRelatedItems);
        }
        else
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
        }
    }
    
    public void updateInProgressValues(final double elapsedTime, int movesDone)
    {
        if (frame != null) 
        {
            frame.updateInProgressValues(elapsedTime, movesDone);
        }
        else
        {
            assert(!JavaPuzzle2.isUsingGUIflow());            
        }
    }
    
    
    
    public static class PuzzleState
    {

        private PuzzleState(PuzzleConfigData config)
        {
            try
            {
                assert (config != null);
                this.config = config.clone();
                P2LMapping = new int[config.numRows * config.numCols];
            } catch (CloneNotSupportedException e)
            {
                assert (false);
            }
        }
        
        public int[] getP2LMappingCopy()
        {
            int[] newCopy = Arrays.copyOf(P2LMapping, P2LMapping.length);
            
            return newCopy;
        }

        private PuzzleConfigData getConfig()
        {
            return config;
        }
        
        private int[] getP2LMapping()
        {
            return P2LMapping;
        }

        private PuzzleConfigData config;
        private int[] P2LMapping;
    }
    
    final private PuzzleFrame           frame;
    final private TreeMap<Integer, PuzzleTile>   puzzleTiles;
    PuzzleTileSetCalculator             calculator;
    final private Rectangle2D.Double[]  locationOfPhysicalSlot;
    final private PuzzleTileSetHistory  history;
    final private PuzzleState           state; //? is final correct?
    private int[]                       P2LMapping;
    final private int[]                 L2PMapping;
    final private int                   numRows;
    final private int                   numCols;
    private int                         currentCost;
    private int                         vacantSlotLogicalIndex;
    final private String                puzzleName;
    private boolean                     bitmapImported;
    private boolean                     puzzleUnderSolverOperations;
    private boolean                     puzzleIsSolved;
    final private String                bitmapFilename;
    final private JavaPuzzle2.BitmapImportMode 
                                        importMode;
    
    private BufferedImage               srcPicture;
    final private Random                random;
    
    final private int                   tileSeparation;
    final private int                   tileSize;
    final float                         tileScalingFactor;
    final int                           textOffset;
    
    //debug
    final private ArrayList<Rectangle2D> dots;
    
    static final private int RANDOM_SEED = 42;
    static final private int DEFAULT_FONT_SIZE = 24;
    
    static final private int DEFAULT_TEXT_OFFSET = 7;
    static final private int DEFAULT_X_OFFSET = 100;
    static final private int DEFAULT_Y_OFFSET = 100;
    static final private int DEFAULT_TILE_SEPARATION = 100;
    static final private int DEFAULT_TILE_GROOVE_WIDTH = 2;
    static final private int DEFAULT_TILE_SIZE = DEFAULT_TILE_SEPARATION - DEFAULT_TILE_GROOVE_WIDTH;
}
