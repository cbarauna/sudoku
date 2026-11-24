package br.com.dio.ui.custom.screen;

import javax.swing.Timer;
import java.util.function.Consumer;

/** Cronômetro da partida, atualizado a cada segundo na thread da interface. */
public class GameTimer {

    private final Timer timer;
    private final Consumer<String> onTick;
    private long seconds = 0;

    public GameTimer(final Consumer<String> onTick) {
        this.onTick = onTick;
        this.timer = new Timer(1000, e -> {
            seconds++;
            onTick.accept(format());
        });
        onTick.accept(format());
    }

    public void start() { timer.start(); }

    public void stop() { timer.stop(); }

    public void restart() {
        seconds = 0;
        onTick.accept(format());
        timer.restart();
    }

    public String format() {
        var h = seconds / 3600;
        var m = (seconds % 3600) / 60;
        var s = seconds % 60;
        return h > 0 ? "%d:%02d:%02d".formatted(h, m, s) : "%02d:%02d".formatted(m, s);
    }
}
