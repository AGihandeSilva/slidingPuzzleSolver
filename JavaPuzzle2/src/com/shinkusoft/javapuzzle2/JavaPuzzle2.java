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

import java.awt.EventQueue;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.util.logging.*;

import com.shinkusoft.javapuzzle2.PuzzleSolverResult;

/**
 *
 * @author Gihan
 */
public class JavaPuzzle2 {
    
    public interface PuzzleSolver
    {
        public boolean Solve();
        public PuzzleToolsSolverMethod GetMethodDescription();
        public PuzzleSolverResult getResultReport();
        public int getMovesDone();
        public boolean isSolverActive();
        public boolean haltWasRequested();
    }
    
    public interface ResultObserver
    {
        public void generateResultResponse(PuzzleSolverResult solverResult);
    }
    
    public interface Checksum
    {
        @Override
        public boolean equals(Object other);
        public boolean append(int value);
    }
    
    public enum BitmapImportMode 
    { 
        CROP("(cropped)"), SCALE("(scaled)"), MAX_IMPORT_MODES(""); 

        BitmapImportMode(String descriptor)
        {
            this.descriptor = descriptor;
        }
        
        public String getDescriptor()
        {
            return descriptor;
        }
        
        private final String descriptor;
    };
    
    public enum PuzzleTileDirection
    {
        UP(0), RIGHT(1), LEFT(2), DOWN(3);
        
        PuzzleTileDirection(int value)
        {
            this.value = value;
        }

        public int getIntValue()
        {
            return value;
        }
        
        static public PuzzleTileDirection getInverse(PuzzleTileDirection dir)
        {
            PuzzleTileDirection result = UP;
            switch(dir.getIntValue())
            {
                case(0):
                    result = DOWN;
                    break;
                case(1):
                    result = LEFT;
                    break;
                case(2):
                    result = RIGHT;
                    break;
                case(3):
                    result = UP;
                    break;
                default:
                    assert(false);
                break;
            }
            return result;
        }
        
        static public PuzzleTileDirection getValue(int intValue)
        {
            PuzzleTileDirection dir = intToEnumMap.get(intValue);
            return dir;
        }
        
        
        private static final Map<Integer, PuzzleTileDirection> intToEnumMap
                = new HashMap<>();
        
        static {
                for (PuzzleTileDirection dir: PuzzleTileDirection.values())
                {
                    intToEnumMap.put(dir.value, dir);
                }
        }
        
        private final int value;
    }
    
    public enum PuzzleToolsSolverMethod
    {
        PURE_GREEDY_SEARCH(0), UNIFORM_COST_SEARCH(1), MAX_NUM_SUPPORTED_SOLVER_METHODS(2);

        PuzzleToolsSolverMethod(int value)
        {
            this.value = value;
        }

        public int getIntValue()
        {
            return value;
        }

        static public PuzzleToolsSolverMethod getValue(int intValue)
        {
            PuzzleToolsSolverMethod method = intToEnumMap.get(intValue);
            return method;
        }

        private static final Map<Integer, PuzzleToolsSolverMethod> intToEnumMap
                = new HashMap<>();

        static
        {
            for (PuzzleToolsSolverMethod dir : PuzzleToolsSolverMethod.values())
            {
                intToEnumMap.put(dir.value, dir);
            }
        }

        final private int value;
    }
    
    public enum SolverState
    {
        IDLE(0), RUNNING(1), HALTED(2), FAILED(3), SOLVED(4), MAX_SOLVER_STATES(5);
        
        SolverState(int value)
        {
           this.value = value; 
        }
        
        public static String getSolverStateName(SolverState state)
        {
            assert (state.value <= solverStateNames.length);
            return solverStateNames[state.value];
        }
        
        private static final String[] solverStateNames = { "Idle", "Running", "Halted", "Failed", "Solved" };
        
        final private int value;     
    }
    
    public static boolean clearSolverState()
    {
        BasePuzzleSolver solver = getCurrentSolver();
        boolean stateChanged = false;
        if (solver != null)
        {
            stateChanged = solver.isSolverIdle();
            solver.clearSolverState();
        }
        
        return stateChanged;
    }
    
