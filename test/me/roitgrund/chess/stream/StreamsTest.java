package me.roitgrund.chess.stream;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static me.roitgrund.chess.stream.Streams.reverseIntStream;

public class StreamsTest {

    @Test
    public void testReverseIntStream() {
        assertThat(
                reverseIntStream(10, 0).boxed()
                        .collect(Collectors.toList()))
                .containsExactlyElementsIn(
                        Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
                .inOrder();
    }
}
