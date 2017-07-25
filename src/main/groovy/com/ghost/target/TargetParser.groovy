package com.ghost.target

import com.ghost.utils.FileReader
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.DynamicDispatchActor

class TargetParser extends DynamicDispatchActor
{
    private Actor agregator
    private Actor reader
    private int id = 0

    TargetParser(Actor agregator)
    {
        this.agregator = agregator
        reader = new FileReader(this,'conf','target.list').start()
    }


    void onMessage(String target)
    {
        id ++
        def split = target.split(' ')
        agregator << [url: split.first().trim(), runs: split.last().trim() as Integer, id: id]
    }

    void onMessage(Exception ex)
    {
        reader.terminate()
        agregator.terminate()
        this.terminate()
    }
}
