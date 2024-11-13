package org.testing.project_2_1.GameLogic;
import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.util.ArrayList;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.InvalidMove;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.NormalMove;
import org.testing.project_2_1.Moves.Turn;

public class GameState {
    protected Tile[][] board;
    protected boolean isWhiteTurn;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Move> movesPlayed;
    private ArrayList<Turn> turnsPlayed;
    private Turn currentTurn;

    public GameState(){
        isWhiteTurn = true;
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        turnsPlayed = new ArrayList<Turn>();
        currentTurn = new Turn();
        board = new Tile[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;

                if (y <= 3 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.BLACK, x, y);
                    tile.setPiece(piece);
                    blackPieces.add(piece);
                } else if (y >= 6 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.WHITE, x, y);
                    tile.setPiece(piece);
                    whitePieces.add(piece);
                }

            }
        }

    }

    public GameState(GameState originalB) {
        isWhiteTurn = true;
        whitePieces = new ArrayList<Piece>();
        blackPieces = new ArrayList<Piece>();
        movesPlayed = new ArrayList<Move>();
        turnsPlayed = new ArrayList<Turn>();
        currentTurn = new Turn();
        this.isWhiteTurn = originalB.getIsWhiteTurn();
        this.board = new Tile[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.board[x][y] = new Tile(x, y);
                if (originalB.board[x][y].hasPiece()) {
                    Piece originalPiece = originalB.board[x][y].getPiece();
                    Piece piece = new Piece(originalPiece.type, x, y);
                    this.board[x][y].setPiece(piece);
                    if (piece.type == PieceType.WHITE || piece.type == PieceType.WHITEKING) {
                        this.whitePieces.add(this.board[x][y].getPiece());
                    } 
                    else {
                        this.blackPieces.add(this.board[x][y].getPiece());
                    }
                }
            }
        }
        // copy originalB.turnsPlayed to bCopy.turnsPlayed
        this.turnsPlayed = new ArrayList<Turn>();
        for (Turn turn : originalB.getTurnsPlayed()) {
            Turn turnCopy = new Turn(turn);
            turnsPlayed.add(turnCopy);
        }
        this.movesPlayed = new ArrayList<Move>();
        for (Move move : originalB.getMovesPlayed()) {
            movesPlayed.add(move);
        }
    }

    public Tile[][] getBoard() {
        return board;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<Move> getMovesPlayed() {
        return movesPlayed;
    }

    public ArrayList<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }

    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    public boolean move(Move move) {
        movesPlayed.add(move);
        currentTurn.addMove(move);
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        piece.movePiece(move);
        board[move.getToX()][move.getToY()].setPiece(piece);
        board[move.getFromX()][move.getFromY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCaptureAtX()][capture.getCaptureAtY()].getPiece();
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
            removeCapturedPieceFromLists(capturedPiece);
        }
        if (move.isTurnEnding()) {
            turnsPlayed.add(currentTurn);
            currentTurn = new Turn();
            switchTurn();
        }
        return true;
    }

    public boolean undoMove(Move move){
        movesPlayed.remove(move);
        currentTurn.removeMove(move);
        Piece piece = board[move.getToX()][move.getToY()].getPiece();
        piece.undoMove(move);
        board[move.getFromX()][move.getFromY()].setPiece(piece);
        board[move.getToX()][move.getToY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = new Piece(capture.getCapturedPiece().getType(), capture.getCaptureAtX(), capture.getCaptureAtY());
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
            addCapturedPieceToLists(capturedPiece);
        }
        if (move.isTurnEnding()) {
            currentTurn = turnsPlayed.get(turnsPlayed.size() - 1);
            turnsPlayed.remove(currentTurn);
            switchTurn();
        }
        return true;
    }

    
    private void removeCapturedPieceFromLists(Piece piece){
        if (piece.type.color.equals("white")) {
            whitePieces.remove(piece);
        }
        else {
            blackPieces.remove(piece);
        }
    }

    private void addCapturedPieceToLists(Piece piece){
        if (piece.type.color.equals("white")) {
            whitePieces.add(piece);
        }
        else {
            blackPieces.add(piece);
        }
    }

    public Piece getPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].getPiece();  // Assuming each tile has a method getPiece()
        }
        return null;
    }

    public boolean hasPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].hasPiece();
        }
        return false;
    }

    public boolean getIsWhiteTurn() {
        return isWhiteTurn;
    }
    
    public Move determineMoveType(int x0, int y0, int newX, int newY) {
        Piece piece = board[x0][y0].getPiece();
        Tile tile = board[newX][newY];

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is empty
        if (tile.hasPiece()) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the tile is black
        if (!tile.isBlack()) {
            System.out.println("white tile");
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // If the piece is a king, allow all types of moves and captures
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            // Check for normal king move any direction (multi-tile)
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }

            // Check for king capture any direction
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        else {
            // Normal diagonal move for regular pieces
        if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {return new NormalMove(x0, y0, piece, newX, newY);}


        // Horizontal capture logic for normal pieces
        if (newY == y0 && Math.abs(newX - x0) == 4) {
            int x1 = (newX + x0) / 2;
            Tile halfWay = board[x1][y0];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Vertical capture logic for normal pieces
        if (newX == x0 && Math.abs(newY - y0) == 4) {
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x0][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }

        // Diagonal capture logic for normal pieces
        if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
            int x1 = (newX + x0) / 2;
            int y1 = (newY + y0) / 2;
            Tile halfWay = board[x1][y1];
            Piece capturedPiece = halfWay.getPiece();
            if (halfWay.hasPiece() && !capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
    }

    return new InvalidMove(x0, y0, piece, newX, newY);
    }

    // Helper method to check if move is available for king
    private boolean isMoveforKing(int x0, int y0, int newX, int newY) {
        return (x0 == newX || y0 == newY || Math.abs(newX - x0) == Math.abs(newY - y0));
    }

    // Helper method to check if move is diagonal for normal pieces
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    // Check if the path for king movement (diagonal, horizontal, vertical) is clear
    private boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
        int dx = Integer.compare(newX, x0);
        int dy = Integer.compare(newY, y0);
    
        int x = x0 + dx;
        int y = y0 + dy;
    
        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    } 

    // Check if there is a capturable piece on the path
    private boolean isCapturePathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return false;  // Not a move for the burger king
        }

        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX=x;
                        capturedY=y;
                    } else {
                        return false;  // Path is blocked by a friendly piece
                    }
                } else {
                    return false;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            // Add error margin for vertical/horizontal captures
            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return true;  // Immediately after the captured piece
            } else {
                return false;  // Not immediately after
            }
        }

        return false;  // No capturable piece wis found
    }

    // Return the piece to capture along the path
    private Piece getCapturedPieceOnPathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveforKing(x0, y0, newX, newY)) {
            return null;  // Not a move for the burger king
        }
        int dx = Integer.compare(newX , x0);
        int dy = Integer.compare(newY , y0);

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  //Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return null;  // Path is blocked by a friendly piece
                    }
                } else {
                    return null;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }
        // Ensure capturing piece can only land 1 square after the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= 1 && Math.abs(newY - landingY) <= 1) {
                return capturedPiece;  // Valid capture
            } else {
                return null;  // not immediately after
            }
        }

        return null; // No valid capture is found
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!board[x][y].hasPiece())  {
                    sb.append("0 ");
                }
                else {
                    Piece piece = board[x][y].getPiece();
                    if (piece.getType() == PieceType.BLACK) {
                        sb.append("b ");
                    } else if (piece.getType() == PieceType.WHITE) {
                        sb.append("w ");
                    } else if (piece.getType() == PieceType.BLACKKING) {
                        sb.append("B ");
                    } else if (piece.getType() == PieceType.WHITEKING) {
                        sb.append("W ");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(GameState b){
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (board[x][y].hasPiece() != b.getBoard()[x][y].hasPiece()) {
                    return false;
                }
                if (board[x][y].hasPiece() && b.getBoard()[x][y].hasPiece()) {
                    if (!board[x][y].getPiece().equals(b.getBoard()[x][y].getPiece())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Piece getPiece(int x, int y) {
        return board[x][y].getPiece();
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }
}