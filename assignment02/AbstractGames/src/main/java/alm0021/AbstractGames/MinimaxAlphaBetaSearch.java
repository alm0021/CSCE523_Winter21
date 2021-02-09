/**
 * Used the following sources:
 * https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-3-tic-tac-toe-ai-finding-optimal-move/
 */


package alm0021.AbstractGames;


public class MinimaxAlphaBetaSearch<BOARD extends Board, MOVE extends Move> implements Search<BOARD,MOVE>  {
    BOARD board;
    int totalNodesSearched;
    int totalLeafNodes;
    int maxDepth;

    @Override
    public MOVE findBestMove(BOARD board, int depth) {
        MOVE best_move = null;
        int runningNodeTotal = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long currentPeriod;
        long previousPeriod = 0;
        int i = 1;

        this.board = board;

        // Including the iterative deepening for consistency.
        while ( i <= depth ) {
            totalNodesSearched = totalLeafNodes = 0;

            best_move = Minimax(i); // Min-Max alpha beta with transposition tables

            elapsedTime = System.currentTimeMillis()-startTime;
            currentPeriod = elapsedTime-previousPeriod;
            double rate = 0.0;
            if ( i > 3 && previousPeriod > 50 )
                rate = (currentPeriod - previousPeriod)/previousPeriod;
            previousPeriod = elapsedTime;

            runningNodeTotal += totalNodesSearched;
            System.out.println("Depth: " + i +" Time: " + elapsedTime/1000.0 + " " + currentPeriod/1000.0 + " Nodes Searched: " +
                    totalNodesSearched + " Leaf Nodes: " + totalLeafNodes + " Rate: " + rate);

            // increment indexes;
            i = i + 2;
        }

        System.out.println("Nodes per Second = " + runningNodeTotal/(elapsedTime/1000.0));
        if (best_move == null ) {
            throw new Error ("No Move Available - Search Error!");
        }
        return best_move;
    }

    /**
     * Minimax
     *
     * @param depth Depth to search to
     * @return best move found at this node
     */
    public MOVE Minimax(int depth) {
        return max(depth, -1000.0, 1000.0);
    }

    /**
     * max
     *
     * @param depth Depth to search to
     * @param alpha
     * @param beta
     * @return best move found at this node
     */
    @SuppressWarnings("unchecked")
    public MOVE max(int depth, double alpha, double beta) {
        totalNodesSearched++;
        MOVE return_move = (MOVE) board.newMove();
        MOVE opp_move = (MOVE) board.newMove();

        // Terminating conditions - check for winner, draw, or continue
        int game_value = board.endGame();
        if(game_value != board.GAME_CONTINUE){
            if (game_value == board.getCurrentPlayer()){ //Current Player wins
                return_move.value = (20.0 - depth);
                return return_move;
            }
            if (game_value == 0){ //Opponent Wins
                return_move.value = -(20.0 - depth);
                return return_move;
            }
            else if (game_value == 1){ //Opponent Wins
                return_move.value = -(20.0 - depth);
                return return_move;
            }
            if(game_value == board.GAME_DRAW) { // Draw
                return_move.value = 0.0;
                return return_move;
            }
        }

        // Depth check - If depth reached, evaluate board and return result
        if(depth <= 0){
            totalLeafNodes++;
            return_move.value = board.heuristicEvaluation(); // Game will not be completed so use heuristic to evaluate board
            return return_move;
        }

        // Generate moves
        MOVE move_list = (MOVE) board.generateMoves();
        if(move_list == null){
            return_move.value = -(20.0 - depth); // If no available moves, assign neg value
            return return_move;
        }

        // Evaluate each move in the list
        for(MOVE m = move_list; m != null; m = (MOVE) m.next){
            board.makeMove(m); // make move

            opp_move = min(depth-1, alpha, beta); // call min function for opponent move
            if (opp_move.value > alpha){ //assign new alpha if new move is greater
                return_move = m;
                return_move.value = opp_move.value;
                alpha = Math.max(alpha, return_move.value);
            }
            board.reverseMove(m); // undo move
        }
        if (alpha >= beta){ // Prune
            return return_move;
        }

        return return_move;
    }

