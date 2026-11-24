package br.com.dio.ui.custom.screen;

import br.com.dio.service.BoardService;
import br.com.dio.service.EventEnum;
import br.com.dio.service.NotifierService;
import br.com.dio.ui.custom.board.SudokuBoardPanel;
import br.com.dio.ui.custom.button.CheckGameStatusButton;
import br.com.dio.ui.custom.button.FinishGameButton;
import br.com.dio.ui.custom.button.FlatButton;
import br.com.dio.ui.custom.button.ResetButton;
import br.com.dio.ui.custom.frame.MainFrame;
import br.com.dio.ui.custom.panel.HeaderPanel;
import br.com.dio.ui.custom.panel.MainPanel;
import br.com.dio.ui.custom.panel.NumberPadPanel;
import br.com.dio.ui.custom.theme.Theme;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

import static br.com.dio.service.BoardService.BOARD_LIMIT;
import static br.com.dio.service.EventEnum.CLEAR_SPACE;
import static br.com.dio.service.EventEnum.SPACE_CHANGED;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainScreen {

    private static final int TOTAL_SPACES = BOARD_LIMIT * BOARD_LIMIT;

    private final BoardService boardService;
    private final NotifierService notifierService;

    private JFrame mainFrame;
    private SudokuBoardPanel boardPanel;
    private HeaderPanel header;
    private NumberPadPanel numberPad;
    private GameTimer gameTimer;

    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton eraseButton;
    private JButton undoButton;
    private JButton hintButton;
    private JCheckBox showErrorsCheck;

    private boolean finished = false;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen(){
        var mainPanel = new MainPanel();

        header = new HeaderPanel();
        gameTimer = new GameTimer(header::setTime);

        boardPanel = new SudokuBoardPanel(boardService, notifierService);
        notifierService.subscribe(CLEAR_SPACE, boardPanel);
        notifierService.subscribe(SPACE_CHANGED, boardPanel);
        notifierService.subscribe(CLEAR_SPACE, this::onBoardChanged);
        notifierService.subscribe(SPACE_CHANGED, this::onBoardChanged);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(buildSidePanel(), BorderLayout.EAST);
        mainPanel.add(buildFooter(), BorderLayout.SOUTH);
        registerShortcuts(mainPanel);

        mainFrame = new MainFrame(mainPanel);
        onBoardChanged(SPACE_CHANGED);
        selectFirstEditable();
        mainFrame.setVisible(true);
        // No macOS as bordas da janela só ficam corretas depois de exibida: recalcula o tamanho.
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        boardPanel.requestFocusInWindow();
        gameTimer.start();
    }

    private JPanel buildSidePanel(){
        var side = new JPanel();
        side.setOpaque(false);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        numberPad = new NumberPadPanel(digit -> {
            boardPanel.setSelectedValue(digit);
            boardPanel.requestFocusInWindow();
        });
        numberPad.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        numberPad.setMaximumSize(new Dimension(216, 216));

        var actions = new JPanel(new GridLayout(0, 2, 8, 8));
        actions.setOpaque(false);
        actions.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(216, 88));
        actions.setPreferredSize(new Dimension(216, 88));
        eraseButton = FlatButton.secondary("Apagar", e -> boardPanel.clearSelected());
        eraseButton.setToolTipText("Apaga a casa selecionada (Backspace)");
        undoButton = FlatButton.secondary("Desfazer", e -> undo());
        undoButton.setToolTipText("Desfaz a última jogada (Ctrl/Cmd + Z)");
        hintButton = FlatButton.secondary("Dica", e -> hint());
        hintButton.setToolTipText("Preenche a casa selecionada com o valor correto (H)");
        actions.add(eraseButton);
        actions.add(undoButton);
        actions.add(hintButton);

        showErrorsCheck = new JCheckBox("Destacar erros");
        showErrorsCheck.setOpaque(false);
        showErrorsCheck.setFocusable(false);
        showErrorsCheck.setFont(Theme.LABEL);
        showErrorsCheck.setForeground(Theme.TEXT);
        showErrorsCheck.setToolTipText("Marca em vermelho os números diferentes da solução");
        showErrorsCheck.addActionListener(e -> boardPanel.setShowErrors(showErrorsCheck.isSelected()));
        showErrorsCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        var help = new JLabel("<html><div style='width:200px;color:#6B7280'>"
                + "<b>Como jogar</b><br>"
                + "Clique numa casa ou use as setas.<br>"
                + "Digite 1–9 para preencher, Backspace para apagar.<br>"
                + "Números repetidos na linha, coluna ou quadrante ficam em vermelho."
                + "</div></html>");
        help.setFont(Theme.LABEL.deriveFont(12f));
        help.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        help.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));

        side.add(numberPad);
        side.add(Box.createVerticalStrut(14));
        side.add(actions);
        side.add(Box.createVerticalStrut(8));
        side.add(showErrorsCheck);
        side.add(Box.createVerticalStrut(10));
        side.add(help);
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JPanel buildFooter(){
        var footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setOpaque(false);
        footer.add(new ResetButton(e -> resetGame()));
        checkGameStatusButton = new CheckGameStatusButton(e -> checkGameStatus());
        footer.add(checkGameStatusButton);
        finishGameButton = new FinishGameButton(e -> finishGame());
        footer.add(finishGameButton);
        return footer;
    }

    private void registerShortcuts(final JComponent root){
        var inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = root.getActionMap();
        var shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcut), "undo");
        actionMap.put("undo", action(this::undo));
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "hint");
        actionMap.put("hint", action(this::hint));
    }

    private static AbstractAction action(final Runnable runnable){
        return new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                runnable.run();
            }
        };
    }

    private void selectFirstEditable(){
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                if (!boardService.getSpace(col, row).isFixed()) {
                    boardPanel.select(col, row);
                    return;
                }
            }
        }
    }

    private void onBoardChanged(final EventEnum eventType){
        var filled = boardService.filledCount();
        header.setProgress(filled, TOTAL_SPACES);
        numberPad.setPadEnabled(!finished, boardService::countOf);
        undoButton.setEnabled(!finished && boardService.canUndo());

        if (!finished && boardService.gameIsFinished()) {
            onGameFinished();
            return;
        }
        updateStatus();
    }

    private void updateStatus(){
        if (finished) {
            header.setStatus("Concluído! Parabéns", Theme.SUCCESS);
            return;
        }
        var conflicts = countConflicts();
        if (conflicts > 0) {
            header.setStatus("%d casa(s) em conflito".formatted(conflicts), Theme.ERROR_TEXT);
            return;
        }
        var message = switch (boardService.getStatus()) {
            case NON_STARTED -> "Jogo não iniciado — escolha uma casa e comece";
            case INCOMPLETE -> "Em andamento";
            case COMPLETE -> "Tabuleiro completo, mas há erros";
        };
        header.setStatus(message, Theme.MUTED_TEXT);
    }

    private long countConflicts(){
        var count = 0L;
        for (int col = 0; col < BOARD_LIMIT; col++) {
            for (int row = 0; row < BOARD_LIMIT; row++) {
                if (!boardService.getSpace(col, row).isFixed() && boardService.hasConflict(col, row)) count++;
            }
        }
        return count;
    }

    private void undo(){
        if (finished) return;
        boardService.undo().ifPresent(position -> {
            boardPanel.select(position.col(), position.row());
            notifierService.notify(SPACE_CHANGED);
        });
    }

    private void hint(){
        if (finished) return;
        boardService.hint(boardPanel.getSelected()).ifPresent(position -> {
            boardPanel.select(position.col(), position.row());
            notifierService.notify(SPACE_CHANGED);
        });
        boardPanel.requestFocusInWindow();
    }

    private void onGameFinished(){
        finished = true;
        gameTimer.stop();
        boardPanel.setLocked(true);
        setControlsEnabled(false);
        updateStatus();
        var hints = boardService.hintsUsed();
        var hintsText = hints == 0 ? "sem usar dicas" : "usando %d dica(s)".formatted(hints);
        showMessageDialog(mainFrame,
                "Parabéns, você concluiu o jogo em %s, %s!".formatted(gameTimer.format(), hintsText),
                "Jogo concluído", INFORMATION_MESSAGE);
    }

    private void setControlsEnabled(final boolean enabled){
        checkGameStatusButton.setEnabled(enabled);
        finishGameButton.setEnabled(enabled);
        eraseButton.setEnabled(enabled);
        hintButton.setEnabled(enabled);
        showErrorsCheck.setEnabled(enabled);
        undoButton.setEnabled(enabled && boardService.canUndo());
    }

    private void finishGame() {
        if (boardService.gameIsFinished()){
            onGameFinished();
            return;
        }
        var missing = TOTAL_SPACES - boardService.filledCount();
        String message;
        if (missing > 0) {
            message = "Ainda falta preencher %d casa(s).".formatted(missing);
        } else {
            message = "Seu jogo tem %d número(s) errado(s). Ative \"Destacar erros\" para encontrá-los."
                    .formatted(boardService.wrongCount());
        }
        showMessageDialog(mainFrame, message, "Ainda não terminou", WARNING_MESSAGE);
    }

    private void checkGameStatus() {
        var message = switch (boardService.getStatus()){
            case NON_STARTED -> "O jogo não foi iniciado";
            case INCOMPLETE -> "O jogo está incompleto (%d de %d casas)".formatted(boardService.filledCount(), TOTAL_SPACES);
            case COMPLETE -> "O jogo está completo";
        };
        var wrong = boardService.wrongCount();
        message += wrong > 0 ? " e contém %d erro(s).".formatted(wrong) : " e não contém erros.";
        showMessageDialog(mainFrame, message, "Status do jogo", INFORMATION_MESSAGE);
    }

    private void resetGame() {
        var dialogResult = showConfirmDialog(
                mainFrame,
                "Deseja realmente reiniciar o jogo? Todo o progresso será perdido.",
                "Reiniciar o jogo",
                YES_NO_OPTION,
                QUESTION_MESSAGE
        );
        if (dialogResult != YES_OPTION) return;

        boardService.reset();
        finished = false;
        boardPanel.setLocked(false);
        showErrorsCheck.setSelected(false);
        boardPanel.setShowErrors(false);
        setControlsEnabled(true);
        notifierService.notify(CLEAR_SPACE);
        selectFirstEditable();
        gameTimer.restart();
        boardPanel.requestFocusInWindow();
    }

}
