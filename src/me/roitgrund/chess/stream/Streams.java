package me.roitgrund.chess.stream;

import java.util.stream.IntStream;

public class Streams {

    public static IntStream reverseIntStream(int startInclusive,
                                             int endInclusive) {
        return IntStream.rangeClosed(endInclusive, startInclusive)
                .map(i -> endInclusive - i + startInclusive);
    }
}