    //copied from forum
    public static void addDelay(long nanos)
    {
        long elapsed;
        final long startTime = System.nanoTime();
        do
        {
            elapsed = System.nanoTime() - startTime;
        } while (elapsed < nanos);
    }
    
    protected static void initForTests()
    {
        nextPuzzleIndex = 0;
        puzzles.clear();
        handlers.clear();
        solverMap.clear();
        
        initLogger();
        registerSolvers();
        setUsingGUIflow(false);
    }
    
    //is exposing this so that test code can use it really necessary?
    private static void initLogger()
    {
        if (System.getProperty("java.util.logging.config.class") == null
                && System.getProperty("java.util.logging.config.file") == null)
        {
            try
            {
                Level logLevel = Level.FINE;
                logger = Logger.getLogger("com.kukusoft.JavaPuzzle2");
                final int LOG_ROTATION_COUNT = 10;
                logger.setLevel(logLevel);
                //logger.setUseParentHandlers(false);
                Handler handler =  new FileHandler("%h/JavaPuzzle2.log", 0, LOG_ROTATION_COUNT);
                handler.setLevel(logLevel);
                logger.addHandler(handler);  
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "Can't create log file handler", e);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initLogger();
        registerSolvers();

        Runnable puzzleRunner = ()
                -> 
                {
                    PuzzleFrame frame = new PuzzleFrame();

                    PuzzleTileSet firstPuzzle = new PuzzleTileSet(new PuzzleConfigData(),
                            frame,
                            makeDefaultPuzzleName());
                    AddPuzzle(frame, firstPuzzle);

                    frame.add(tileHandler);
                    handlers.add(tileHandler);
                    keyListener = new PuzzleTileDebugKeyListener(tileHandler, frame);
                    frame.addKeyListener(keyListener);

                    mouseListener = new PuzzleTileMouseListener(tileHandler, frame);
                    frame.addMouseListener(mouseListener);

                    Handler windowHandler = new WindowHandler();
                    windowHandler.setLevel(Level.FINER);
                    logger.addHandler(windowHandler);

                    frame.setVisible(true);
        };
        EventQueue.invokeLater(puzzleRunner);
    }
    
    static public boolean SpaceOkForNewPuzzle()
    {
        return (nextPuzzleIndex < MAX_NUM_PUZZLES);
    }
    
    static public int getNumberOfPuzzles()
    {
        assert(puzzles != null);
        return (puzzles.size());
    }
    
    static PuzzleTileSet getPuzzle(int index)
    {
        if (index < 0 || index >= puzzles.size())
        {
            return null;
        }
        return puzzles.get(index);
    }
    
    static public boolean ClosePuzzle(PuzzleFrame frame, int index)
    {
        boolean removed = false;
        if (JavaPuzzle2.isSolverActive() ||
                puzzles.size() <= 1)
        {
            return false;
        }
                
        assert(index >= 0 && index < puzzles.size());
        
        PuzzleTileSet puzzleToGo = getPuzzle(index);
        
        assert(puzzleToGo != null);
        
        removed = puzzles.remove(puzzleToGo);
        
        assert(removed);
        
        assert(nextPuzzleIndex > 1);
        --nextPuzzleIndex;

        assert(puzzles.size() > 0);
        PuzzleTileSet puzzleForFocus = getPuzzle(puzzles.size() - 1);
        
        SwitchDisplayToPuzzle(frame, puzzleForFocus);
        
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
        }
        else
        {
            frame.repaint();
        }

        
        return removed;
    }
    
    static public void AddPuzzle(PuzzleFrame frame, PuzzleTileSet newPuzzle)
    {
        assert(newPuzzle != null);
        assert(SpaceOkForNewPuzzle());
        puzzles.add(newPuzzle);
        ++nextPuzzleIndex;
        if (!JavaPuzzle2.isSolverActive())
        {
            AddPuzzleToGUI(frame, newPuzzle);
        }
    }

