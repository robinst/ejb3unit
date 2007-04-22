package com.bm.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rpresentiert nested properties wie: rules.1.gefallenInProz=0.2345.
 * rules.1.gestiegenInProz=0.1345.
 * 
 * @author Daniel Wiese
 * @since 06.07.2006
 */
public abstract class NestedProperty {

    private final List<String> innerValues = new ArrayList<String>();

    private final Map<String, String> keyValuePairs = new HashMap<String, String>();

    /**
     * Constructor.
     * 
     * @param innerValues -
     *            the names of the inner values like: gefallenInProz,
     *            gestiegenInProz.
     */
    public NestedProperty(String... innerValues) {
        for (String current : innerValues) {
            this.innerValues.add(current);
        }
    }

    /**
     * Returns a list of inner values.
     * 
     * @author Daniel Wiese
     * @since 06.07.2006
     * @return list of inner values
     */
    public List<String> innerValues() {
        return innerValues;
    }

    /**
     * Sets an inner value.
     * 
     * @author Daniel Wiese
     * @since 06.07.2006
     * @param key -
     *            the key
     * @param value -
     *            the value
     */
    public void setValue(String key, String value) {
        this.keyValuePairs.put(key, value);
    }
    
    /**
     * Liest einen Wert als Boolean.
     * 
     * @author Daniel Wiese
     * @since 04.12.2005
     * @param key -
     *            der key um im Property-File einen Wert
     *            auszulesen
     * @return - den gelesen wert aus der Konfigurationsdatei.
     */
    public boolean getBoolean(String key) {
        return ((this.keyValuePairs.get(key).equalsIgnoreCase("true"))? true : false);
    }
    
    /**
     * Liest einen Wert als Double.
     * 
     * @author Daniel Wiese
     * @since 04.12.2005
     * @param key -
     *            der key um im Property-File einen Wert
     *            auszulesen
     * @return - den gelesen wert aus der Konfigurationsdatei.
     */
    public double getDouble(String key) {
        return Double.parseDouble(this.keyValuePairs.get(key));
    }
    
    /**
     * Liest einen Wert als Integer.
     * 
     * @author Daniel Wiese
     * @since 04.12.2005
     * @param key -
     *            der key um im Property-File einen Wert
     *            auszulesen
     * @return - den gelesen wert aus der Konfigurationsdatei.
     */
    public int getInteger(String key) {
        return Integer.parseInt(this.keyValuePairs.get(key));
    }
    
    /**
     * Liest einen Wert als Integer.
     * 
     * @author Daniel Wiese
     * @since 04.12.2005
     * @param key -
     *            der key um im Property-File einen Wert
     *            auszulesen
     * @return - den gelesen wert aus der Konfigurationsdatei.
     */
    public String getString(String key) {
        return this.keyValuePairs.get(key);
    }

}
