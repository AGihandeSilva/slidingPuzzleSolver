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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import com.shinkusoft.javapuzzle2.JavaPuzzle2.*;
import java.util.logging.Level;

/**
 *
 * @author Gihan
 */
public class PuzzleTileBitmapImporter
{
    boolean importBitmap(PuzzleConfigData destConfig, PuzzleTileSet puzzle, String filename)
    {
        assert(destConfig != null);
        assert(puzzle != null);
        assert(filename != null);
        File f = new File(filename);
        boolean fileOK = f.isFile();
        
        if (!fileOK)
        {
            return fileOK;
        }
        
        BufferedImage fullImage = puzzle.setSourcePicture(filename);
        
        final int fullSrcWidth = fullImage.getWidth();
        final int fullSrcHeight = fullImage.getHeight();
        int cropWidth;
        int cropHeight;
        
        final Rectangle2D destAreaData = puzzle.getPlotAreaData();
        
        assert (destAreaData.getWidth() != 0);
        assert (destAreaData.getHeight() != 0);

        int logicalIndex = 1;
        double xScaling;
        double yScaling;
        
        //this code assumes logical position = physical position (true at starting configuration)
        //JavaPuzzle2.logger.fine("fw: " + fullSrcWidth + " fh: " + fullSrcHeight);
        JavaPuzzle2.logger.log(Level.FINE, "fw: {0} fh: {1}", new Integer[]{fullSrcWidth, fullSrcHeight});
        
        BufferedImage processedSrcImage = fullImage;

        if (destConfig.importMode == BitmapImportMode.SCALE)
        {
            JavaPuzzle2.logger.log(Level.FINE, "SCALE Bitmap: lookup: x: {0} y: {1}", new Object[]{destAreaData.getX(), destAreaData.getY()});
            xScaling = fullSrcWidth / destAreaData.getWidth();
            yScaling = fullSrcHeight / destAreaData.getHeight();
        }
        else
        {
            assert(destConfig.importMode == BitmapImportMode.CROP);
            JavaPuzzle2.logger.log(Level.FINE, "CROP Bitmap: lookup: x: {0} y: {1}", new Object[]{destAreaData.getX(), destAreaData.getY()});
            final double tgtAspectRatio = (double)destConfig.numCols / (double)destConfig.numRows;
            final double srcAspectRatio = (double)fullSrcWidth / (double)fullSrcHeight;
            JavaPuzzle2.logger.log(Level.FINE, "TGT aspect: {0} SRC aspect: {1}", new Object[]{tgtAspectRatio, srcAspectRatio});
            
            int SrcCropWidth = fullSrcWidth;
            int SrcCropHeight = fullSrcHeight;
            
            if (srcAspectRatio >= tgtAspectRatio)
            {
                //src is wider, crop to left and right
                SrcCropWidth = (int)((double)(fullSrcWidth) * (tgtAspectRatio / srcAspectRatio));   
            }
            else
            {
                //src is taller, crop top and bottom
                SrcCropHeight = (int)((double)(fullSrcHeight) * (srcAspectRatio / tgtAspectRatio));
            }
            JavaPuzzle2.logger.fine("Cropping src: details as below:-");
            JavaPuzzle2.logger.log(Level.FINE, "src x: {0} src y: {1}", new Object[]{(fullSrcWidth - SrcCropWidth) / 2, (fullSrcHeight - SrcCropHeight) / 2});
            JavaPuzzle2.logger.log(Level.FINE, "src w: {0} src h: {1}", new Object[]{SrcCropWidth, SrcCropHeight});
            processedSrcImage = fullImage.getSubimage((fullSrcWidth - SrcCropWidth) / 2, (fullSrcHeight - SrcCropHeight) / 2, SrcCropWidth, SrcCropHeight);
            
            xScaling = SrcCropWidth / destAreaData.getWidth();
            yScaling = SrcCropHeight / destAreaData.getHeight();
        }
        importAllTiles(destConfig, puzzle, logicalIndex, destAreaData, xScaling, yScaling, processedSrcImage);
        
        puzzle.registerImportedBitmap();
        JavaPuzzle2.logger.fine("Bitmap import done!");
        return true;
    }

    private void importAllTiles(final PuzzleConfigData destConfig, PuzzleTileSet puzzle, 
            int logicalIndex, final Rectangle2D destAreaData, final double xScaling, 
            final double yScaling, BufferedImage fullSrcImage)
    {
        int cropWidth;
        int cropHeight;
        for (int row = 0; row < destConfig.numRows; ++row)
        {
            for (int col = 0; col < destConfig.numCols; ++col)
            {
                Rectangle2D.Double destLocation = puzzle.getTileLocation(logicalIndex);
                
                //calculate source area corresponding to this tile
                int x = (int)((destLocation.getX() - destAreaData.getX()) * xScaling);
                assert(x >= 0);
                int y = (int)((destLocation.getY() - destAreaData.getY()) * yScaling);
                assert(y >= 0);
                cropWidth = (int)(destLocation.getWidth() * xScaling);
                cropHeight = (int)(destLocation.getHeight() * yScaling);
                
                JavaPuzzle2.logger.log(Level.FINER, "Lookup: row: {0} col: {1} x: {2} y: {3} w: {4} h: {5}", new Object[]{row, col, x, y, cropWidth, cropHeight});
                
                //make crop of source data
                BufferedImage cropImage = fullSrcImage.getSubimage(x, y, cropWidth, cropHeight);
                
                int tileSize = puzzle.getTileSize();
                BufferedImage destTileImage = new BufferedImage(tileSize, tileSize, fullSrcImage.getType());
                //scale the cropped area to match the tile
                Image scaleCropImage = cropImage.getScaledInstance(tileSize, tileSize, BufferedImage.SCALE_SMOOTH);
                //write the image into the tile's bitmap object
                Graphics g = destTileImage.getGraphics();
                g.drawImage(scaleCropImage, 0, 0, null);
                
                puzzle.setBitmap(logicalIndex, destTileImage);
                ++logicalIndex;
            }
        }
    }
}
