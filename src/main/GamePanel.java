package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();
    //PIECES
    public  static  ArrayList<Piece> pieces =new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> simPiecesCopy = new ArrayList<>();
    Piece activeP;
    public  static Piece castlingP;

    //COLORS
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //BOOLEANS
    boolean canMove;
    boolean validSquare;



    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        setPieces();
        copyPieces(pieces,simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();//this calls the run methode on the 26 line
    }

    public void setPieces() {
        //White team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
       
        // pieces.add(new Knight(WHITE, 1, 7));
        // pieces.add(new Knight(WHITE, 6, 7));
        // pieces.add(new Bishop(WHITE, 2, 7));
        // pieces.add(new Bishop(WHITE, 5, 7));
        // pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //Black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));

    }

    private void copyPieces(ArrayList<Piece> source,ArrayList<Piece> target){
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread!=null) {
            currentTime = System.nanoTime();
            
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                // Create a snapshot for rendering
                synchronized(this) {
                    copyPieces(simPieces, simPiecesCopy);
                }
                repaint();
                delta--;
            }
        }
        
    }

    
    //this methode is gonna handle all the updating stuff(piece X and Y pos or the number of the pieces)
    private void update() {
        if (mouse.pressed) {
                if (activeP == null) {
                // If the activeP is null,check if you can pick up a piece
                for (Piece piece : simPieces) {
                    //If The mouse is on a ally piece ,pick it up  as the activeP
                    if (piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            piece.row == mouse.y / Board.SQUARE_SIZE) {
                        activeP = piece;
                    }
                }
                //If the played is already holding a piece(THINKING PHASE)
            } else {
                simulate();
            }
        }
        ///////MOUSE BUTTON RELEASED/////////
        if (mouse.pressed == false) {
            if (activeP != null) {
                if (validSquare) {
                    //MOVE CONFIRMED
                    //Update the piece list in case  a piece  has been captured  and removed during   the simulation
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if (castlingP!=null) {
                        castlingP.updatePosition();
                    }
                    changeTurn();
                } else {
                    //the move is not valid so reset everything
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

    private void simulate() {
        canMove = false;
        validSquare = false;

        //reset the pieces list in every loop
        //This is basically for restoring the removed piece during the simulation 
        copyPieces(pieces, simPieces);
        //reset castling piece pos

        if (castlingP!=null) {
            castlingP.col=castlingP.preCol;
            castlingP.x =castlingP.getX(castlingP.col);
            castlingP=null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;//adjust the pointer te center of the image
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            //if hitting a piece ,remove it from the list
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            validSquare = true;
        }
    }
    
    private void checkCastling(){
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            }
            else if(castlingP.col==7){
                castlingP.col -= 2;

            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    public void changeTurn() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
        } else {
            currentColor = WHITE;
        }
        activeP = null;
    }
    //this methode(in JComponent that JPanel inherits and is used  to draw objetcs  on the panel)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //board
        board.draw(g2);
        //pieces 
        synchronized(this) {
            for (Piece p : simPiecesCopy) {
                p.draw(g2);
            }
        }
        if (activeP != null) {

            if (canMove) {
                g2.setColor(Color.green);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            } else {
                g2.setColor(Color.red);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            //draw the active piece in the end so it wont be hidden  by the board or the colored square
            activeP.draw(g2);
            // 

        }
        // Status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(currentColor == WHITE) {
            g2.drawString("White's Turn", 750, 650);
        } else {
            g2.drawString("Black's Turn", 750, 100);
        }
    }
    

}
