package br.com.dio.service;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.Space;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.newSetFromMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class BoardService {

    public final static int BOARD_LIMIT = 9;
    public final static int SECTOR_SIZE = 3;

    private final Board board;
    private final Deque<Move> history = new ArrayDeque<>();
    private final Set<Space> hinted = newSetFromMap(new IdentityHashMap<>());

    public record Position(int col, int row) {}

    private record Move(int col, int row, Integer previous, boolean wasHinted) {}

    public BoardService(final Map<String, String> gameConfig) {
        this.board = new Board(initBoard(gameConfig));
    }

    public List<List<Space>> getSpaces(){
        return board.getSpaces();
    }

    public Space getSpace(final int col, final int row){
        return board.getSpaces().get(col).get(row);
    }

    /**
     * Altera o valor de uma posição registrando o movimento para permitir desfazer.
     * @return true se o valor foi alterado
     */
    public boolean setValue(final int col, final int row, final Integer value){
        var space = getSpace(col, row);
        if (space.isFixed() || Objects.equals(space.getActual(), value)){
            return false;
        }
        history.push(new Move(col, row, space.getActual(), hinted.contains(space)));
        hinted.remove(space);
        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        return setValue(col, row, null);
    }

    public boolean canUndo(){
        return !history.isEmpty();
    }

    public Optional<Position> undo(){
        if (history.isEmpty()){
            return Optional.empty();
        }
        var move = history.pop();
        var space = getSpace(move.col(), move.row());
        space.setActual(move.previous());
        if (move.wasHinted()){
            hinted.add(space);
        } else {
            hinted.remove(space);
        }
        return Optional.of(new Position(move.col(), move.row()));
    }

    /**
     * Preenche com o valor correto a posição preferida (se ela precisar) ou a primeira posição vazia/errada.
     */
    public Optional<Position> hint(final Position preferred){
        Position target = null;
        if (nonNull(preferred) && needsHint(preferred.col(), preferred.row())){
            target = preferred;
        } else {
            for (int row = 0; row < BOARD_LIMIT && isNull(target); row++) {
                for (int col = 0; col < BOARD_LIMIT && isNull(target); col++) {
                    if (needsHint(col, row)) target = new Position(col, row);
                }
            }
        }
        if (isNull(target)){
            return Optional.empty();
        }
        var space = getSpace(target.col(), target.row());
        setValue(target.col(), target.row(), space.getExpected());
        hinted.add(space);
        return Optional.of(target);
    }

    private boolean needsHint(final int col, final int row){
        var space = getSpace(col, row);
        return !space.isFixed() && !Objects.equals(space.getActual(), space.getExpected());
    }

    public boolean isHinted(final int col, final int row){
        return hinted.contains(getSpace(col, row));
    }

    public int hintsUsed(){
        return hinted.size();
    }

    /** Valor preenchido diferente do valor esperado da solução. */
    public boolean isWrong(final int col, final int row){
        var space = getSpace(col, row);
        return nonNull(space.getActual()) && space.getActual() != space.getExpected();
    }

    public long wrongCount(){
        var count = 0;
        for (int col = 0; col < BOARD_LIMIT; col++) {
            for (int row = 0; row < BOARD_LIMIT; row++) {
                if (isWrong(col, row)) count++;
            }
        }
        return count;
    }

    /** Valor repetido na mesma linha, coluna ou quadrante (regra do sudoku, sem revelar a solução). */
    public boolean hasConflict(final int col, final int row){
        var value = getSpace(col, row).getActual();
        if (isNull(value)) return false;

        for (int i = 0; i < BOARD_LIMIT; i++) {
            if (i != row && value.equals(getSpace(col, i).getActual())) return true;
            if (i != col && value.equals(getSpace(i, row).getActual())) return true;
        }
        var initCol = (col / SECTOR_SIZE) * SECTOR_SIZE;
        var initRow = (row / SECTOR_SIZE) * SECTOR_SIZE;
        for (int c = initCol; c < initCol + SECTOR_SIZE; c++) {
            for (int r = initRow; r < initRow + SECTOR_SIZE; r++) {
                if ((c != col || r != row) && value.equals(getSpace(c, r).getActual())) return true;
            }
        }
        return false;
    }

    public long countOf(final int value){
        return allSpaces().filter(s -> Objects.equals(s.getActual(), value)).count();
    }

    public long filledCount(){
        return allSpaces().filter(s -> nonNull(s.getActual())).count();
    }

    public void reset(){
        board.reset();
        history.clear();
        hinted.clear();
    }

    public boolean hasErrors(){
        return board.hasErrors();
    }

    public GameStatusEnum getStatus(){
        return board.getStatus();
    }

    public boolean gameIsFinished(){
        return board.gameIsFinished();
    }

    private java.util.stream.Stream<Space> allSpaces(){
        return board.getSpaces().stream().flatMap(Collection::stream);
    }

    private List<List<Space>> initBoard(final Map<String, String> gameConfig) {
        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = gameConfig.get("%s,%s".formatted(i, j));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(i).add(currentSpace);
            }
        }

        return spaces;
    }
}
