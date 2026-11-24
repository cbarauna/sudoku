package br.com.dio.ui.custom.button;

import br.com.dio.ui.custom.theme.Theme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

/** Botão com visual plano e cantos arredondados, igual em qualquer sistema operacional. */
public class FlatButton extends JButton {

    private final Color background;
    private final Color hover;
    private final Color foreground;

    public FlatButton(final String text, final Color background, final Color hover,
                      final Color foreground, final ActionListener actionListener) {
        super(text);
        this.background = background;
        this.hover = hover;
        this.foreground = foreground;
        this.setFont(Theme.BUTTON);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setFocusable(false);
        this.setOpaque(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setPreferredSize(new Dimension(150, 40));
        if (actionListener != null) this.addActionListener(actionListener);
    }

    public static FlatButton primary(final String text, final ActionListener listener){
        return new FlatButton(text, Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE, listener);
    }

    public static FlatButton secondary(final String text, final ActionListener listener){
        return new FlatButton(text, Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.TEXT, listener);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        var model = getModel();
        var fill = !isEnabled() ? blend(background, Theme.BACKGROUND, 0.55f)
                : (model.isRollover() || model.isPressed()) ? hover : background;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        paintContent(g2);
        g2.dispose();
    }

    protected void paintContent(final Graphics2D g2){
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? foreground : blend(foreground, Theme.BACKGROUND, 0.5f));
        var fm = g2.getFontMetrics();
        var x = (getWidth() - fm.stringWidth(getText())) / 2;
        var y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);
    }

    protected static Color blend(final Color a, final Color b, final float ratio){
        var r = a.getRed() * (1 - ratio) + b.getRed() * ratio;
        var g = a.getGreen() * (1 - ratio) + b.getGreen() * ratio;
        var bl = a.getBlue() * (1 - ratio) + b.getBlue() * ratio;
        return new Color((int) r, (int) g, (int) bl);
    }
}
