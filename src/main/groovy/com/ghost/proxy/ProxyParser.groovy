package com.ghost.proxy

import com.ghost.utils.Config
import com.ghost.utils.FileReader
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import org.slf4j.LoggerFactory

class ProxyParser extends DefaultActor
{
    private Actor agregator
    private double timeFilter
    private static log = LoggerFactory.getLogger(ProxyParser.class)

    ProxyParser(Actor agregator)
    {
        this.agregator = agregator
        timeFilter = Config.getProxyResponseTime()
    }

    void afterStart()
    {
        new FileReader(this,'conf','proxy.list').start()
    }

    void act()
    {
        loop {
            react { String msg ->
                log.info("Pinging proxy $msg")
                def addr = msg.substring(0,msg.lastIndexOf(':'))
                def port = msg.substring(msg.lastIndexOf(':')+1,msg.size()) as Integer
                def proxy = StaticPing.ping(new GhostProxy(addr: addr, port:port))
                if ( proxy.avg < timeFilter)
                    agregator << proxy
                else
                    log.warn("Droping proxy $msg for low response time")
            }
        }
    }
}
