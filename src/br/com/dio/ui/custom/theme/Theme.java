package br.com.dio.ui.custom.theme;

import java.awt.Color;
import java.awt.Font;

public final class Theme {

    private Theme() {}

    public static final Color BACKGROUND = new Color(0xF3F5FA);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(0x1F2937);
    public static final Color MUTED_TEXT = new Color(0x6B7280);

    public static final Color GRID_THIN = new Color(0xC9D1E0);
    public static final Color GRID_THICK = new Color(0x344055);

    public static final Color FIXED_TEXT = new Color(0x1F2937);
    public static final Color USER_TEXT = new Color(0x2F6FED);
    public static final Color HINT_TEXT = new Color(0x0E9F6E);
    public static final Color ERROR_TEXT = new Color(0xDC2626);

    public static final Color FIXED_BG = new Color(0xF7F8FB);
    public static final Color PEER_BG = new Color(0xE7EDF8);
    public static final Color SAME_NUMBER_BG = new Color(0xCFDDF7);
    public static final Color SELECTED_BG = new Color(0xA9C6F7);
    public static final Color ERROR_BG = new Color(0xFBDADA);

    public static final Color PRIMARY = new Color(0x2F6FED);
    public static final Color PRIMARY_HOVER = new Color(0x2459C4);
    public static final Color SECONDARY = new Color(0xE3E8F2);
    public static final Color SECONDARY_HOVER = new Color(0xD3DAE8);
    public static final Color DANGER = new Color(0xE5484D);
    public static final Color DANGER_HOVER = new Color(0xC93B40);
    public static final Color SUCCESS = new Color(0x0E9F6E);

    public static final String FONT_FAMILY = "SansSerif";
    public static final Font TITLE = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font LABEL = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font LABEL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font CELL_FIXED = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font CELL_USER = new Font(FONT_FAMILY, Font.PLAIN, 26);
    public static final Font PAD_DIGIT = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font PAD_COUNT = new Font(FONT_FAMILY, Font.PLAIN, 11);

}
