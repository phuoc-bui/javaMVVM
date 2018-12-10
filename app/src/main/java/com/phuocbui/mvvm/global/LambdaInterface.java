package com.phuocbui.mvvm.global;

import io.reactivex.annotations.NonNull;

public interface LambdaInterface {
    interface Function<R> {
        @NonNull
        R apply();
    }

    /**
     * A functional interface (callback) that computes a value based on multiple input values.
     * @param <T1> the first value type
     * @param <T2> the second value type
     * @param <R> the result type
     */
    interface BiFunction<T1, T2, R> {

        /**
         * Calculate a value based on the input values.
         * @param t1 the first value
         * @param t2 the second value
         * @return the result value
         */
        @NonNull
        R apply(@NonNull T1 t1, @NonNull T2 t2);
    }
}
