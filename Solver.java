import java.util.Collections;
import java.util.Stack;
import java.util.HashMap;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Solver { 
    private final Stack<Board> solution = new Stack<Board>();
    private final int numberOfMoves;
    private final boolean solvable;
    
    private class Node implements Comparable<Node> {
        private final Board b;
        private final int p;
        private final int n;
        private final Board f;
        
        public Node(Board b, int p, int n, Board f) { 
            this.b = b;
            this.p = p;
            this.n = n;
            this.f = f;
        }
        
        public int compareTo(Node that) {
            return this.p - that.p;
        }
        
        public Board board() {
            return b;
        }
        
        public int numberOfMoves() {
            return n;
        }
        
        public Board father() {
            return f;
        }
    }
    
    // Comparing with using Stack<Node> to store visited, 
    // the running time of HashMap method is smaller when board size n is greater than 30.
    public Solver(Board initial) { // find a solution to the initial board (using the A* algorithm) 
        if (initial == null)
            throw new NullPointerException("Null argument");        
        
        MinPQ<Node> pq = new MinPQ<Node>();
        HashMap<Integer, Stack<Node>> visited = new HashMap<>();
        Node initialNode = new Node(initial, initial.manhattan(), 0, null);
        pq.insert(initialNode);
        Stack<Node> st = new Stack<Node>();
        st.push(initialNode);
        visited.put(initial.manhattan(), st);
        
        MinPQ<Node> pqTwin = new MinPQ<Node>();
        HashMap<Integer, Stack<Board>> visitedTwin = new HashMap<>();
        Board twin = initial.twin();
        pqTwin.insert(new Node(twin, twin.manhattan(), 0, null));
        Stack<Board> stTwin = new Stack<Board>();
        stTwin.push(twin);
        visitedTwin.put(twin.manhattan(), stTwin);
        
        boolean sol = false;
        Node curNode = null;
        while (!pq.isEmpty() && !pqTwin.isEmpty()) {
            curNode = pq.delMin();
            if (curNode.board().isGoal()) {
                sol = true;
                break;
            }
            
            Iterable<Board> ngb = curNode.board().neighbors();
            for (Board nb : ngb) {
                int manht = nb.manhattan();
                int moves = curNode.numberOfMoves()+1;
                Node nbNode = new Node(nb, manht+moves, moves, curNode.board());
                if (curNode.father() != null && curNode.father().equals(nb))
                    continue;
                boolean flag = false;
                if (visited.get(manht) != null) {
                    visited.get(manht).push(nbNode);
                }
                else {
                    Stack<Node> stk = new Stack<Node>();
                    stk.push(nbNode);
                    visited.put(manht, stk);
                }
                pq.insert(nbNode);
            }
            
            Node curNodeTwin = pqTwin.delMin();
            if (curNodeTwin.board().isGoal()) {
                sol = false;
                break;
            }
            Iterable<Board> ngbTwin = curNodeTwin.board().neighbors();
            for (Board nbTwin : ngbTwin) {
                int manht = nbTwin.manhattan();
                if (curNodeTwin.father() != null && curNodeTwin.father().equals(nbTwin))
                    continue;
                if (visitedTwin.get(manht) != null) {
                    visitedTwin.get(manht).push(nbTwin);
                }
                else {
                    Stack<Board> stk = new Stack<Board>();
                    stk.push(nbTwin);
                    visitedTwin.put(manht, stk);
                }
                int moves = curNodeTwin.numberOfMoves()+1;
                pqTwin.insert(new Node(nbTwin, manht+moves, moves, curNodeTwin.board()));
            }
        }
        solvable = sol;
        if (!solvable) {
            numberOfMoves = -1;
            return;
        }
        numberOfMoves = curNode.numberOfMoves();
        solution.push(curNode.board());
        while (curNode.father() != null) {
            solution.push(curNode.father());
            for (Node n : visited.get(curNode.father().manhattan())) {
                if (curNode.father().equals(n.board()) && curNode.numberOfMoves() == n.numberOfMoves()+1) {
                    curNode = n;
                    break;
                }
            }
        }
        Collections.reverse(solution);
    }
        
    public boolean isSolvable() { // is the initial board solvable? 
        return solvable;
    }
    
    public int moves() { // min number of moves to solve initial board; -1 if unsolvable 
        return numberOfMoves;
    }
    
    public Iterable<Board> solution() { // sequence of boards in a shortest solution; null if unsolvable 
        if (isSolvable()) return (Iterable<Board>) solution.clone();
        return null;
    }
    
    public static void main(String[] args) { // solve a slider puzzle
        // create initial board from file 
        In in = new In(args[0]); 
        int n = in.readInt(); 
        int[][] blocks = new int[n][n]; 
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
            blocks[i][j] = in.readInt(); 
        Board initial = new Board(blocks); // solve the puzzle 
        Solver solver = new Solver(initial); // print solution to standard output 
        if (!solver.isSolvable()) 
            StdOut.println("No solution possible"); 
        else { 
            StdOut.println("Minimum number of moves = " + solver.moves()); 
            for (Board board : solver.solution()) 
                StdOut.println(board); 
        }
    }
}