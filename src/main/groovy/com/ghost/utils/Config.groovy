package com.ghost.utils

//Static methods that wrap around the instance of the com.ghost.GhostPlay properties
//If something goes wrong it will return the default values
class Config
{
    static String getPathToDriver()
    {
        GhostProperties.getInstance().get('driver') ?: '/usr/bin/chromedriver'
    }

    static Integer getBrowserTimeout()
    {
        getInteger('browserTimeout') ?: 120
    }

    static Double getProxyResponseTime()
    {
        getDouble('proxyResponseTime') ?: 300d
    }

    static Double getPlayThroughTime()
    {
        getDouble('playThrough') ?: 35d
    }

    static Integer getDefaultRuns()
    {
        getInteger('defaultRuns') ?: 10
    }

    static String getString(String key)
    {
        GhostProperties.getInstance().get(key)
    }

    static int getInteger(String key)
    {
        Integer value
        try {
            value = GhostProperties.getInstance().get(key) as Integer
        }
        catch(IllegalArgumentException e) { value = null }
        return value
    }

    static boolean getBoolean(String key)
    {
        Boolean value
        try {
            value = GhostProperties.getInstance().get(key) as Boolean
        }
        catch(IllegalArgumentException e) { value = null }
        return value
    }

    static double getDouble(String key)
    {
        Double value
        try {
            value = GhostProperties.getInstance().get(key) as Double
        }
        catch(IllegalArgumentException e) { value = null }
        return value
    }
}
