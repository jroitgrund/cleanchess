package me.roitgrund.chess.game;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TestCoord {

    @Test
    public void testIsValid() {
        assertThat(new Coord(0, 0).isValid()).isTrue();
        assertThat(new Coord(-1, 0).isValid()).isFalse();
    }
}
