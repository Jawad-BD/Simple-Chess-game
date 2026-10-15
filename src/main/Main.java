package main;


import javax.swing.JFrame;

public class Main{
    public static void main(String[] args) {

        JFrame window = new JFrame("Simple Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//the programm closes if u closed the window
        window.setResizable(false);//you cannot resize this window

        //Add GamePanel to the window
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
                 

    }
}