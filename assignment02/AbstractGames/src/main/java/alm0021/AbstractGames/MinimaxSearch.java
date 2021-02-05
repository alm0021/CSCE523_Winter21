/**
 * Used the following sources:
 * https://www.geeksforgeeks.org/minimax-algorithm-in-game-theory-set-3-tic-tac-toe-ai-finding-optimal-move/
 */


package alm0021.AbstractGames;


public class MinimaxSearch<BOARD extends Board, MOVE extends Move> implements Search<BOARD,MOVE>  {
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
  @SuppressWarnings("unchecked")
  private MOVE Minimax(int depth) {

    Move theBestMove = null;
    double theBestVal = 1000;
    this.maxDepth = depth;

    // generate available moves
    Move movesList = this.board.generateMoves();

    while (movesList != null) {
      // Make move
      this.board.makeMove(movesList);
      // Call new minimax method
      double theMoveVal = selectMinimax(depth);
      // Undo move
      this.board.reverseMove(movesList);
      // Update theBestMove if value of current
      if (theMoveVal < theBestVal) {
        theBestMove = movesList;
        theBestVal = theMoveVal;
      }
      movesList = movesList.next;
    }
    return (MOVE) theBestMove;
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
    if(depth < 0){
      return gameOver();
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

  /**
   * maxMove
   *
   * @param m1 Move to compare
   * @param m2 Move to compare
   * @return Move with higher value
   */
  public MOVE maxMove(MOVE m1, MOVE m2) {
    if (m2 == null) {
      return m1;
    } else if (m1 == null) {
      return m2;
    }
    return m1.value >= m2.value ? m1 : m2;
  }

  /**
   * minMove
   *
   * @param m1 Move to compare
   * @param m2 Move to compare
   * @return Move with lower value
   */
  public MOVE minMove(MOVE m1, MOVE m2) {
    if (m2 == null) {
      return m1;
    } else if (m1 == null) {
      return m2;
    }
    return m1.value <= m2.value ? m1 : m2;
  }
}

