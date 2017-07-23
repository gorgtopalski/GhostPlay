package com.ghost.utils

import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor

import java.nio.file.*

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class FileMonitorActor extends DefaultActor
{
    private WatchService watcher = FileSystems.getDefault().newWatchService();
    private Path logDir
    private Actor notify
    private String fileName
    private String path

    FileMonitorActor(Actor notify, String path = 'conf', String fileName)
    {
        this.logDir = Paths.get(path)
        logDir.register(watcher, ENTRY_MODIFY);
        this.notify = notify
        this.fileName = fileName
        this.path = path
    }

    void act()
    {
        loop {
            WatchKey key = watcher.take()

            key.pollEvents().each { event ->

                if (event.kind() == ENTRY_MODIFY && event.context().toString() == fileName)
                    notify << new File("$path/$fileName")
            }
            key.reset()
        }
    }
}
