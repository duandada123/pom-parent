package com.sinogale.dwsdk;

/**
 * A named DW SDK business scene.
 *
 * @param <T> request type
 * @param <R> response type
 */
public interface DwSdkScene<T, R> {

    /**
     * Returns the unique scene name used by {@link DwSdkSceneWrapper}.
     */
    String getSceneName();

    /**
     * Executes this scene.
     */
    R execute(T request);
}
