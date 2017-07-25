package com.ghost.proxy

import com.ghost.utils.Config
import com.ghost.utils.FileReader
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.DynamicDispatchActor
import org.slf4j.LoggerFactory

class ProxyParser extends DynamicDispatchActor
{
    private Actor agregator
    private Actor reader
    private double timeFilter
    private static log = LoggerFactory.getLogger(ProxyParser.class)

    ProxyParser(Actor agregator)
    {
        this.agregator = agregator
        timeFilter = Config.getProxyResponseTime()
        reader = new FileReader(this,'conf','proxy.list').start()
    }

    void onMessage(String msg)
    {
        log.info("Pinging proxy $msg")
        def addr = msg.substring(0,msg.lastIndexOf(':'))
        def port = msg.substring(msg.lastIndexOf(':')+1,msg.size()) as Integer
        def proxy = StaticPing.ping(new GhostProxy(addr: addr, port:port))
        if (proxy.avg < timeFilter)
            agregator << proxy
        else
            log.warn("Droping proxy $msg for low response time")
    }

    void onMessage(Exception ex)
    {
        reader.terminate()
        agregator.terminate()
        this.terminate()
    }
}
