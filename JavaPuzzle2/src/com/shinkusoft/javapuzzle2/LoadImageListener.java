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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Gihan
 */
    public class LoadImageListener implements ActionListener
    {
        public LoadImageListener(PuzzleFrame owner, JFileChooser FILE_CHOOSER)
        {
            super();
            setFileSetterCallback(null);
            this.owner  = owner;
            this.FILE_CHOOSER = FILE_CHOOSER;
        }
        
        final public void setFileSetterCallback(PuzzleFrame.PuzzleFrameFileSetter fileSetter)
        {
            this.fileSetter = fileSetter;
        }
        
        public String getFilename()
        {
            return filename;
        }
        
        @Override
        public void actionPerformed(ActionEvent event)
        {
            JavaPuzzle2.logger.entering("LoadImageListener", "actionPerformed");
            
            FILE_CHOOSER.setCurrentDirectory(new File("."));
            FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files",
                                "jpg", "jpeg", "gif");
            FILE_CHOOSER.setFileFilter(filter);
            
            FILE_CHOOSER.setAccessory(new PuzzleImagePreviewer(FILE_CHOOSER));
            int result = FILE_CHOOSER.showOpenDialog(owner);
            
            if (result == JFileChooser.APPROVE_OPTION)
            {
                String chosenName = FILE_CHOOSER.getSelectedFile().getPath();
                JavaPuzzle2.logger.log(Level.FINE, "Selected image file: {0}", chosenName);
                if (fileSetter != null)
                {
                    fileSetter.setFilename(chosenName);
                }
                this.filename = chosenName;
            }
            else
            {
                this.filename = "";
            }
        }
        
        private PuzzleFrame.PuzzleFrameFileSetter   fileSetter;
        final private JFileChooser                  FILE_CHOOSER;
        final private PuzzleFrame                   owner;
        private String                              filename;
    }
