package br.com.dio.ui.custom.button;

import br.com.dio.ui.custom.theme.Theme;

import java.awt.Color;
import java.awt.event.ActionListener;

public class FinishGameButton extends FlatButton {

    public FinishGameButton(final ActionListener actionListener){
        super("Concluir", Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE, actionListener);
    }

}
