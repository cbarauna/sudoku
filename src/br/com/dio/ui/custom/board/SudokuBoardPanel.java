package br.com.dio.ui.custom.board;

import br.com.dio.service.BoardService;
import br.com.dio.service.BoardService.Position;
import br.com.dio.service.EventEnum;
import br.com.dio.service.EventListener;
import br.com.dio.service.NotifierService;
import br.com.dio.ui.custom.theme.Theme;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import static br.com.dio.service.BoardService.BOARD_LIMIT;
import static br.com.dio.service.BoardService.SECTOR_SIZE;
import static br.com.dio.service.EventEnum.SPACE_CHANGED;

/**
 * Tabuleiro desenhado manualmente: seleção com mouse/teclado, destaque de linha, coluna,
 * quadrante e números iguais, e marcação em vermelho de conflitos.
 */
public class SudokuBoardPanel extends JComponent implements EventListener {

    private static final int CELL = 52;
    private static final int SIZE = CELL * BOARD_LIMIT;
    private static final int THICK = 3;

    private final BoardService boardService;
    private final NotifierService notifierService;

    private int selectedCol = 0;
    private int selectedRow = 0;
    private boolean showErrors = false;
    private boolean locked = false;

    public SudokuBoardPanel(final BoardService boardService, final NotifierService notifierService) {
        this.boardService = boardService;
        this.notifierService = notifierService;
        var dimension = new Dimension(SIZE + THICK, SIZE + THICK);
        this.setPreferredSize(dimension);
        this.setMinimumSize(dimension);
        this.setFocusable(true);
        this.setRequestFocusEnabled(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                requestFocusInWindow();
                var col = (e.getX() - originX()) / CELL;
                var row = (e.getY() - originY()) / CELL;
                if (col >= 0 && col < BOARD_LIMIT && row >= 0 && row < BOARD_LIMIT) {
                    select(col, row);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.isControlDown() || e.isMetaDown()) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> moveSelection(0, -1);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> moveSelection(0, 1);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> moveSelection(-1, 0);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> moveSelection(1, 0);
                    case KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE, KeyEvent.VK_0, KeyEvent.VK_NUMPAD0 -> clearSelected();
                    default -> {
                        var c = e.getKeyChar();
                        if (c >= '1' && c <= '9') setSelectedValue(c - '0');
                    }
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(final FocusEvent e) { repaint(); }
            @Override public void focusLost(final FocusEvent e) { repaint(); }
        });
    }

    public Position getSelected(){
        return new Position(selectedCol, selectedRow);
    }

    public void select(final int col, final int row){
        selectedCol = col;
        selectedRow = row;
        repaint();
    }

    private void moveSelection(final int dCol, final int dRow){
        select(Math.floorMod(selectedCol + dCol, BOARD_LIMIT), Math.floorMod(selectedRow + dRow, BOARD_LIMIT));
    }

    public void setSelectedValue(final int value){
        if (locked) return;
        if (boardService.setValue(selectedCol, selectedRow, value)) {
            notifierService.notify(SPACE_CHANGED);
        }
    }

    public void clearSelected(){
        if (locked) return;
        if (boardService.clearValue(selectedCol, selectedRow)) {
            notifierService.notify(SPACE_CHANGED);
        }
    }

    public void setShowErrors(final boolean showErrors) {
        this.showErrors = showErrors;
        repaint();
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
        repaint();
    }

    @Override
    public void update(final EventEnum eventType) {
        repaint();
    }

    private int originX(){
        return (getWidth() - SIZE) / 2;
    }

    private int originY(){
        return (getHeight() - SIZE) / 2;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        var g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        var ox = originX();
        var oy = originY();
        var selectedValue = boardService.getSpace(selectedCol, selectedRow).getActual();

        for (int col = 0; col < BOARD_LIMIT; col++) {
            for (int row = 0; row < BOARD_LIMIT; row++) {
                paintCell(g2, col, row, ox + col * CELL, oy + row * CELL, selectedValue);
            }
        }
        paintGrid(g2, ox, oy);
        g2.dispose();
    }

    private void paintCell(final Graphics2D g2, final int col, final int row,
                           final int x, final int y, final Integer selectedValue){
        var space = boardService.getSpace(col, row);
        var value = space.getActual();
        var conflict = boardService.hasConflict(col, row);
        var wrong = showErrors && !space.isFixed() && boardService.isWrong(col, row);
        var hinted = boardService.isHinted(col, row);

        var selected = !locked && col == selectedCol && row == selectedRow;
        var peer = !locked && (col == selectedCol || row == selectedRow
                || (col / SECTOR_SIZE == selectedCol / SECTOR_SIZE && row / SECTOR_SIZE == selectedRow / SECTOR_SIZE));
        var sameNumber = !locked && value != null && Objects.equals(value, selectedValue);

        Color background = space.isFixed() ? Theme.FIXED_BG : Theme.SURFACE;
        if (peer) background = Theme.PEER_BG;
        if (sameNumber) background = Theme.SAME_NUMBER_BG;
        if (conflict || wrong) background = Theme.ERROR_BG;
        if (selected) background = hasFocus() ? Theme.SELECTED_BG : Theme.SAME_NUMBER_BG;
        g2.setColor(background);
        g2.fillRect(x, y, CELL, CELL);

        if (value == null) return;

        Color foreground;
        if (space.isFixed()) foreground = Theme.FIXED_TEXT;
        else if (conflict || wrong) foreground = Theme.ERROR_TEXT;
        else if (hinted) foreground = Theme.HINT_TEXT;
        else foreground = Theme.USER_TEXT;

        g2.setFont(space.isFixed() ? Theme.CELL_FIXED : Theme.CELL_USER);
        g2.setColor(foreground);
        var fm = g2.getFontMetrics();
        var text = value.toString();
        var tx = x + (CELL - fm.stringWidth(text)) / 2;
        var ty = y + (CELL - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);
    }

    private void paintGrid(final Graphics2D g2, final int ox, final int oy){
        g2.setColor(Theme.GRID_THIN);
        g2.setStroke(new BasicStroke(1));
        for (int i = 1; i < BOARD_LIMIT; i++) {
            if (i % SECTOR_SIZE == 0) continue;
            g2.drawLine(ox + i * CELL, oy, ox + i * CELL, oy + SIZE);
            g2.drawLine(ox, oy + i * CELL, ox + SIZE, oy + i * CELL);
        }

        g2.setColor(Theme.GRID_THICK);
        g2.setStroke(new BasicStroke(THICK, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        for (int i = SECTOR_SIZE; i < BOARD_LIMIT; i += SECTOR_SIZE) {
            g2.drawLine(ox + i * CELL, oy, ox + i * CELL, oy + SIZE);
            g2.drawLine(ox, oy + i * CELL, ox + SIZE, oy + i * CELL);
        }
        g2.drawRect(ox, oy, SIZE, SIZE);
    }
}
