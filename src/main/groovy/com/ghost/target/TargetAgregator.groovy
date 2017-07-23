package com.ghost.target

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DynamicDispatchActor
import org.slf4j.LoggerFactory

class TargetAgregator extends DynamicDispatchActor
{
    def targets = new TreeMap<Target,Integer>()
    private Actor pool
    private Actor parser
    private static log = LoggerFactory.getLogger('Targets')


    TargetAgregator(Actor pool)
    {
        this.pool = pool
        this.parser = new TargetParser(this).start()
    }

    void onMessage(Map params)
    {
        targets.put(new Target(id : params.id,url: params.url), params.runs as Integer)
    }

    void onMessage(Target target)
    {
        def runs = targets.get(target)
        if (runs != null)
        {
            runs -= 1
            log.info("$target.url | $runs | ${sender.toString()}")
            targets.replace(target, runs)
        }
        if (runs == 0) targets.remove(target)

        if (targets.size() > 0)
        {
            target = targets.higherKey(target)
            if(target == null) target = targets.firstKey()
            reply target
        }
        else {
            pool.sendAndContinue('finished',
        {
                    parser.terminate().join()
                    this.terminate().join()
                }
            )
        }
    }

    void onMessage(String str)
    {
        reply targets.firstKey()
    }
}
