package piece;

import main.GamePanel;


public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            image = getImage("white-king");
        } else {
            image = getImage("black-king");
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinTheBoard(targetCol, targetRow)) {
            if ((Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1) ||
                    (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1)) {//diagonaly
                
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;

    }


}
