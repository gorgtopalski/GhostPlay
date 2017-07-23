package com.ghost.utils

import org.slf4j.LoggerFactory

class GhostProperties
{
    private static GhostProperties instance
    private static Properties properties

    private GhostProperties()
    {
        properties = new Properties()
        try {
            properties.load(new FileInputStream(new File('conf/ghost.properties')))
        }catch(FileNotFoundException e) { LoggerFactory.getLogger(this.class).error('Properties file not found')}
    }

    static GhostProperties getInstance()
    {
        instance = instance ?: new GhostProperties()
    }

    static get(String key)
    {
        properties.getProperty(key)
    }
}


