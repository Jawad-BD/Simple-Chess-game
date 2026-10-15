package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {
    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow; // pre == previous
    public int color;
    public Piece hittingP;
    public boolean moved ,twoStepped;


    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage img = null;

        try {
            // System.out.println("ROOT = " + getClass().getResource("/"));
            // System.out.println("FILE = " + getClass().getResource("/resources/piece/" + imagePath + ".png"));

            // Fixed: absolute path from classpath root
            img = ImageIO.read(getClass().getResourceAsStream("/resources/piece/" + imagePath + ".png"));

            if (img == null) {
                System.err.println("Image not found: " + imagePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    }

    //adjusts the position and place the piece at the center of the square
    public void updatePosition() {

        if (type==Type.PAWN) {
            if (Math.abs(row-preRow) == 2) {
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
        
    }
    
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinTheBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        if (targetCol == preCol && targetRow == preRow) {
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for(Piece piece : GamePanel.simPieces){
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);

        if (hittingP == null) { //this square is free

            return true;

        } else {//the squre is occupied

            if (hittingP.color != this.color) {// if the color is different the piece can be captured    
                return true;
            } else {
                hittingP = null;
            }
        }
        return false;

    }
    
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        //When the piece is moving to the left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        //When the piece is moving to the right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        //When the piece is moving Up
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        //When the piece is moving Down
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        
        if (targetRow < preRow) {
            //Up left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }


            //Up Right

            for(int c = preCol + 1;c<targetCol;c++){
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }

        }
        if (targetRow > preRow) {
            //Down left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }

             //Down Right
             for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }
        }
            

       
        
        
        
        
        
        
        return false;
    }


    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
        }
    }
}
