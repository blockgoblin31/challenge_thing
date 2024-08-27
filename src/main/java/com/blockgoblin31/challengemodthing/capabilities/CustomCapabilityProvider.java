package com.blockgoblin31.challengemodthing.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CustomCapabilityProvider<A, B> implements ICapabilityProvider {
    private final Function<Capability<A>, LazyOptional<A>> capA;
    private final Function<Capability<B>, LazyOptional<B>> capB;

    public CustomCapabilityProvider(Function<Capability<A>, LazyOptional<A>> capA, Function<Capability<B>, LazyOptional<B>> capB) {
        this.capA = capA;
        this.capB = capB;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        LazyOptional<A> a = capA.apply((Capability<A>) cap);
        if(a.isPresent()) return (LazyOptional<T>) a;
        LazyOptional<T> b =  (LazyOptional<T>) capB.apply((Capability<B>) cap);
        if(b.isPresent()) return b;
        return LazyOptional.empty();
    }
}