    /**
     * min
     *
     * @param depth Depth to search to
     * @param alpha
     * @param beta
     * @return best move found at this node
     */
    public MOVE min(int depth, double alpha, double beta){
        totalNodesSearched++;
        MOVE return_move = (MOVE) board.newMove();
        MOVE opp_move = (MOVE) board.newMove();

        // Terminating conditions - check for winner, draw, or continue
        int game_value = board.endGame();
        if(game_value != Board.GAME_CONTINUE){
            if (game_value == board.getCurrentPlayer()){ //Current Player wins
                return_move.value = -(20.0 - depth);
                return return_move;
            }
            if (game_value == 0){ //Opponent Wins
                return_move.value = (20.0 - depth);
                return return_move;
            }
            else if (game_value == 1){ //Opponent Wins
                return_move.value = (20.0 - depth);
                return return_move;
            }
            if(game_value == Board.GAME_DRAW) { // Draw
                return_move.value = 0.0;
                return return_move;
            }
        }

        // Depth check - If depth reached, evaluate board and return result
        if(depth <= 0){
            totalLeafNodes++;
            return_move.value = board.heuristicEvaluation(); // Game will not be completed so use heuristic to evaluate board
            return return_move;
        }

        // Generate moves
        MOVE move_list = (MOVE) board.generateMoves();
        if(move_list == null){
            return_move.value = (20.0 - depth); // If no available moves, assign neg value
            return return_move;
        }

        // Evaluate each move in the list
        for(Move m = move_list; m != null; m = (MOVE) m.next){
            board.makeMove(m); // make move

            opp_move = max(depth-1, alpha, beta); // call max function for opponent move
            if (opp_move.value < beta){ //assign new beta if new move is greater
                return_move = (MOVE) m;
                return_move.value = opp_move.value;
                beta = Math.min(beta, return_move.value);
            }
            board.reverseMove(m); // undo move
            if (beta <= alpha){ // Prune
                return return_move;
            }
        }

        return return_move;
    }

    /**
     * gameOver
     *
     * @return score of board if terminal state
     */
    public int gameOver() {
        // Terminating conditions
        if (this.board.endGame() == 1) { // BLACK WINNER
            return 10;
        } else if (this.board.endGame() == 0) { // WHITE WINNER
            return -10;
        } else { // GAME DRAW or CONTINUE
            return 0;
        }
    }

    /**
     * selectMinimax
     *
     * @param depth depth of search
     * @return value of best move
     */
    public double selectMinimax(int depth) {
        int score = gameOver();

        // If BLACK wins, return score
        if (score == 10) {
            totalLeafNodes++;
            return score - depth;
        }
        // If WHITE wins, return score
        if (score == -10) {
            totalLeafNodes++;
            return score - depth;
        }
        // If DRAW, return 0
        if (this.board.endGame() == BOARD.GAME_DRAW) {
            totalLeafNodes++;
            return 0;
        }

        //Depth check
        if(depth > this.maxDepth){
            return 0;
        }

        // If player is BLACK, select MAX value Move
        if (this.board.getCurrentPlayer() == 1) {
            Double best = -1000.0;
            Move maxMoves = this.board.generateMoves();
            // Traverse all available moves
            while (maxMoves != null) {
                // Make the move
                this.board.makeMove(maxMoves);
                totalNodesSearched++;
                // Use Minimax to get max value
                best = Math.max(best, selectMinimax(depth + 1));
                // reverse the move
                this.board.reverseMove(maxMoves);
                maxMoves = maxMoves.next;
            }
            return best;

        }
        // If player is WHITE, select MIN value Move
        else {
            Double best = 1000.0;
            Move minMoves = this.board.generateMoves();
            // Traverse all available moves
            while (minMoves != null) {
                // Make the move
                this.board.makeMove(minMoves);
                totalNodesSearched++;
                // Use Minimax to get max value
                best = Math.min(best, selectMinimax(depth + 1));
                // reverse the move
                this.board.reverseMove(minMoves);
                minMoves = minMoves.next;
            }
            return best;
        }
    }
}

