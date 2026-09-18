package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece startingPiece = board.getPiece(startPosition);
        if(startingPiece == null){
            return null;
        }
        var moves = startingPiece.pieceMoves(board, startPosition);


        ArrayList<ChessMove> legalMoves;
        legalMoves = new ArrayList<>();

        for(ChessMove move : moves){
            ChessBoard copyBoard = copyBoard();
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece movingPiece = copyBoard.getPiece(start);
            copyBoard.addPiece(start, null);
            if(move.getPromotionPiece() != null){
                ChessPiece promoted = new ChessPiece(
                        movingPiece.getTeamColor(),
                        move.getPromotionPiece()
                );
                copyBoard.addPiece(end, promoted);
            } else {
                copyBoard.addPiece(end, movingPiece);
            }

            ChessGame testGame = new ChessGame();
            testGame.setBoard(copyBoard);
            if(testGame.isInCheck(startingPiece.getTeamColor()) == false){
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece movingPiece = board.getPiece(start);

        if(movingPiece == null) {
            throw new InvalidMoveException("Invalid Move");
        }

        if(movingPiece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Invalid Move");
        }

        Collection<ChessMove> legal = validMoves(start);
        if(legal == null || !legal.contains(move)){
            throw new InvalidMoveException("Invalid Move");
        }
        board.addPiece(start, null);
        if(move.getPromotionPiece() !=null){
            ChessPiece promoted = new ChessPiece(teamTurn, move.getPromotionPiece());
            board.addPiece(end, promoted);
        } else {
            board.addPiece(end, movingPiece);
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        ChessPosition kingSpot = findKing(teamColor);
        if(kingSpot == null){
            return false;
        }
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition spotCheck = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(spotCheck);
                if(target == null || target.getTeamColor() != enemyColor){
                    continue;
                }
                var moves = target.pieceMoves(board, spotCheck);
                for(ChessMove move : moves){
                    if(move.getEndPosition().equals(kingSpot)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <=8; c++){
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    continue;
                }
                Collection<ChessMove> move = validMoves(pos);
                if(move != null && !move.isEmpty()){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> move = validMoves(pos);
                if (move != null && !move.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor){
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(pos);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return pos;
                }
            }
        }
        return null;
    }

    private ChessBoard copyBoard(){
        ChessBoard copyBoard = new ChessBoard();
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null){
                    copyBoard.addPiece(pos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return copyBoard;
    }
}