    private static void AddPuzzleToGUI(PuzzleFrame frame, PuzzleTileSet newPuzzle)
    {
        if (frame == null)
        {
            assert(!JavaPuzzle2.isUsingGUIflow());
            return;
        }
        SwitchDisplayToPuzzle(frame, newPuzzle);
        frame.updatePuzzleDocumentList();
    }
    
    static public void SwitchDisplayToPuzzle(PuzzleFrame frame, PuzzleTileSet puzzle)
    {
        if (frame != null)
        {
            frame.setCurrentPuzzle(puzzle);
            tileHandler.setCurrentPuzzle(puzzle);
        }
        puzzle.propagateStateChange();
    }
    
    static public String makeDefaultPuzzleName()
    {
        return new String("Puzzle" + (nextPuzzleIndex + 1));       
    }
    
    static public String[] getPuzzleDescriptions()
    {
        final int sizeOfArray = puzzles.size();
        assert(sizeOfArray >= 0 && sizeOfArray <= MAX_NUM_PUZZLES);
        String[] descriptions =  new String[sizeOfArray];
        
        
        int  i = 0;
        for (PuzzleTileSet puzzle : puzzles)
        {
            Point2D rowColData = puzzle.getRowColData();
            String descRoot = new String((i + 1) + ": " + puzzle.getName() + ", " +
                    (int)rowColData.getX() + "x" + (int)rowColData.getY());
            String SourceDesc = puzzle.getSourceDescription();
            descriptions[i] = descRoot + ", " + SourceDesc;
            ++i;
        }
        
        return descriptions;
    }
    
    static public PuzzleToolsConfig getToolsConfigData()
    {
        return toolsConfig;
    }
    
    static private void registerSolvers()
    {
        //TODO, find a way to share the strings here with the GUI form
        solverMap.put(PuzzleToolsSolverMethod.PURE_GREEDY_SEARCH, new GreedyPuzzleSolver());
        solverNames.put(PuzzleToolsSolverMethod.PURE_GREEDY_SEARCH.getIntValue(), "Pure Greedy search");
        solverMap.put(PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH, new UniformCostSearchSolver());
        solverNames.put(PuzzleToolsSolverMethod.UNIFORM_COST_SEARCH.getIntValue(), "Uniform Cost search");
    }
        
    static public boolean setCurrentSolverMethodValue(PuzzleToolsSolverMethod SolverMethodValue)
    {
        int selectedMethodIndex = SolverMethodValue.getIntValue();
        if (selectedMethodIndex < 0 ||
                selectedMethodIndex >= JavaPuzzle2.PuzzleToolsSolverMethod.MAX_NUM_SUPPORTED_SOLVER_METHODS.getIntValue())
        {
            return false;
        }
        
        toolsConfig.setCurrentSolverMethod(SolverMethodValue);
        return true;
    }

    static public boolean isSolverActive()
    {
        BasePuzzleSolver solver = getCurrentSolver();
        assert(solver != null);
        return solver.isSolverActive();
    }
    
    static public void haltSolver()
    {
        if (isSolverActive())
        {
            BasePuzzleSolver solver = getCurrentSolver();
            solver.requestSolverHalt();
        }
    }
    
    static public boolean solverWasHalted()
    {
        BasePuzzleSolver solver = getCurrentSolver();
        assert(solver != null);
        
        return solver.haltWasRequested();
    }
    
    static public boolean Solve(PuzzleTileSet puzzle, ResultObserver observer)
    {
        BasePuzzleSolver solver = getCurrentSolver();
        assert(solver != null);
        assert(puzzle != null);
        if (observer != null)
        {
            solver.setResultListener(observer);
        }
        solver.setPuzzle(puzzle);
        final boolean SolverResult = solver.Solve();
        
        return (SolverResult);
    }
    
    static public boolean Solve(PuzzleTileSet puzzle)
    {
        return Solve(puzzle, null);
    }

    public static BasePuzzleSolver getCurrentSolver()
    {
        PuzzleToolsSolverMethod method = toolsConfig.getCurrentSolverMethod();
        assert(method.getIntValue() < PuzzleToolsSolverMethod.MAX_NUM_SUPPORTED_SOLVER_METHODS.getIntValue());
        BasePuzzleSolver solver = solverMap.get(method);
        return solver;
    }
    
