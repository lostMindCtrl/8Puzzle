import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

public class Board {

    private final int size;
    private final int[][] puz;
    private int[][] solPuz;
    private Board[] neigh;
    private int[] zroCoord;
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles){

      puz = tiles;
      size = tiles.length;
      initSol();
      zroCoord = new int[2];
      findZro();
    }

    // string representation of this board
    public String toString(){
      String s = size + "\n";
      for(int i =0; i < puz.length; i++){
        for(int j = 0; j < puz[0].length; j++){
          s += puz[i][j] + " ";
        }
        s += "\n";
      }
      return s;
    }

    // tile at (row, col) or 0 if blank
    public int tileAt(int row, int col){
      if(row > size - 1 || col > size - 1) throw new IllegalArgumentException("values not between 0 and n-1");
      return puz[row][col];
    }

    // board size n
    public int size(){
      return size;
    }

    // number of tiles out of place
    public int hamming(){

      int outtaPlace = 0;
      for(int i = 0; i < puz.length; i++){
          for(int j = 0; j < puz.length; j++){
            if(puz[i][j] != solPuz[i][j] && puz[i][j] != 0){
              outtaPlace++;
            }
          }
      }
      return outtaPlace;
    }


    // sum of Manhattan distances between tiles and goal
    public int manhattan(){
      int mnhNmbr = 0;
      for(int i = 0; i < size; i++){
      for(int j = 0 ; j < size; j++){
        if(puz[i][j] == solPuz[i][j] || puz[i][j] == 0) mnhNmbr += 0;
        else
        {
          //StdOut.println("Manhattan Number[BEFORE]: " + mnhNmbr + " Current puzNmbr: " + puz[i][j]);
          mnhNmbr += calculateManhattenDist(((puz[i][j]-1) / size), i, ((puz[i][j]-1) % size), j);
          //StdOut.println("Manhattan Number[AFTER]: " + mnhNmbr + " Current puzNmbr: " + puz[i][j] + "\n");
        }
      }
      }
      return mnhNmbr;
    }

    // is this board the goal board?
    public boolean isGoal(){
      return this.manhattan() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y){
      if(this == y) return true;
      if(this.size != ((Board)y).size()) return false;
      else if(this.toString().equals(y.toString())) return true;
      else return false;
    }

    // all neighboring boards
    public Iterable<Board> neighbors(){
      if(tileAt(zroCoord[0], zroCoord[1]) != 0) throw new IllegalArgumentException("An Error Occured");
      initNeighbors(zroCoord[0], zroCoord[1]);
      return new BoardIterable();
    }

    // is this board solvable?
    public boolean isSolvable(){
      long inv = solveableInversions();
      StdOut.println("Inversions:" + inv);
      if(this.size % 2 != 0 && inv % 2 != 0) return false;
      else if(this.size % 2 == 0 && (inv + zroCoord[0]) % 2 == 0)return false;
      else return true;
    }

    // unit testing (required)
    public static void main(String[] args){
      assert unitTestZero();
      assert unitTestOne();
      assert unitTestTwo();
      assert unitTestThree();
      assert unitTestFour();
    }

    private void initNeighbors(int x, int y){
      int[][] tmp_brd = copy(puz);
      Board[] tmpBrd = new Board[4];
      int boards = 0;
      int tmp;

      if(x+1 < size)
      {
        tmp = puz[x+1][y];
        tmp_brd[x+1][y] = 0;
        tmp_brd[x][y] = tmp;
        tmpBrd[boards] = new Board(copy(tmp_brd));
        boards++;
      }
      tmp_brd = copy(puz);
      if(x-1 >= 0)
      {
        tmp = puz[x-1][y];
        tmp_brd[x-1][y] = 0;
        tmp_brd[x][y] = tmp;
        tmpBrd[boards] = new Board(copy(tmp_brd));

        boards++;
      }
      tmp_brd = copy(puz);
      if(y+1 < size)
      {
        tmp = puz[x][y+1];
        tmp_brd[x][y+1] = 0;
        tmp_brd[x][y] = tmp;
        tmpBrd[boards] = new Board(copy(tmp_brd));
        boards++;
      }
      tmp_brd = copy(puz);
      if(y-1 >= 0)
      {
        tmp = puz[x][y-1];
        tmp_brd[x][y-1] = 0;
        tmp_brd[x][y] = tmp;
        tmpBrd[boards] = new Board(copy(tmp_brd));
        boards++;
      }

      //StdOut.println(tmpBrd[1].toString());
      neigh = new Board[boards];
      for(int i = 0; i < boards; i++){
        neigh[i] = tmpBrd[i];
      }
    }



    private int[][]  initSol()
    {
      solPuz = new int[size][size];

      for(int i = 0; i < size; i++)
      {
        for(int j = 0; j < size; j++)
        {
          solPuz[i][j] = ((i*size)+i)+(j+1)-i;
        }
      }
      solPuz[size-1][size-1] = 0;
      return solPuz;
    }

    private int[][] copy(int[][] w){
      int[][] copied = new int[w.length][w.length];

      for(int i = 0; i < w.length; i++){
        for(int j = 0; j < w.length; j++){
          copied[i][j] = w[i][j];
        }
      }
      return copied;
    }

    private int calculateManhattenDist(int pxz, int pxo, int pyz, int pyo)
    {
      //StdOut.println("[]" + pxz + "[]" + pxo + "[]" + pyz + "[]" + pyo +"[]");
      int x = Math.abs(pxz - pxo);
      int y = Math.abs(pyz - pyo);
      //StdOut.println("\nY:" + y + " / " + pyz  + "-" + pyo);
      //if(x < 0)x = x * (-1);
      //if(y < 0)y = y * (-1);
      return (x + y);
    }

	

    private void findZro()
    {
      boolean found = false;
      for(int i = 0; i < size && !found; i++)
      {
          for(int j = 0; j < size && !found; j++)
          {
            if(puz[i][j] == 0)
            {
              zroCoord[0] = i;
              zroCoord[1] = j;
              found = true;
            }
          }
      }
    }

    private int solveableInversions()
    {
      int[] row_majorOrder = new int[size*size];
      int ind = 0;
      int inversion = 0;
      for(int i = 0; i < size; i++)
      {
        for(int j = 0; j < size; j++)
        {
          row_majorOrder[ind++] = puz[i][j];
        }
      }
      for(int i = 1; i < row_majorOrder.length; i++)
      {
        for(int j = 0; j < i; j++)if(row_majorOrder[j] > row_majorOrder[i] && (row_majorOrder[j] != 0 && row_majorOrder[i] != 0))inversion++;
      }
      return inversion;
    }

    private class BoardIterable implements Iterable<Board>
    {
      public Iterator<Board> iterator()
      {
        return new BoardIterator();
      }
    }

    private class BoardIterator implements Iterator<Board>{
      private Board[] boards = neigh;
      private int sz = 0;
      public boolean hasNext(){return sz < neigh.length;}
      public void remove(){ throw new UnsupportedOperationException("Method has been romoved");}

      public Board next(){
        if(!hasNext()) throw new NoSuchElementException();
        return neigh[sz++];
      }
    }

    //Methods for unit testing

    private static boolean unitTestZero(){
      int[][] testArray = new  int[3][3];

      testArray[0][0] = 1;
      testArray[0][1] = 2;
      testArray[0][2] = 3;
      testArray[1][0] = 4;
      testArray[1][1] = 5;
      testArray[1][2] = 6;
      testArray[2][0] = 7;
      testArray[2][1] = 8;
      testArray[2][2] = 0;

      Board test = new Board(testArray);
      Board test_one = new Board(testArray);
      if(test.manhattan() != 0)return false;
      if(test.hamming() != 0)return false;
      if(!test.isGoal()) return false;
      if(!test.equals(test_one)) return false;
      Iterator iter = test.neighbors().iterator();
      while(iter.hasNext())StdOut.println(iter.next().toString());
      return true;
    }

    private static boolean unitTestOne(){
      int[][] testArray = new  int[3][3];

      testArray[0][0] = 8;
      testArray[0][1] = 6;
      testArray[0][2] = 7;
      testArray[1][0] = 2;
      testArray[1][1] = 0;
      testArray[1][2] = 4;
      testArray[2][0] = 3;
      testArray[2][1] = 5;
      testArray[2][2] = 1;

      Board test = new Board(testArray);
      if(test.manhattan() == 0)return false;
      if(test.hamming() == 0)return false;
      if(test.isGoal())return false;

      return true;
    }

    private static boolean unitTestTwo(){
      int[][] testArray = new  int[3][3];

      testArray[0][0] = 8;
      testArray[0][1] = 1;
      testArray[0][2] = 3;
      testArray[1][0] = 4;
      testArray[1][1] = 0;
      testArray[1][2] = 2;
      testArray[2][0] = 7;
      testArray[2][1] = 6;
      testArray[2][2] = 5;
      Board test = new Board(testArray);
      StdOut.println(test.manhattan());
      if(test.manhattan() != 10) return false;
      if(test.hamming() != 5) return false;
      testArray = new  int[3][3];
      testArray[0][0] = 8;
      testArray[0][1] = 6;
      testArray[0][2] = 7;
      testArray[1][0] = 2;
      testArray[1][1] = 0;
      testArray[1][2] = 4;
      testArray[2][0] = 3;
      testArray[2][1] = 5;
      testArray[2][2] = 1;

      Board testOne = new Board(testArray);
      if(testOne.manhattan() != 22)return false;
      if(testOne.hamming() != 8) return false;
      return true;
    }

    private static boolean unitTestThree()
    {
      int[][] testArray = new  int[3][3];
      testArray[0][0] = 1;
      testArray[0][1] = 2;
      testArray[0][2] = 3;
      testArray[1][0] = 0;
      testArray[1][1] = 4;
      testArray[1][2] = 6;
      testArray[2][0] = 8;
      testArray[2][1] = 5;
      testArray[2][2] = 7;

      Board test = new Board(testArray);
      if(test.isSolvable())return false;
      return true;
    }

    private static boolean unitTestFour(){
      int[][] testArray = new  int[3][3];
      testArray[0][0] = 5;
      testArray[0][1] = 0;
      testArray[0][2] = 4;
      testArray[1][0] = 2;
      testArray[1][1] = 3;
      testArray[1][2] = 8;
      testArray[2][0] = 7;
      testArray[2][1] = 1;
      testArray[2][2] = 6;

      Board test = new Board(testArray);
      if(test.manhattan() != 15)return false;
      return true;
    }
}
