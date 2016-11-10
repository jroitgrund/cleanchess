package me.roitgrund.chess.format;

import me.roitgrund.chess.game.Board;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TestPrettyPrinter {

    private BoardFormatter boardFormatter;

    @Before
    public void setUp() {
        boardFormatter = new BoardFormatter();
    }

    @Test
    public void testPrintNewBoard() {
        assertThat(boardFormatter.format(new Board())).isEqualTo(
                " a  b  c  d  e  f  g  h\n" +
                        "-------------------------\n" +
                        "|BR|BC|BB|BQ|BK|BB|BC|BR| 8\n" +
                        "|BP|BP|BP|BP|BP|BP|BP|BP| 7\n" +
                        "|  |  |  |  |  |  |  |  | 6\n" +
                        "|  |  |  |  |  |  |  |  | 5\n" +
                        "|  |  |  |  |  |  |  |  | 4\n" +
                        "|  |  |  |  |  |  |  |  | 3\n" +
                        "|WP|WP|WP|WP|WP|WP|WP|WP| 2\n" +
                        "|WR|WC|WB|WQ|WK|WB|WC|WR| 1\n" +
                        "-------------------------\n");
    }
}
