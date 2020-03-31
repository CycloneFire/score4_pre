package cpsc101.bluemountian.model.player;

import cpsc101.bluemountian.model.board.*;

import java.awt.*;
import java.util.Random;

public class ArtificialPlayer extends Player implements CanPlay{
    private Move move;
    private Boolean hasMove = false;
    private Player opposition;
    private final static int SEARCH_DEPTH = 5;

    public ArtificialPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Move getMove() {
        hasMove=false;
        return move;
    }

    @Override
    public void resetMove() {
        hasMove=false;
    }

    public void setRandomMove(){
        hasMove=true;
        Random random = new Random();
        int randomPeg=random.nextInt(16);
        //System.out.println(((randomPeg/4)-1)+" good "+((randomPeg%4)));
        move=new Move(randomPeg/4,randomPeg%4);
    }

    public void setMove(Board board,Player opposition){
        hasMove=true;
        this.opposition=opposition;
        move=null;  // Empty the move
        miniMax(board,SEARCH_DEPTH,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
        if(move==null){ // If there are no good moves (all searched moves cause player to lose)
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if(board.getPeg(new Move(i,j)).canPlace()){
                        move=new Move(i,j); // Place on first available cell
                        return;
                    }
                }

            }
        }
    };

    // Ahead is miniMax algorithm layered with alpha-beta pruning.
    private int miniMax(Board board, int depth,int alpha,int beta, boolean maximizingPlayer){
        if(depth==0 || new WinningCondition(board,this.getColor()).winCheck().isSet()){
            // Stop evaluating once highest depth is reached or game has ended
            return (new EvaluateBoard(board,this).getScore());
        }
        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 4; i++) {   // Looping through all available cells
                for (int j = 0; j < 4; j++) {
                        Board modifiedBoard = new Board();
                        modifiedBoard.setPegs(board.getPegs());
                        // Making a clone of current board, so as to not change it when predicting.
                        if(board.getPeg(new Move(i,j)).canPlace()){
                            modifiedBoard.addBead(new Move(i,j),this);
                            int eval = miniMax(modifiedBoard,depth-1,alpha,beta,false);
                            if(eval>maxEval){   // Basic miniMax application
                                maxEval=eval;
                                if(depth==SEARCH_DEPTH){    // Set move if highest result is found.
                                    move=new Move(i,j);
                                }

                            }
                            alpha = Math.max(alpha,eval);   // Alpha-Beta Pruning
                            if(alpha>=beta){
                                return alpha;
                            }
                        }
                }
            }
            return maxEval;
        }else{
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 4; i++) {   // Looping through all available cells
                for (int j = 0; j < 4; j++) {
                        Board modifiedBoard = new Board();
                        modifiedBoard.setPegs(board.getPegs());
                        // Making a clone of current board, so as to not change it while predicting.
                        if(board.getPeg(new Move(i,j)).canPlace()){
                            modifiedBoard.addBead(new Move(i,j),opposition);
                            int eval = miniMax(modifiedBoard,depth-1,alpha,beta,true);
                            if(eval<minEval){   // Basic miniMax application
                                minEval=eval;
                            }
                            beta = Math.min(beta,eval); // Alpha-beta pruning
                            if(alpha>=beta){
                                return beta;
                            }
                        }
                }
            }
            return minEval;
        }
    }

}
