package com.ghost.utils

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import org.slf4j.LoggerFactory

import java.util.stream.Collector
import java.util.stream.Collectors

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
        if (file.exists())
        {
            def lines = file.readLines().stream()
                    .filter { line -> !line.startsWith('#') }
                    .filter { line -> !line.empty }
                    .filter { line -> set.add(line) }
                    .collect(Collectors.toList())

            if (set.size() == 0)
            {
                LoggerFactory.getLogger(this.class).error("$file.name is empty")
                parser << new IOException("$file.name is empty")
                this.terminate().join()
            }

            lines.each { parser << it }
        }
    }
}
