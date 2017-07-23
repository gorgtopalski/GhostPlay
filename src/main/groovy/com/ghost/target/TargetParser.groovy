package com.ghost.target

import com.ghost.utils.FileReader
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor

class TargetParser extends DefaultActor
{
    private Actor agregator
    private int id = 0

    TargetParser(Actor agregator)
    {
        this.agregator = agregator
    }

    void afterStart()
    {
        new FileReader(this,'conf','target.list').start()
    }

    void act()
    {
        loop {
            react { String target ->
                id ++
                def split = target.split(' ')
                agregator << [url: split.first().trim(), runs: split.last().trim() as Integer, id: id]
            }
        }
    }
}
