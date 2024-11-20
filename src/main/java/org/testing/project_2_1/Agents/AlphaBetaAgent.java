package org.testing.project_2_1.Agents;
import org.testing.project_2_1.GameLogic.GameLogic;
import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.Turn;

import java.util.List;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AlphaBetaAgent implements Agent {
    private GameLogic gameLogic;
    private boolean isWhite;

    private int maxDepth;

    public AlphaBetaAgent(boolean isWhite, int maxDepth) {
        this.isWhite=isWhite;
        this.maxDepth=maxDepth;
    }

    public void setGameLogic(GameLogic gameLogic) { this.gameLogic = gameLogic; }
    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public void makeMove() {
        System.out.println("Alpha-Beta agent making move");
        PauseTransition pause = new PauseTransition(Duration.seconds(Agent.delay));
        pause.setOnFinished(event -> {
            if (gameLogic.g.getIsWhiteTurn() == isWhite && !gameLogic.g.isGameOver()) {
                List<Turn> turns = GameLogic.getLegalTurns(gameLogic.g);
                Turn bestTurn = getBestTurn(turns);
                Move move = bestTurn.getMoves().remove(0);
                gameLogic.takeMove(move);
            }
        });
        pause.play();
    }

    private Turn getBestTurn(List<Turn> turns) {
        Turn bestTurn = null;
        int bestValue;
        if (isWhite) {
            bestValue = Integer.MIN_VALUE;
        } else {
            bestValue = Integer.MAX_VALUE;
        }

        for (Turn turn : turns) {
            GameState newState = new GameState(gameLogic.g);
            for (Move move : turn.getMoves()) {
                newState.move(move);
            }

            int boardValue = minimaxPruning(newState, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            if (isWhite && boardValue > bestValue) {
                bestValue = boardValue;
                bestTurn = turn;
            } else if (!isWhite && boardValue < bestValue) {
                bestValue = boardValue;
                bestTurn = turn;
            }
        }
        return bestTurn;
    }

    public int minimaxPruning(GameState gameState, int depth, int alpha, int beta, boolean isMaxPlayerWhite) {
        if (depth == 0 || gameLogic.g.isGameOver()) {
            return (int) GameLogic.evaluateBoard(gameState);
        }

        List<Turn> legalTurns = GameLogic.getLegalTurns(gameState);
        boolean maxPlayer = (gameState.getIsWhiteTurn() == isMaxPlayerWhite); // changed parameter to decrease complexity

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimaxPruning(newState, depth - 1, alpha, beta, isMaxPlayerWhite);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) { // Beta cutoff, pruning is done here
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Turn turn : legalTurns) {
                GameState newState = new GameState(gameState);
                for (Move move : turn.getMoves()) {
                    newState.move(move);
                }

                int eval = minimaxPruning(newState, depth - 1, alpha, beta, isMaxPlayerWhite);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) { // Alpha cutoff, pruning is done here
                    break;
                }
            }
            return minEval;
        }
    }
    public Agent reset() {
        return new AlphaBetaAgent(isWhite, maxDepth);
    }

    @Override
    public void simulate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simulate'");
    }
}
