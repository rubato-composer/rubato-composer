package org.rubato.composer.denobrowser;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class Messages {
    
    private static final String BUNDLE_NAME = "org.rubato.composer.denobrowser.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() { /* pure static class */ }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
