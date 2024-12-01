package xyz.amymialee.mialib.util.interfaces;

public @SuppressWarnings("unused") interface MItemEntity {
    default int mialib$getMergeDelay() {
        return 0;
    }

    default void mialib$setMergeDelay(int mergeDelay) {}
}