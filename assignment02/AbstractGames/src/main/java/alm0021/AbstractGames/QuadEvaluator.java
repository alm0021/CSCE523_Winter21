package alm0021.AbstractGames;

import alm0021.AbstractGames.LinesOfAction.LOABoard;
import alm0021.AbstractGames.LinesOfAction.LOAPiece;

/**
 * Each game should implement a StaticBoardEvaluator that returns an estimate of
 * the current game's winning potential. It is called from the board's
 * heuristicEvaluation() method.
 *
 * The double returned must be between [-1.0..1.0]. An easy way to scale is to
 * perform a tanh.
 */
public class QuadEvaluator implements StaticBoardEvaluator {
  /**
   * Code for a game's heuristic evaluator. Winands, Mark HM. "Analysis and
   * implementation of Lines of Action." Master's thesis, Department of Computer
   * Science, Universiteit Maastricht (2000).
   * 
   * @param board
   * @return a double value in [-1.0..1.0]
   */
  public double heuristicEvaluation(Board board) {

    LOABoard lboard = new LOABoard();
    lboard.loadBoard(board.toString());

    int currentPlayer = board.getCurrentPlayer();
    int piece_list_count = 0;
    // Get size of piece list

    for (LOAPiece p = lboard.piece_list[currentPlayer]; p != null; p = p.next) {
      piece_list_count++;
    }

    LOAPiece BLACK_list[] = new LOAPiece[piece_list_count];
    int pieceCount = 0;
    LOAPiece pB = lboard.piece_list[currentPlayer];

    // Creat list of pieces
    for (; pB != null; pieceCount++) {
      BLACK_list[pieceCount] = pB;
      pB = pB.next;
    }
    pieceCount = 0;

    // For each side, the centre of mass of the pieces at the board is computed.
    int B_x_ctr = lboard.centerMass_horz(currentPlayer);
    int B_y_ctr = lboard.centerMass_vert(currentPlayer);

    // Second, we compute for each piece its distance to the centre of mass. The
    // distance is measured as the minimal number of squares the piece is removed
    // from the centre of mass. These distances are summed together, called the
    // sum-of-distances.
    int sumOfD_B = manhattnD(BLACK_list, B_x_ctr, B_y_ctr);

    // Third, the sum-of-minimal-distances is calculated. It is defined as the sum
    // of the minimal distances of the pieces from the centre of mass. This
    // computation is necessary since otherwise boards with a few pieces would be
    // preferred. For instance, if we have ten pieces, there will be always at least
    // eight pieces at a distance of 1 from the centre of mass, and one piece at a
    // distance of 2. In this case the total sum of distances is minimal 10. Thus,
    // the sum-of-minimal-distances is subtracted from the sum-of-distances.
    double diffOfD_B = sumOfD_B - minimalD(BLACK_list, B_x_ctr, B_y_ctr);

    // Fourth, the average distance towards the centre of mass is calculated and the
    // inverse of the average distance is defined as the concentration
    double concentration_B = 1.0 / (sumOfD_B / BLACK_list.length);

    // Finally, penalties are given for each piece on the edge.
    double edgePenalty_B = edgePenalty(BLACK_list);

    double euler_B = (lboard.quadcount[currentPlayer][1] - lboard.quadcount[currentPlayer][3]
        - 2 * lboard.quadcount[currentPlayer][5]) / 4;

    return Math.tanh((diffOfD_B * euler_B * concentration_B) - edgePenalty_B);

  }

  /**
   * Calculates sum of Manhattan distance to center of mass
   * 
   * @param list     piece list
   * @param center_x center of mass x component
   * @param center_y center of mass y component
   * @return sum of Manhattan distance to center of mass
   */
  public int manhattnD(LOAPiece[] list, int center_x, int center_y) {
    int sum = 0;

    for (int i = 0; i < list.length; i++) {
      sum += (Math.abs(list[i].x - center_x) + Math.abs(list[i].y - center_y));
    }
    return sum;
  }

  /**
   * Calculates sum of minimal distance to center of mass
   * 
   * @param list     piece list
   * @param center_x center of mass x component
   * @param center_y center of mass y component
   * @return sum of minimal distance to center of mass
   */
  public double minimalD(LOAPiece[] list, int center_x, int center_y) {
    int sum = 0;

    for (int i = 0; i < list.length; i++) {
      sum += Math.sqrt(Math.pow(list[i].x - center_x, 2.0) + Math.pow(list[i].y - center_y, 2.0));
    }
    return sum;
  }

  /**
   * Calculates penalty for pieces on the edge
   * 
   * @param list piece list
   * @return double penalty for pieces on the edge
   */
  public double edgePenalty(LOAPiece[] list) {
    double sum = 0.0;

    for (int i = 0; i < list.length; i++) {
      // check for edge pieces
      if (list[i].y == 0 || list[i].y == 7 || list[i].x == 0 || list[i].x == 7) {
        sum++;
      }
    }
    return sum;
  }
}
