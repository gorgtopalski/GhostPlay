package com.ghost.utils

import spock.lang.Specification

class ConfigSpec extends Specification
{
    def "Retrieve the default values"()
    {
        expect:
        Config.getBrowserTimeout() == 120
        Config.getPlayThroughTime() == 35d
        Config.getPathToDriver() == '/usr/bin/chromedriver'
        Config.getProxyResponseTime() == 300
        Config.getDefaultRuns() == 10
    }

    def "Retrieve string values"()
    {
        expect:
        Config.getString(key) == value

        where:
        key                 ||value
        'playThrough'       ||'35'
        'browserTimeout'    ||'120'
        'driver'            ||'/usr/bin/chromedriver'
        'proxyResponseTime' ||'300'
        'defaultRuns'       ||'10'
    }

}
