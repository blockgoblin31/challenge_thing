package com.blockgoblin31.challengemodthing.util;

import java.util.ArrayList;

public interface FunctionPasser<T> {

        T get(T input);
        ArrayList<T> getFinal(ArrayList<T> input);
        void process();

}
