package com.ghost.runner

import com.ghost.proxy.GhostProxy
import com.ghost.proxy.ProxyParser
import com.ghost.target.TargetAgregator
import com.ghost.utils.Config
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DynamicDispatchActor
import org.openqa.selenium.remote.UnreachableBrowserException
import org.slf4j.LoggerFactory

class BrowserPool extends DynamicDispatchActor
{
    private static log = LoggerFactory.getLogger(BrowserPool.class)
    private Actor targets
    private Actor proxies

    BrowserPool()
    {
        System.setProperty("webdriver.chrome.verboseLogging", "false");
        System.setProperty("webdriver.chrome.driver", Config.getPathToDriver())
        proxies = new ProxyParser(this).start()
        targets = new TargetAgregator(this).start()
    }

    //Start a new runner when a proxy is received
    void onMessage(GhostProxy proxy)
    {
        try {
            new ChromeRunnerActor(proxy,targets).start()
        }
        catch(UnreachableBrowserException e) { log.error("${proxy.addr} failed to start") }
    }

    void onMessage(String str)
    {
        if (str == 'finished')
        {
            log.info("It's over!")
            proxies.terminate().join()
            this.terminate().join()
        }
    }
}