package rushhour.afit.edu;

import java.util.LinkedList;
import java.util.Queue;

class BrdSearch implements Search
{
    private Board board;
    private int count;

    /**
     * Main constructor
     * 
     * @param b   - Board
     */
    public BrdSearch(Board b)
    {
        this.board = b;
    }

    /**
    * creates string representation of board for the visited list
    *
    * @param b - Board 
    * @return String resultant String representation of the board
    */
    public String toString(Board b) {
        String outString = new String();
 
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

        if ( p.name.equals("X0") && p.y == Board.BOARD_EXIT_Y && 
       p.x == Board.BOARD_EXIT_X - 1) {
                return true;
        }
            return false;
    }

    private boolean containsCopy(LinkedList<Board> list, Board b){
        for (int i = 0; i < list.size(); i++){
            if (equals(b,list.get(i))){
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
        for (int i = 0; i < m.size(); i++){
            
            if(moveCounter(m.get(i)) < moveCounter(shortest)){
                shortest = m.get(i);
            }
        }
        return shortest;
    }

    public Move findMoves()
    {   
        Board v = new Board(this.board);
        LinkedList<Board> discoBoards = new LinkedList<Board>();
        LinkedList<Move> solutions = new LinkedList<Move>();
        Queue<Board> q = new LinkedList<Board>();
        discoBoards.add(v);
        //Add start board to queue
        q.add(v);
        while (!q.isEmpty())
        {
            v = q.remove();
            this.count++; //visit node
            //Check is v is the goal board
            if(goalBoard(v)){
                return v.move_list;
            }
            //Generate move list (children of v)
            Move w_moves = v.genMoves();
            Board w = null;
            //Add v's children moves to the queue
            while(w_moves.next != null){
                w = new Board(v);
                w.makeMove(w_moves);
                if (!containsCopy(discoBoards, w)){
                    discoBoards.add(w);
                    //Set the the previous board of w to v?
                    q.add(w);
                    w_moves = w_moves.next;
                }
                else{w_moves = w_moves.next;}
            } 
        }
        return v.move_list;
    }

    public long nodeCount()
    {
        return this.count;
    }
}