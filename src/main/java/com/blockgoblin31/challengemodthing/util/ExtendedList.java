package com.blockgoblin31.challengemodthing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ExtendedList<E> extends ArrayList<E> implements Cloneable {
    ArrayList<E> toRemove = new ArrayList<>();

    public ExtendedList() {
        super();
    }

    public ExtendedList(E e) {
        super();
        add(e);
    }

    public ExtendedList<E> add(E[] input) {
        this.addAll(Arrays.asList(input));
        return this;
    }
    public ExtendedList<E> add(ArrayList<E> input) {
        this.addAll(input);
        return this;
    }
    public ExtendedList<E> sortBy(ExtendedList<E> key) {
        ExtendedList<E> output = new ExtendedList<>();
        IterationHelper.ForLoop<E> elemLoop = IterationHelper.forLoop(new FunctionPasser<E>() {
            @Override
            public E get(E input) {
                int[] i = {0};
                IterationHelper.WhileLoop<E> loop = IterationHelper.whileLoop(() -> i[0] < getCount(input), new FunctionPasser<E>() {
                    @Override
                    public E get(E input) {
                        return input;
                    }

                    @Override
                    public ArrayList<E> getFinal(ArrayList<E> input) {
                        return input;
                    }

                    @Override
                    public void process() {
                        output.add(input);
                        i[0] = i[0] + 1;
                    }
                });
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {

            }
        });
        elemLoop.loop(key);
        this.set(output);
        return output;
    }
    public int getCount(E o) {
        OutputHandlingFunctionPasser<E, Integer> function = new OutputHandlingFunctionPasser<>() {
            int count = 0;

            @Override
            public Integer getOutput() {
                return count;
            }

            @Override
            public E get(E input) {
                if (input.equals(o)) count++;
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {

            }
        };
        IterationHelper.ForLoop<E> loop = IterationHelper.forLoop(function);
        loop.loop(this);
        return function.getOutput();
    }
    public void set(ExtendedList<E> to) {
        this.clear();
        this.add(to);
    }
    public void set(E[] to) {
        this.clear();
        this.add(to);
    }
    public int getTotal() {
        OutputHandlingFunctionPasser<E, ExtendedList<E>> function = new OutputHandlingFunctionPasser<E, ExtendedList<E>>() {
            final ExtendedList<E> count = new ExtendedList<>();

            @Override
            public ExtendedList<E> getOutput() {
                return count;
            }

            @Override
            public E get(E input) {
                if (!count.contains(input)) count.add(input);
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {

            }
        };
        IterationHelper.ForLoop<E> loop = IterationHelper.forLoop(function);
        loop.loop(this);
        return function.getOutput().size();
    }
    public ExtendedList<E> remove() {
        this.remove(0);
        return this;
    }
    public void scheduleRemove(E e) {
        this.toRemove.add(e);
    }
    public void scheduleRemove(int i) {
        this.toRemove.add(get(i));
    }
    public void applyRemove() {
        IterationHelper.ForLoop<E> removeLoop = IterationHelper.forLoop(new FunctionPasser<E>() {
            @Override
            public E get(E input) {
                remove(input);
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {

            }
        });
        removeLoop.loop(toRemove);
        toRemove.clear();
    }

    public Integer[] allIndecesOf(E e) {
        ExtendedList<E> elements = clone();
        OutputHandlingFunctionPasser<E, ExtendedList<Integer>> function = new OutputHandlingFunctionPasser<E, ExtendedList<Integer>>() {
            final ExtendedList<Integer> indices = new ExtendedList<>();

            @Override
            public ExtendedList<Integer> getOutput() {
                return indices;
            }

            @Override
            public E get(E input) {
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {
                indices.add(elements.indexOf(e));
                elements.remove(e);
            }
        };
        IterationHelper.WhileLoop<E> loop = new IterationHelper.WhileLoop<>(function, () -> elements.contains(e));
        loop.loopThrough();
        return function.getOutput().toArray(new Integer[0]);
    }
    public ExtendedList<E> remove(ExtendedList<E> toCheck) {
        IterationHelper.ForLoop<E> loop = IterationHelper.forLoop(new FunctionPasser<E>() {
            @Override
            public E get(E input) {
                if (contains(input)) {
                    remove(input);
                    return null;
                }
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                input.removeIf(Objects::isNull);
                return input;
            }

            @Override
            public void process() {

            }
        });
        return (ExtendedList<E>) loop.loop(toCheck);
    }
    public ExtendedList<E> getSame(ExtendedList<E> other) {
        ExtendedList<E> clone = other.clone();
        IterationHelper.ForLoop<E> loop = new IterationHelper.ForLoop<>(new FunctionPasser<E>() {
            final ExtendedList<E> out = new ExtendedList<>();

            @Override
            public E get(E input) {
                if (clone.contains(input)) {
                    out.add(input);
                    clone.remove(input);
                }
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return out;
            }

            @Override
            public void process() {

            }
        });
        return (ExtendedList<E>) loop.loop(this);
    }
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ExtendedList<?> other)) return false;
        if (this.size() != other.size()) return false;
        //for (int i = 0; i < this.size(); i++) {
        //    if (this.get(i) != other.get(i)) return false;
        //}
        int[] i = {0};
        boolean[] out = {true};
        IterationHelper.WhileLoop<E> whileLoop = new IterationHelper.WhileLoop<>(new FunctionPasser<E>() {
            @Override
            public E get(E input) {
                return null;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return null;
            }

            @Override
            public void process() {
                if (!ExtendedList.this.get(i[0]).equals(other.get(i[0]))) out[0] = false;
            }
        }, () -> out[0] && i[0] < size());
        whileLoop.loopThrough();
        return out[0];
    }
    public void replace(E original, E newValue) {
        IterationHelper.WhileLoop<E> loop = IterationHelper.whileLoop(() -> this.contains(original), new FunctionPasser<E>() {
            @Override
            public E get(E input) {
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {
                set(indexOf(original), newValue);
            }
        });
        loop.loopThrough();
    }
    public boolean contains(E[] e) {
        int consecutive = 0;
        int index;
        //for (E e1 : e) {
        //    if (!contains(e1)) return false;
        //}
        OutputHandlingFunctionPasser<E, Boolean> func = new OutputHandlingFunctionPasser<E, Boolean>() {
            boolean output = true;
            @Override
            public Boolean getOutput() {
                return output;
            }

            @Override
            public E get(E input) {
                if (!contains(input)) output = false;
                return input;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return input;
            }

            @Override
            public void process() {

            }
        };
        IterationHelper.ForLoop<E> forLoop = IterationHelper.forLoop(func);
        forLoop.loop(new ExtendedList<E>().add(e));
        if (!func.getOutput()) return false;
        final boolean[] stopProcessing = {false};
        func = new OutputHandlingFunctionPasser<E, Boolean>() {
            boolean returnVal = false;
            int curIndex = indexOf(e[0]);
            int curBaseIndex = 0;
            int indexIntoE = 0;
            @Override
            public Boolean getOutput() {
                return returnVal;
            }

            @Override
            public E get(E input) {
                return null;
            }

            @Override
            public ArrayList<E> getFinal(ArrayList<E> input) {
                return null;
            }

            @Override
            public void process() {
                if (indexIntoE >= e.length) {
                    returnVal = true;
                    stopProcessing[0] = true;
                    return;
                }
                if (curIndex >= size()) {
                    stopProcessing[0] = true;
                    return;
                }
                if (!e[indexIntoE].equals(ExtendedList.this.get(curIndex))) {
                    curIndex++;
                    indexIntoE++;
                } else {
                    curBaseIndex++;
                    curIndex = allIndecesOf(e[0])[curBaseIndex];
                    indexIntoE = 0;
                }
            }
        };
        while (consecutive < e.length) {
            index = indexOf(e[0]);
            for (E e1 : e) {
                if (index >= size()) {
                    return false;
                }
                if (!get(index).equals(e1)) break;
                index++;
                consecutive++;
            }
            consecutive = 0;
        }
        return true;
    }

    public String toUnformattedString() {
        StringBuilder output = new StringBuilder();
        for (E e : this) {
            output.append(e.toString());
        }
        return output.toString();
    }

    @Override
    public ExtendedList<E> clone() {
        if (this.size() == 0) return new ExtendedList<>();
        ArrayList<?> base = (ArrayList<?>) super.clone();
        ExtendedList<E> output = new ExtendedList<>();
        base.forEach((Object o) -> output.add((E) o));
        return output;
    }

    public ExtendedList<E> removeDuplicates() {
        ExtendedList<E> finished = new ExtendedList<>();
        for (E e : this) {
            if (finished.contains(e)) continue;
            for (int i = 1; i < getCount(e); i++) scheduleRemove(e);
            finished.add(e);
        }
        applyRemove();
        return this;
    }
}