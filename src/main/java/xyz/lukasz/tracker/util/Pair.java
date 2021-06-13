package xyz.lukasz.tracker.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<L, R> {
    private L left;
    private R right;

    public static <A, B> Pair<A, B> of(A left, B right) {
        return new Pair<>(left, right);
    }
}
