# slidingPuzzleSolver

A Java-based sliding puzzle game with solvers.

This tool uses Swing and was developed using JDK 1.8, the NetBeans IDE (version 8.2) and junit.

The code is stored under a _shinkusoft_ name I sometimes use for my code.



![Alt text](./screenshot/screenCapture.jpg?raw=true "Screen Shot")


Motivation for this project
==================================

I wrote this program from scratch in the hope my son might find it fun to use (he hasn't yet) and to refresh my fading Java knowledge.

(Java is somewhat of a 'second language' to me, so perhaps the source code might have some room for improvement....)

I also wanted to investigate using search algorithms to solve this kind of problem (though I have only constructed
two simple solvers so far, hopefully they are not so difficult to add...)

How to use this
==================================
* Slide a tile adjacent to the puzzle's empty slot by clicking on it
* A random shuffle of 100 moves is available via the menus, as are undo, redo and 'reset' operations
* Solvers can be selected via the Tools menu, the current options are:
     + Greedy solver (unlikely to be successful, except for trivial cases)
     + Uniform cost search solver - attempts to solve the puzzle by traversing a tree of possible moves
* new puzzles of various dimensions can be added via the File menu
     + either plain 'number' puzzles, or picture puzzles using bitmaps you can browse for

TODOs
==================================
* Add more intelligent solvers
* Add a GUI control to change the delay between tile slides when executing a solution sequence
* Find a way of monitoring virtual memory usage somehow so that a solver can dynamically adjust its tree size to match its environment
* Add code to save configurations for later use
* Make the GUI look a bit more polished
* Code tidy
* Add a lot more junit tests



