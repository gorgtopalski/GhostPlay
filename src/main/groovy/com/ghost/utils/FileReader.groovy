package com.ghost.utils

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import org.slf4j.LoggerFactory

class FileReader extends DefaultActor
{
    private Actor parser
    private Actor monitor
    private String path
    private HashSet<String> set
    private String dir
    private String fileName


    FileReader(Actor parser, String dir = 'conf', String fileName)
    {
        this.parser = parser
        this.path = "$dir/$fileName"
        this.dir = dir
        this.fileName = fileName
        this.set = new HashSet<>()
    }

    void afterStart()
    {
        readFile(new File(path))
        monitor = new FileMonitorActor(this,dir,fileName).start()
    }

    void act()
    {
        loop {
            react { File file -> readFile(file) }
        }
    }

    private void readFile(File file)
    {
        def values = false
        if (file.exists())
        {
            file.readLines().each { line ->
                if (!line.startsWith('#') && !line.empty)
                {
                    values = true
                    if (set.add(line))
                        parser << line
                }
            }
        }
        if (!values) {
            LoggerFactory.getLogger(this.class).error('No proxies or targets found')
            System.exit(1)
        }
    }
}
