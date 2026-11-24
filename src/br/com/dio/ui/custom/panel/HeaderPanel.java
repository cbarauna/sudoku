package br.com.dio.ui.custom.panel;

import br.com.dio.ui.custom.theme.Theme;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Cabeçalho com título, status do jogo, progresso e cronômetro. */
public class HeaderPanel extends JPanel {

    private final JLabel status = new JLabel();
    private final JLabel progress = new JLabel();
    private final JLabel timer = new JLabel("00:00");

    public HeaderPanel() {
        super(new BorderLayout());
        setOpaque(false);

        var left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        var title = new JLabel("Sudoku");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        status.setFont(Theme.LABEL);
        status.setForeground(Theme.MUTED_TEXT);
        left.add(title);
        left.add(status);

        var right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        timer.setFont(Theme.TITLE.deriveFont(24f));
        timer.setForeground(Theme.TEXT);
        timer.setAlignmentX(RIGHT_ALIGNMENT);
        timer.setToolTipText("Tempo de jogo");
        progress.setFont(Theme.LABEL);
        progress.setForeground(Theme.MUTED_TEXT);
        progress.setAlignmentX(RIGHT_ALIGNMENT);
        right.add(timer);
        right.add(progress);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setStatus(final String text, final java.awt.Color color) {
        status.setText(text);
        status.setForeground(color);
    }

    public void setProgress(final long filled, final long total) {
        progress.setText("%d de %d casas preenchidas".formatted(filled, total));
    }

    public void setTime(final String time) {
        timer.setText(time);
    }
}
