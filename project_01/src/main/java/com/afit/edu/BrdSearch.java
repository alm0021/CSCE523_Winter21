package com.afit.edu;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Hashtable;

class BrdSearch implements Search
{
    private Board board;
    private int count;
    private boolean goalFound; // Have we found the goal?
    private Queue<Board> q = new LinkedList<Board>();
    private Hashtable<String, Integer> discoBoards = new Hashtable<String, Integer>();

    /**
     * Main constructor
     * 
     * @param b   - Board
     */
    public BrdSearch(Board b)
    {
        this.board = b;
        //Add start board to queue
        q.offer(b);
    }

    /**
    * creates string representation of board for the visited list
    *
    * @param b - Board 
    * @return String resultant String representation of the board
    */
    public String toString(Board b) {
        String outString = "";
 
        for (int i = 0; i < Board.BOARD_SIZE + 1; i++)
            for (int j = 0; j < Board.BOARD_SIZE; j++)
                outString = outString.concat(String.valueOf(b.theBoard[i][j]));
        return outString;
    }

    /**
    * Compares two boards using toString
    *
    * @param b - Board
    * @param c - Board 
    * @return boolean if Boards are equal
    */
    public boolean equals(Board b, Board c) {
        return toString(b).equals(toString(c));
    }

    private boolean goalBoard(Board b){
        Piece p = b.piece_list[b.findPiece("X0")];

        return p.name.equals("X0") && p.y == Board.BOARD_EXIT_Y &&
                p.x == Board.BOARD_EXIT_X - 1;
    }

    private boolean containsCopy(LinkedList<Board> list, Board b){
        for (Board value : list) {
            if (equals(b, value)) {
                //System.out.println("Already Discovered");
                return true;
            }
        }
        return false;
    }

    /**
    * Finds number of moves
    *
    * @param m - Move
    * @return number of moves
    */
    private int moveCounter(Move m){
        if (m == null){return 0;}
        if (m.next == null){return 1;}
        int count = 0;
        Move newMove = new Move(m);
        while(newMove.next != null){
            count++;
            newMove = newMove.next;
        }
        return count;
    }

    /**
    * Finds shortest solution
    *
    * @param m - LinkedList<Move>
    * @return shortest move in list
    */
    private Move shortestSln(LinkedList<Move> m){
        Move shortest = m.get(0);
        for (Move move : m) {

            if (moveCounter(move) < moveCounter(shortest)) {
                shortest = move;
            }
        }
        return shortest;
    }

    /**
     * Uses breadth first search to find best move list
     *
     * @return Move for goal Board
     */
    public Move findMoves()
    {

        //Add start board to queue
        while (!goalFound)
        {
            this.board = this.q.poll();
            this.count++; //visit node
            //Check is v is the goal board
            assert this.board != null;
            if(this.board.isGoal()){
                goalFound = true;
                return this.board.move_list;
            }
            //Generate move list (children of v)
            Move w_moves = this.board.genMoves();
            Board w = null;
            //Add v's children moves to the queue
            while(w_moves != null){
                this.board.makeMove(w_moves);
                if (!discoBoards.containsKey(this.board.hashKey())){
                    w = new Board(this.board);
                    discoBoards.put(w.hashKey(),1);
                    //Set the the previous board of w to v?
                    this.q.offer(w);
                }
                this.board.reverseMove(w_moves);
                w_moves = w_moves.next;
            } 
        }
        return null;
    }

    public long nodeCount()
    {
        return this.count;
    }
}