package xyz.amymialee.mialib.interfaces;

public interface MItemEntity {
    default int mialib$getMergeDelay() {
        return 0;
    }

    default void mialib$setMergeDelay(int mergeDelay) {}
}