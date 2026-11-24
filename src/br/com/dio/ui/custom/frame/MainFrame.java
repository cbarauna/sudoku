package br.com.dio.ui.custom.frame;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public MainFrame(final JPanel mainPanel){
        super("Sudoku");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }

}
