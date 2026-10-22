package com.sinogale.dwsdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registers DW SDK scenes and exposes a single entry point for scene execution.
 */
public class DwSdkSceneWrapper {

    private final Map<String, DwSdkScene<?, ?>> scenes;

    public DwSdkSceneWrapper(Collection<DwSdkScene<?, ?>> scenes) {
        Map<String, DwSdkScene<?, ?>> sceneMap = new LinkedHashMap<>();
        if (scenes != null) {
            for (DwSdkScene<?, ?> scene : scenes) {
                if (scene == null) {
                    continue;
                }
                String sceneName = normalizeSceneName(scene.getSceneName());
                DwSdkScene<?, ?> previous = sceneMap.put(sceneName, scene);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate DW SDK scene name: " + sceneName);
                }
            }
        }
        this.scenes = Collections.unmodifiableMap(sceneMap);
    }

    public boolean supports(String sceneName) {
        return scenes.containsKey(normalizeSceneName(sceneName));
    }

    public Set<String> getSceneNames() {
        return scenes.keySet();
    }

    public List<DwSdkScene<?, ?>> getScenes() {
        return Collections.unmodifiableList(new ArrayList<>(scenes.values()));
    }

    public int size() {
        return scenes.size();
    }

    /**
     * Executes a scene registered under the supplied name.
     *
     * @throws IllegalArgumentException when the scene name is unknown
     * @throws ClassCastException when the request type does not match the scene
     */
    @SuppressWarnings("unchecked")
    public <T, R> R execute(String sceneName, T request) {
        String normalizedName = normalizeSceneName(sceneName);
        DwSdkScene<?, ?> scene = scenes.get(normalizedName);
        if (scene == null) {
            throw new IllegalArgumentException("Unknown DW SDK scene: " + normalizedName
                    + "; available scenes: " + scenes.keySet());
        }
        return ((DwSdkScene<T, R>) scene).execute(request);
    }

    private static String normalizeSceneName(String sceneName) {
        if (sceneName == null || sceneName.trim().isEmpty()) {
            throw new IllegalArgumentException("DW SDK scene name must not be blank");
        }
        return sceneName.trim();
    }
}
