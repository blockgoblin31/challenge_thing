package com.blockgoblin31.challengemodthing.util;

public interface OutputHandlingFunctionPasser<T, O> extends FunctionPasser<T> {

    O getOutput();

}
