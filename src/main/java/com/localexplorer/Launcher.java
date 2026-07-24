package com.localexplorer;

/**
 * Separate entry point that does NOT extend javafx.application.Application.
 *
 * Why this file exists: when the class containing main() is itself an
 * Application subclass and gets launched directly off the classpath
 * (instead of the module path), the JVM throws:
 *   "Error: JavaFX runtime components are missing, and are required to run this application"
 * even though all the JavaFX jars are present as dependencies.
 *
 * Routing through this plain Launcher class avoids that check entirely.
 * This is the standard fix for JavaFX + Maven (non-modular) projects.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
