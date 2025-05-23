package utils;

public interface TestLogger {
    default void log(String message, String... args) {
        System.out.println(String.format(message, args));
    }

    default void logError(String message, String... args) {
        System.err.println(String.format(message, args));
    }
}
