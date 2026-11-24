package br.com.dio.ui.custom.button;

import br.com.dio.ui.custom.theme.Theme;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

/** Botão do teclado numérico: mostra o dígito e quantas vezes ele ainda falta no tabuleiro. */
public class DigitButton extends FlatButton {

    private final int digit;
    private long remaining = 9;

    public DigitButton(final int digit, final ActionListener actionListener) {
        super(String.valueOf(digit), Theme.SURFACE, Theme.SECONDARY, Theme.PRIMARY, actionListener);
        this.digit = digit;
        this.setPreferredSize(new Dimension(64, 64));
        this.setToolTipText("Inserir %d (tecla %d)".formatted(digit, digit));
    }

    public int getDigit() {
        return digit;
    }

    public void setRemaining(final long remaining) {
        this.remaining = Math.max(0, remaining);
        this.setEnabled(this.remaining > 0);
        repaint();
    }

    @Override
    protected void paintContent(final Graphics2D g2) {
        g2.setColor(Theme.GRID_THIN);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        var color = isEnabled() ? Theme.PRIMARY : Theme.GRID_THIN;
        g2.setFont(Theme.PAD_DIGIT);
        g2.setColor(color);
        var fm = g2.getFontMetrics();
        var text = getText();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2 + fm.getAscent() / 2 - 6);

        g2.setFont(Theme.PAD_COUNT);
        g2.setColor(isEnabled() ? Theme.MUTED_TEXT : Theme.GRID_THIN);
        var count = remaining == 0 ? "completo" : "faltam " + remaining;
        fm = g2.getFontMetrics();
        g2.drawString(count, (getWidth() - fm.stringWidth(count)) / 2, getHeight() - 8);
    }
}
