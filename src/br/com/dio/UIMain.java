package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;
import br.com.dio.util.GameConfigParser;

import javax.swing.SwingUtilities;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class UIMain {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> {
            try {
                var gameConfig = GameConfigParser.parse(args);
                new MainScreen(gameConfig).buildMainScreen();
            } catch (IllegalArgumentException e) {
                showMessageDialog(null, e.getMessage(), "Configuração do jogo inválida", ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

}
