package com.blockgoblin31.challengemodthing.util;

import java.util.Objects;

public class BiStorage<F, S> {
    F f;
    S s;
    public BiStorage() {

    }
    public BiStorage(F f, S s) {
        this.f = f;
        this.s = s;
    }
    public F getF() {
        return f;
    }
    public S getS() {
        return s;
    }
    @Deprecated
    public Object get(int i) {
        return switch (i) {
            case 1 -> f;
            case 2 -> s;
            default -> null;
        };
    }
    public BiStorage<F, S> setF(F n) {
        this.f = n;
        return this;
    }
    public BiStorage<F, S> setS(S n) {
        this.s = n;
        return this;
    }
    public BiStorage<F, S> set(F f, S s) {
        this.f = f;
        this.s = s;
        return this;
    }
    public BiStorage<F, S> set(BiStorage<F, S> to) {
        this.f = to.f;
        this.s = to.s;
        return this;
    }
    public void use(java.util.function.BiConsumer<F, S> e) {
        e.accept(this.f, this.s);
    }
    public String toString() {
        return "("+(f == null ? "null" : f.toString())+";"+(s == null ? "null" : s.toString())+")";
    }
    @Override
    public boolean equals(Object other) {
        try {
            return other instanceof BiStorage<?, ?> && f == null ? ((BiStorage<?, ?>) other).getF() == null : ((BiStorage<?, ?>) other).getF().equals(f) && s == null ? ((BiStorage<?, ?>) other).getS() == null : ((BiStorage<?, ?>) other).getS().equals(s);
        } catch (NullPointerException npe) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(f, s);
    }
}