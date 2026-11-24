package br.com.dio.ui.custom.button;

import br.com.dio.ui.custom.theme.Theme;

import java.awt.event.ActionListener;

public class CheckGameStatusButton extends FlatButton {

    public CheckGameStatusButton(final ActionListener actionListener){
        super("Verificar jogo", Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.TEXT, actionListener);
    }

}
