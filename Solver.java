import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Stopwatch;

public class Solver {
    private int moves;
    private Stack<Board> solution;
    private Stack<BoardNode> closedList;
    private MinPQ<BoardNode> openList;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial){
        //init Values
        Stopwatch s = new Stopwatch();
        solution = new Stack<>();
        if(initial == null) throw new IllegalArgumentException("initial Board is null!");
        if(!initial.isSolvable()) throw new IllegalArgumentException("initial Board is not solvable!");
        moves = 0;
        if(initial.isGoal())
        {
        solution.push(initial);
        StdOut.println("Moves needed: "+ moves + "\n"  + initial.toString());
		return;
        }
        else{
        closedList = new Stack<>();
        BoardNode initBoard = new BoardNode(initial, 0, null);
        //openList = new MinPQ(initBoard.ByManhattenAndMoves());
        openList = new MinPQ();
        openList.insert(initBoard);
        closedList.push(openList.min());
        while(!openList.min().content.isGoal())solve();
        }
        BoardNode indexer = openList.min();
        while(indexer.previousBoardNode != null){
        solution.push(indexer.content);
        indexer = indexer.previousBoardNode;
        }
        solution.push(initial);
        StdOut.println("Time needed to compute the Process:" + s.elapsedTime() +"s");
        /*Steps to be made
        1.original board to open open List
        2.check if its goal state
        3.get the children Boards from the open list
        4. move the first board from the open list to closed List
        5. if any children appear in one of the Lists remove them
        6. Add list of remaining Boards to the open List
        7. back to step 2
         */


    }

    // min number of moves to solve initial board
    public int moves(){
      return solution.size()-1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution(){
      return solution;
    }

    // test client (see below)
    public static void main(String[] args)
    {
      //assert unitTest();
      In i = new  In("PuzzleData/" + args[0]);
      int[] r = i.readAllInts();
      //String _s = i.readString();
      int sz = r[0];
      int[][] puzzle = new int[sz][sz];

      int index = 1;
      for(int z = 0; z < sz; z++)
      {
        for(int y = 0; y < sz; y++)
        {
        puzzle[z][y] = r[index];
        index++;
        }
      }
      Board solveBoard = new Board(puzzle);
      StdOut.println("\nInput Board is going to be solved - Input Board:\n\n" + solveBoard.toString());
      Solver s = new Solver(solveBoard);
      Iterator iter = s.solution().iterator();
      while(iter.hasNext()) StdOut.println(iter.next().toString());
      StdOut.println("Moves Needed to solve the puzzle: " + s.moves());

    }

    private void solve(){
      //StdOut.println(openList.min().content.toString());
      closedList.push(openList.min());
      Iterator children = openList.delMin().content.neighbors().iterator();
      BoardNode node = closedList.peek();
      while(children.hasNext()){
        Board checker = (Board)children.next();
        if(node.previousBoardNode == null || (node.previousBoardNode != null && !node.previousBoardNode.content.equals(checker))){
          openList.insert(new BoardNode(checker, moves, node));
        }
      }
    }

    private class BoardNode implements Comparable<BoardNode>
      {
        public Board content;
        public BoardNode previousBoardNode;
        public int movesNeeded;
        public int manhattan;
        public int hamming;

        public  BoardNode(Board content, int moves, BoardNode prev)
        {
          this.content = content;
          this.previousBoardNode = prev;
          if(previousBoardNode != null) movesNeeded = previousBoardNode.movesNeeded+1;
          else moves = 0;
          this.manhattan = content.manhattan();
          this.hamming = content.hamming();
        }

        public  Comparator<BoardNode> ByHamming(){
          return new BoardNodeComp();
        }

        public Comparator<BoardNode> ByManhatten(){
          return new BoardNodeCompManh();
        }

        public Comparator<BoardNode> ByManhattenAndMoves(){
          return new BoardNodeCompManhMovIn();
        }

        public int compareTo(BoardNode that)
        {
          int prio = (this.movesNeeded + this.manhattan) - (that.movesNeeded + that.manhattan);
          if(prio != 0) return prio;
          else return this.manhattan - that.manhattan;
        }
      }

    private class BoardNodeComp implements Comparator<BoardNode>
    {
      private Board content;

      public BoardNodeComp()
      {
        //this.content = content;
      }

      public int compare(BoardNode v, BoardNode w){

        if(v.content.hamming() > w.content.hamming()) return 1;
        else if(v.content.hamming() < w.content.hamming()) return -1;
        else return mnhCompare(v,w);

      }

      private int mnhCompare(BoardNode v, BoardNode w)
      {

        if(v.content.manhattan() > w.content.manhattan()) return 1;
        else if(v.content.manhattan() < w.content.manhattan()) return -1;
        else return 0;
      }

    }

    private class BoardNodeCompManh implements Comparator<BoardNode>
    {
      public BoardNodeCompManh()
      {
        //this.content = content;
      }

      public int compare(BoardNode v, BoardNode w){

        if(v.content.manhattan() > w.content.manhattan()) return 1;
        else if(v.content.manhattan() < w.content.manhattan()) return -1;
        else return hamCompare(v,w);

      }

      private int hamCompare(BoardNode v, BoardNode w)
      {

        if(v.content.hamming() > w.content.hamming()) return 1;
        else if(v.content.hamming() < w.content.hamming()) return -1;
        else return 0;
      }
    }

    private class BoardNodeCompManhMovIn implements Comparator<BoardNode>
    {
      public BoardNodeCompManhMovIn()
      {
        //this.content = content;
      }

      public int compare(BoardNode v, BoardNode w){

        if(v.manhattan+v.movesNeeded > w.manhattan+w.movesNeeded) return 1;
        else if(v.manhattan+v.movesNeeded < w.manhattan+w.movesNeeded) return -1;
        else return 0+v.movesNeeded;

      }
    }

    private static boolean unitTest()
    {
      int[][] testArray = new int[3][3];

      testArray[0][0] = 0;
      testArray[0][1] = 1;
      testArray[0][2] = 3;
      testArray[1][0] = 4;
      testArray[1][1] = 2;
      testArray[1][2] = 5;
      testArray[2][0] = 7;
      testArray[2][1] = 8;
      testArray[2][2] = 6;

      Board test = new Board(testArray);
      Solver s = new Solver(test);
      if(s.moves > 4)return false;
      return true;
    }
}
