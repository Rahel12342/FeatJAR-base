package de.featjar.base.computation;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.task.CancelableMonitor;
import de.featjar.base.task.IMonitor;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utilities for creating and computing computations.
 * Java does not have explicit keywords (e.g., {@code async} and {@code await}) for asynchronous programming.
 * This class implements both keywords as regular functions, which can be used to easily switch between
 * (a-)synchronous computation modes.
 * Using {@link Computations#async(Object)}, an object can be turned into a (constant) computation
 * (i.e., switch to the asynchronous computation mode).
 * Other {@code async} helpers create computations from other objects.
 * Using {@link Computations#asyncMap(Class, String, Function)} and
 * {@link Computations#asyncFlatMap(Class, String, Function)},
 * functions can be lifted to computation level.
 * To extract the result of a computation (i.e., return to the synchronous computation mode),
 * use {@link Computations#await(IComputation)})} and other {@code await} helpers.
 *
 * @author Elias Kuiter
 */
public class Computations {
    /**
     * {@return a trivial computation that computes a given object}
     *
     * @param object the object
     * @param <T>    the type of the object
     */
    public static <T> IComputation<T> of(T object) {
        return of(object, new CancelableMonitor());
    }

    /**
     * {@return a trivial computation that computes a given object}
     *
     * @param object  the object
     * @param monitor the monitor
     * @param <T>     the type of the object
     */
    public static <T> IComputation<T> of(T object, IMonitor monitor) {
        return new ComputeConstant<>(object, monitor);
    }

    /**
     * {@return a computation that computes both given computations, summarizing their results in a pair}
     *
     * @param computation1 the first computation
     * @param computation2 the second computation
     */
    public static <T, U> IComputation<Pair<T, U>> of(IComputation<T> computation1, IComputation<U> computation2) {
        return new ComputePair<>(computation1, computation2);
    }

    /**
     * {@return a computation that computes all of the given computations, summarizing their results in a list}
     *
     * @param computations the computations
     */
    public static IComputation<List<?>> allOf(List<? extends IComputation<?>> computations) {
        return allOf(computations.toArray(IComputation[]::new));
    }

    /**
     * {@return a computation that computes all its computations, summarizing their results in a list}
     *
     * @param computations the computations
     */
    public static IComputation<List<?>> allOf(IComputation<?>... computations) {
        return new ComputeAllOf(computations);
    }

    /**
     * {@return a constant computation of the given object}
     *
     * @param t the object
     * @param <T> the type of the object
     */
    public static <T> IComputation<T> async(T t) {
        return of(t);
    }

    /**
     * {@return a constant computation of the given result}
     *
     * @param tResult the result
     * @param <T> the type of the result
     */
    public static <T> IComputation<T> async(Result<T> tResult) {
        return tResult.map(Computations::of).orElseThrow(); // todo: better error handling?
    }

    /**
     * {@return the given computation, unchanged}
     * Useful to allow transparently switching between (a-)synchronous computation modes.
     *
     * @param tComputation the computation
     * @param <T> the type of the computation result
     */
    public static <T> IComputation<T> async(IComputation<T> tComputation) {
        return tComputation;
    }

    /**
     * {@return a constant computation of two given objects}
     *
     * @param t the first object
     * @param u the second object
     * @param <T> the type of the first object
     * @param <U> the type of the second object
     */
    public static <T, U> IComputation<Pair<T, U>> async(T t, U u) {
        return of(async(t), async(u));
    }

    /**
     * {@return a computation of two given computations}
     *
     * @param tComputation the first computation
     * @param uComputation the second computation
     * @param <T> the type of the first computation result
     * @param <U> the type of the second computation result
     */
    public static <T, U> IComputation<Pair<T, U>> async(IComputation<T> tComputation, IComputation<U> uComputation) {
        return of(tComputation, uComputation);
    }

    /**
     * {@return a computation of any given number of objects}
     *
     * @param objects the objects
     */
    public static IComputation<List<?>> async(Object... objects) {
        return async(Arrays.stream(objects).map(Computations::async).toArray(IComputation[]::new));
    }

    /**
     * {@return a computation of any given number of computations}
     *
     * @param computations the computations
     */
    public static IComputation<List<?>> async(IComputation<?>... computations) {
        return allOf(computations);
    }

    /**
     * {@return an asynchronous function that operates on computations, lifting a given synchronous function}
     *
     * @param klass the calling class
     * @param scope the calling scope
     * @param fn the function
     * @param <T> the type of the mapped value
     * @param <U> the type of the mapped result
     */
    public static <T, U> Function<IComputation<T>, IComputation<U>> asyncMap(Class<?> klass, String scope, Function<T, U> fn) {
        return tComputation -> tComputation.mapResult(klass, scope, fn);
    }

    /**
     * {@return an asynchronous function that operates on computations, lifting a given synchronous function that returns a result}
     *
     * @param klass the calling class
     * @param scope the calling scope
     * @param fn the function
     * @param <T> the type of the mapped value
     * @param <U> the type of the mapped result
     */
    public static <T, U> Function<IComputation<T>, IComputation<U>> asyncFlatMap(Class<?> klass, String scope, Function<T, Result<U>> fn) {
        return tComputation -> tComputation.flatMapResult(klass, scope, fn);
    }

    /**
     * {@return the given object, unchanged}
     * Useful to allow transparently switching between (a-)synchronous computation modes.
     *
     * @param t the object
     * @param <T> the type of the object
     */
    public static <T> T await(T t) {
        return t;
    }

    /**
     * {@return the value of the given result}
     *
     * @param tResult the result
     * @param <T> the type of the result
     */
    public static <T> T await(Result<T> tResult) {
        return tResult.orElseThrow(); // todo: better error handling? logging?
    }

    /**
     * {@return the result of the given computation}
     *
     * @param tComputation the computation
     * @param <T> the type of the computation result
     */
    public static <T> T await(IComputation<T> tComputation) {
        return await(tComputation.getResult());
    }

    /**
     * {@return a synchronous function, un-lifting a given asynchronous function that operates on computations}
     *
     * @param fn the function
     * @param <T> the type of the mapped value
     * @param <U> the type of the mapped result
     */
    public static <T, U> Function<T, U> awaitMap(Function<IComputation<T>, IComputation<U>> fn) {
        return t -> await(fn.apply(of(t)));
    }

    /**
     * {@return the key of the given pair, unchanged}
     * Useful to allow transparently switching between (a-)synchronous computation modes.
     *
     * @param pair the pair
     * @param <T> the type of the key
     * @param <U> the type of the value
     */
    public static <T, U> T getKey(Pair<T, U> pair) {
        return pair.getKey();
    }

    /**
     * {@return a computation for the key of the given pair computation}
     *
     * @param pair the pair
     * @param <T> the type of the key
     * @param <U> the type of the value
     */
    public static <T, U> IComputation<T> getKey(IComputation<Pair<T, U>> pair) {
        return pair.mapResult(Computations.class, "getKey", Pair::getKey);
    }

    /**
     * {@return the value of the given pair, unchanged}
     * Useful to allow transparently switching between (a-)synchronous computation modes.
     *
     * @param pair the pair
     * @param <T> the type of the key
     * @param <U> the type of the value
     */
    public static <T, U> U getValue(Pair<T, U> pair) {
        return pair.getValue();
    }

    /**
     * {@return a computation for the value of the given pair computation}
     *
     * @param pair the pair
     * @param <T> the type of the key
     * @param <U> the type of the value
     */
    public static <T, U> IComputation<U> getValue(IComputation<Pair<T, U>> pair) {
        return pair.mapResult(Computations.class, "getValue", Pair::getValue);
    }

    /**
     * {@return the value returned by a given function applied to this computation}
     * Typically, this returns a new computation composed with this computation.
     *
     * @param fn the function
     */
    public static <T, U, V> V mapPair(IComputation<Pair<T, U>> computation, BiFunction<IComputation<T>, IComputation<U>, V> fn) {
        return fn.apply(getKey(computation), getValue(computation));
    }
}