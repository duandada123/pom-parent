package com.sinogale.dwsdk;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DwSdkSceneWrapperTest {

    @Test
    public void shouldRegisterAndExecuteScene() {
        DwSdkScene<String, String> echoScene = new DwSdkScene<String, String>() {
            @Override
            public String getSceneName() {
                return "echo";
            }

            @Override
            public String execute(String request) {
                return "reply:" + request;
            }
        };

        DwSdkSceneWrapper wrapper = new DwSdkSceneWrapper(Collections.singletonList(echoScene));

        assertTrue(wrapper.supports("echo"));
        assertFalse(wrapper.supports("other"));
        assertEquals(Collections.singleton("echo"), wrapper.getSceneNames());
        assertEquals("reply:hello", wrapper.execute("echo", "hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnknownScene() {
        new DwSdkSceneWrapper(Collections.emptyList()).execute("missing", "request");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRejectDuplicateSceneNames() {
        DwSdkScene<String, String> first = scene("same");
        DwSdkScene<String, String> second = scene("same");

        new DwSdkSceneWrapper(Arrays.asList(first, second));
    }

    private static DwSdkScene<String, String> scene(final String name) {
        return new DwSdkScene<String, String>() {
            @Override
            public String getSceneName() {
                return name;
            }

            @Override
            public String execute(String request) {
                return request;
            }
        };
    }
}
