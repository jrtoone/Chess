package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();

        switch (pieceType) {
            case KING -> kingMoves(board, myPosition, moves);
            case KNIGHT -> knightMoves(board, myPosition, moves);
            case ROOK -> rookMoves(board, myPosition, moves);
            case BISHOP -> bishopMoves(board, myPosition, moves);
            case QUEEN -> queenMoves(board, myPosition, moves);
            case PAWN -> pawnMoves(board, myPosition, moves);
            default -> { }
        }
        return moves;
    }

    private void kingMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        int r = from.getRow();
        int c = from.getColumn();
        int[][] deltas = {
                {-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}
        };
        for (int[] d : deltas){
            int newRow = r + d[0];
            int newCol = c + d[1];

            if(!isOnBoard(newRow, newCol)){
                continue;
            }

            ChessPosition to = new ChessPosition(newRow, newCol);
            ChessPiece target = board.getPiece(to);

            if (target == null || target.getTeamColor() != this.teamColor) {
                moves.add(new ChessMove(from, to, null));
            }
        }

    }

    private void knightMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        int r = from.getRow();
        int c = from.getColumn();
        int[][] deltas = {
                { 2, 1}, { 2,-1},
                {-2, 1}, {-2,-1},
                { 1, 2}, { 1,-2},
                {-1, 2}, {-1,-2}
        };
        for (int[] d : deltas){
            int newRow = r + d[0];
            int newCol = c + d[1];

            if (!isOnBoard(newRow, newCol)){
                continue;
            }
            ChessPosition to = new ChessPosition(newRow, newCol);
            ChessPiece target = board.getPiece(to);

            if(target == null || target.getTeamColor() != this.teamColor){
                moves.add(new ChessMove(from, to, null));
            }
        }
    }

    private void bishopMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        int r = from.getRow();
        int c = from.getColumn();
        int[][] directions = {
                {1,1},{1,-1},{-1,1},{-1,-1}
        };
        for(int[] dir : directions){
            int dr = dir[0];
            int dc = dir[1];

            int newRow = r + dr;
            int newCol = c + dc;

            while (isOnBoard(newRow, newCol)){
                ChessPosition to = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(to);

                if (target == null){
                    moves.add(new ChessMove(from, to, null));
                } else {
                    if (target.getTeamColor() != this.teamColor){
                        moves.add(new ChessMove(from, to, null));
                    }
                    break;
                }
                newRow += dr;
                newCol += dc;
            }
        }
    }
    private void rookMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        int r = from.getRow();
        int c = from.getColumn();
        int[][] directions = {
                {1,0},{-1,0},{0,-1},{0,1}
        };
        for(int[] dir : directions){
            int dr = dir[0];
            int dc = dir[1];

            int newRow = r + dr;
            int newCol = c + dc;

            while (isOnBoard(newRow, newCol)){
                ChessPosition to = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(to);

                if (target == null){
                    moves.add(new ChessMove(from, to, null));
                } else {
                    if (target.getTeamColor() != this.teamColor){
                        moves.add(new ChessMove(from, to, null));
                    }
                    break;
                }
                newRow += dr;
                newCol += dc;
            }
        }
    }

    private void queenMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        rookMoves(board, from, moves);
        bishopMoves(board, from, moves);
    }

    private void addPromotions(ChessPosition from, ChessPosition to, ArrayList<ChessMove> moves){
        moves.add(new ChessMove(from, to, PieceType.QUEEN));
        moves.add(new ChessMove(from, to, PieceType.ROOK));
        moves.add(new ChessMove(from, to, PieceType.BISHOP));
        moves.add(new ChessMove(from, to, PieceType.KNIGHT));

    }

    private void pawnMoves(ChessBoard board, ChessPosition from, ArrayList<ChessMove> moves){
        int r = from.getRow();
        int c = from.getColumn();

        int dir = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int oneStepRow = r + dir;
        if(isOnBoard(oneStepRow, c)) {
            ChessPosition oneStep = new ChessPosition(oneStepRow, c);

            if(board.getPiece(oneStep) == null){
                if(oneStepRow == promotionRow){
                    addPromotions(from, oneStep, moves);
                } else {
                    moves.add(new ChessMove(from, oneStep, null));
                }
                if (r == startRow){
                    int twoStepRow = r + 2 * dir;
                    ChessPosition twoStep = new ChessPosition(twoStepRow, c);
                    if(isOnBoard(twoStepRow, c) && board.getPiece(twoStep) == null){
                        moves.add(new ChessMove(from, twoStep, null));
                    }
                }
            }


            int[][] captureDeltas = { { dir, -1}, {dir, 1} };
            for (int[] d: captureDeltas){
                int newRow = r + d[0];
                int newCol = c + d[1];

                if(!isOnBoard(newRow, newCol)){
                    continue;
                }

                ChessPosition to = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(to);

                if (target != null && target.getTeamColor() != this.teamColor){
                    if(newRow == promotionRow){
                        addPromotions(from, to, moves);
                    } else {
                        moves.add(new ChessMove(from, to, null));
                    }
                }
            }
        }
    }

    private boolean isOnBoard(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }
}