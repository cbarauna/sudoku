package br.com.dio.ui.custom.panel;

import br.com.dio.ui.custom.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainPanel extends JPanel {

    public MainPanel(){
        super(new BorderLayout(24, 18));
        this.setBackground(Theme.BACKGROUND);
        this.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
    }

}
