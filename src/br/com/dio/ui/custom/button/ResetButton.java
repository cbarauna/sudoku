package br.com.dio.ui.custom.button;

import br.com.dio.ui.custom.theme.Theme;

import java.awt.Color;
import java.awt.event.ActionListener;

public class ResetButton extends FlatButton {

    public ResetButton(final ActionListener actionListener){
        super("Reiniciar jogo", Theme.DANGER, Theme.DANGER_HOVER, Color.WHITE, actionListener);
    }

}
