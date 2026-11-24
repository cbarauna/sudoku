package br.com.dio.ui.custom.panel;

import br.com.dio.ui.custom.button.DigitButton;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntToLongFunction;

/** Teclado numérico 3x3 para preencher a casa selecionada. */
public class NumberPadPanel extends JPanel {

    private final List<DigitButton> buttons = new ArrayList<>();

    public NumberPadPanel(final IntConsumer onDigit) {
        super(new GridLayout(3, 3, 8, 8));
        setOpaque(false);
        for (int digit = 1; digit <= 9; digit++) {
            final var value = digit;
            var button = new DigitButton(value, e -> onDigit.accept(value));
            buttons.add(button);
            add(button);
        }
    }

    /** Atualiza quantas vezes cada dígito ainda falta no tabuleiro. */
    public void refresh(final IntToLongFunction countOf) {
        buttons.forEach(b -> b.setRemaining(9 - countOf.applyAsLong(b.getDigit())));
    }

    public void setPadEnabled(final boolean enabled, final IntToLongFunction countOf) {
        if (enabled) {
            refresh(countOf);
        } else {
            buttons.forEach(b -> b.setEnabled(false));
        }
    }
}