    static public String getSolverName(int solverMethodIndex)
    {
        String result = null;
        if (solverNames.containsKey(solverMethodIndex))
        {
            result = solverNames.get(solverMethodIndex);
        }
        
        return result;
    }
    
    static SolverState getCurrentSolverState()
    {
        BasePuzzleSolver solver = getCurrentSolver();
        assert(solver != null);
        
        return solver.getState();
    }

    public static boolean isUsingGUIflow()
    {
        return usingGUIflow;
    }

    private static void setUsingGUIflow(boolean isGUIflow)
    {
        JavaPuzzle2.usingGUIflow = isGUIflow;
    }
    
    
    static Logger logger;
    
    static private boolean usingGUIflow = true;
    
    static private int nextPuzzleIndex = 0;
    final static private ArrayList<PuzzleTileSet> puzzles = new ArrayList<>();
    final static private ArrayList<PuzzleTileHandler> handlers = new ArrayList<>();
    static private ArrayList<String> puzzleDescriptions;
    static private KeyListener keyListener;
    static private MouseListener mouseListener;
    final static private PuzzleTileHandler tileHandler = new PuzzleTileDisplayer(null);
    final static private PuzzleToolsConfig  toolsConfig = new PuzzleToolsConfig();
    
    final static private Map<PuzzleToolsSolverMethod, BasePuzzleSolver> solverMap = new HashMap<>();
    final static private Map<Integer, String> solverNames = new HashMap<>();
    
    static final public int UNSPECIFIED_VALUE  = -2;
    static final public int UNDEFINED_LOCATION = -1;
    static final public int INIT_LOCATION_VALUE = 0xFFFFFFFF;
    static final public int MAX_NUM_PUZZLES = 10;
    
    static final public int MIN_ROWS  = 2;
    static final public int MIN_COLS  = 2;
    static final public int DEFAULT_ROWS  = 4;
    static final public int DEFAULT_COLS  = 4;
    static final public int MAX_ROWS  = 10;
    static final public int MAX_COLS  = 10;
    static final public int MAX_TILES = MAX_ROWS * MAX_COLS;
    static final public int ALL_DIRECTIONS = 4;
    static final public int MAX_PRIME_NUMBERS = 100;
    
    static final public int[] PRIME_NUMBERS =
    {
        //2,   3,   5,   7,   11,  13,  17,  19,  23,  29,
        //31,  37,  41,  43,  47,  53,  59,  61,  67,  71,
        //73,  79,  83,  89,  97,  101, 103, 107, 109, 113,
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
        179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
        233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
        283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
        353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
        419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
        467, 479, 487, 491, 499, 503, 509, 521, 523, 541,
        547, 557, 563, 569, 571, 577, 587, 593, 599, 601,
        607, 613, 617, 619, 631, 641, 643, 647, 653, 659,
        661, 673, 677, 683, 691, 701, 709, 719, 727, 733
    };
    
    //debug
    static final boolean DEBUG_DRAW_DOTS_AT_PRESS_POS = false;
    

}

/**
The code below is 
taken from Core Java : volume 1

A handler for displaying log records in a window
*/
    class WindowHandler extends StreamHandler
    {
        public WindowHandler()
        {
            frame = new JFrame();
            final JTextArea output = new JTextArea();
            output.setEditable(false);
            frame.setSize(400, 200);
            frame.add(new JScrollPane(output));
            frame.setFocusableWindowState(false);
            frame.setVisible(true);
            frame.setTitle("Debug Log");
            setOutputStream(new OutputStream()
            {
                @Override
                public void write(int b) {}
                
                @Override
                public void write(byte[] b, int off, int len)
                {
                    output.append(new String(b, off, len));
                }
            
            });
        }
        
        @Override
        public void publish(LogRecord record)
        {
            if (!frame.isVisible())
            {
                return;
            }
            super.publish(record);
            flush();
        }
        
        final private JFrame frame;
    }

