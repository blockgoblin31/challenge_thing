package com.blockgoblin31.challengemodthing.util;

import java.util.ArrayList;
import java.util.function.Supplier;

public interface IterationHelper<T> {
    public static <T> ForLoop<T> forLoop(FunctionPasser<T> function) {
        return new ForLoop<>(function);
    }

    public static <T> WhileLoop<T> whileLoop(Supplier<Boolean> condition, FunctionPasser<T> function) {
        return new WhileLoop<>(function, condition);
    }

    ArrayList<T> loop(ArrayList<T> input);
    T next(T input);
    void next();

    class WhileLoop<T> implements IterationHelper<T> {
        final FunctionPasser<T> func;
        final Supplier<Boolean> condition;
        boolean loopEnded = false;

        WhileLoop(FunctionPasser<T> func, Supplier<Boolean> condition) {
            this.func = func;
            this.condition = condition;
        }

        public void loopThrough() {
            if (loopEnded) return;
            loop();
            loopEnded = true;
        }

        @Override
        public ArrayList<T> loop(ArrayList<T> input) {
            loopThrough();
            return func.getFinal(input);
        }

        private void loop() {
            if (condition.get()) {
                func.process();
                loop();
            }
        }

        @Override
        public T next(T input) {
            throw new IllegalStateException("Cannot call for loop methods on while loop!  Try using .next() instead.");
        }

        @Override
        public void next() {
            if (loopEnded) return;
            if (condition.get()) loopEnded = true;
            func.process();
        }
    }

    class ForLoop<T> implements IterationHelper<T> {
        final FunctionPasser<T> func;

        public ForLoop(FunctionPasser<T> func) {
            this.func = func;
        }

        @Override
        public ArrayList<T> loop(ArrayList<T> input) {
            loop(0, input);
            return func.getFinal(input);
        }

        private void loop(int index, ArrayList<T> input) {
            input.set(index, func.get(input.get(index)));
            index++;
            if(index >= input.size()) return;
            loop(index, input);
        }

        @Override
        public T next(T input) {
            return func.get(input);
        }

        @Override
        public void next() {
            func.process();
        }
    }
}
