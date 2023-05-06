package featuregates.interfaces;

@FunctionalInterface
public interface Function<TResult> {
    TResult execute();
}
