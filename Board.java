import java.util.Stack;

public class Board { 
    private final int[][] tiles;
    private final int n;
    
    public Board(int[][] blocks) { // construct a tiles from an n-by-n array of blocks 
                                   // (where blocks[i][j] = block in row i, column j) 
        if (blocks == null)
            throw new NullPointerException("Null argument");        
        n = blocks.length;
        if (n == 0 || blocks[0].length != n)
            throw new IllegalArgumentException("Illegal input"); 
        tiles = new int[n][n];
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                tiles[i][j] = blocks[i][j];
    }
    
    public int dimension() { // tiles dimension n 
        return n;        
    }
    
    public int hamming() { // number of blocks out of place 
        int priority = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (tiles[i][j] == 0)
                    continue;
                priority += tiles[i][j] == i*n+j+1 ? 0 : 1;                
            }
        }
        return priority;
    }
    
    public int manhattan() { // sum of Manhattan distances between blocks and goal 
        int priority = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (tiles[i][j] == 0 || tiles[i][j] == i*n+j+1)
                    continue;
                int row = (tiles[i][j]-1) / n;
                int col = (tiles[i][j]-1) % n;
                priority += Math.abs(row - i);
                priority += Math.abs(col - j);
            }
        }
        return priority;
    }
    
    public boolean isGoal() { // is this tiles the goal tiles? 
        int count = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i == n-1 && j == n-1) {
                    if (tiles[i][j] != 0) 
                        return false;
                }
                else if (tiles[i][j] != ++count) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Board twin() { // a tiles that is obtained by exchanging any pair of blocks 
        Board twinBoard = new Board(tiles);
        int row1, col1, row2, col2;
        if (twinBoard.tiles[0][0] == 0) {
            row1 = 0; col1 = 1; row2 = 1; col2 = 0;
        }
        else if (twinBoard.tiles[0][1] == 0) {
            row1 = 0; col1 = 0; row2 = 1; col2 = 0;
        }
        else {
            row1 = 0; col1 = 0; row2 = 0; col2 = 1;   
        }
        int swap = twinBoard.tiles[row1][col1];
        twinBoard.tiles[row1][col1] = twinBoard.tiles[row2][col2];
        twinBoard.tiles[row2][col2] = swap;
        return twinBoard;
    }
    
    public boolean equals(Object y) { // does this tiles equal y?
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (that.n != this.n)
            return false;
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (that.tiles[i][j] != this.tiles[i][j])
                    return false;
        return true;
    }
    
    public Iterable<Board> neighbors() { // all neighboring boards 
        Stack<Board> nb = new Stack<Board>();
        int row = 0, col = 0;
        for (int i = 0; i < n; ++i) {
            int j = 0;
            for (; j < n; ++j) {
                if (tiles[i][j] == 0) {
                    row = i; 
                    col = j;
                    break;
                }
            }
            if (j < n && tiles[i][j] == 0) break;
        }
        int[][] dir = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int k = 0; k < dir.length; ++k) {
            int i = row + dir[k][0], j = col + dir[k][1];
            if (i >= 0 && i < n && j >= 0 && j < n) {
                Board next = new Board(tiles);
                int swap = next.tiles[row][col];
                next.tiles[row][col] = next.tiles[i][j];
                next.tiles[i][j] = swap;
                nb.push(next);
            }
        }
        return nb;
    }
    
    public String toString() { // string representation of this tiles (in the output format specified below) 
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
    
    public static void main(String[] args) { // unit tests (not graded) 
        int[][] blocks1 = { {0, 1, 2}, {3, 4, 5}, {6, 7, 8} };
        int[][] blocks2 = { {1, 2, 3}, {4, 5, 6}, {7, 8, 0} };
        Board myBoard = new Board(blocks1);
        System.out.println(myBoard.toString());
        System.out.println(myBoard.hamming());
        System.out.println(myBoard.manhattan());
        System.out.println(myBoard.isGoal());
        Board myBoard2 = new Board(blocks2);
        System.out.println(myBoard2.equals(myBoard));
        System.out.println(myBoard2.hamming());
        System.out.println(myBoard2.manhattan());
        System.out.println(myBoard2.isGoal());
        System.out.println(myBoard2.twin().toString());
        System.out.println(myBoard2.toString());
        Iterable<Board> next = myBoard.neighbors();
        for (Board b : next)
            System.out.println(b.toString());
    }
}