package com.blockgoblin31.challengemodthing.util;

import java.util.ArrayList;
import java.util.function.BiPredicate;

public class ConditionChecker {
    ExtendedList<BiPredicate<?, ?>> predicates = new ExtendedList<>();
    int index;
    public ConditionChecker(BiPredicate<?, ?>... predicate) {
        predicates.add(predicate);
    }

    public Boolean getNext(Object a, Object b) {
        BiPredicate<Object, Object> predicate = (BiPredicate<Object, Object>) predicates.get(index++);
        try {
            return predicate.test(a, b);
        } finally {
            return null;
        }
    }
}
