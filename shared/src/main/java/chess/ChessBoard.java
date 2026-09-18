package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int r = position.getRow() - 1;
        int c = position.getColumn() - 1;
        board[r][c] = piece;

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int r = position.getRow() - 1;
        int c = position.getColumn() - 1;
        return board[r][c];
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        ChessPiece.PieceType[] backRow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        for (int col = 0; col <= 7; col++) {
            ChessPiece.PieceType type = backRow[col];
            ChessPiece piece = new ChessPiece(
                    ChessGame.TeamColor.WHITE,
                    type
            );
            addPiece(
                    new ChessPosition(1, col + 1),
                    piece
            );
        }
        for (int col = 0; col <= 7; col++) {
            ChessPiece piece = new ChessPiece(
                    ChessGame.TeamColor.WHITE,
                    ChessPiece.PieceType.PAWN
            );
            addPiece(
                    new ChessPosition(2, col + 1),
                    piece
            );
        }
        for (int col = 0; col <= 7; col++) {
            ChessPiece.PieceType type = backRow[col];
            ChessPiece piece = new ChessPiece(
                    ChessGame.TeamColor.BLACK,
                    type
            );
            addPiece(
                    new ChessPosition(8, col + 1),
                    piece
            );
        }
        for (int col = 0; col <= 7; col++) {
            ChessPiece piece = new ChessPiece(
                    ChessGame.TeamColor.BLACK,
                    ChessPiece.PieceType.PAWN
            );
            addPiece(
                    new ChessPosition(7, col + 1),
                    piece
            );

        }
    }
}