package com.ghost.proxy

import groovy.transform.Canonical
import groovy.transform.ToString
import org.openqa.selenium.Proxy

@Canonical
@ToString(includeNames = true, includePackage = false)
class GhostProxy implements Comparable<GhostProxy>
{
    String addr
    int port
    double avg = 0
    double min = 0
    double max = 0

    @Override
    int compareTo(GhostProxy o) {
        (addr == o.addr) ? 0 : (avg > o.avg) ? 1 : -1
    }

    boolean isUsable()
    {
        (avg > 0) && (min > 0) && (max > 0)
    }

    Proxy toSeleniumProxy()
    {
        String p = "$addr:$port"
        new Proxy(httpProxy: p)
    }
}
