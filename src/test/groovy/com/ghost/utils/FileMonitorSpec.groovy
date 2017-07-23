package com.ghost.utils

import spock.lang.Shared
import spock.lang.Specification

import static groovyx.gpars.actor.Actors.actor

class FileMonitorSpec extends Specification
{
    @Shared
    File file

    def setupSpec()
    {
        file = new File('temp.txt')
        file.createNewFile()
    }

    def "File monitoring"()
    {
        given:
        def hasChanged = false
        def notify = actor {
            react { msg -> hasChanged = true }
        }
        def fm = new FileMonitorActor(notify, '.', 'temp.txt').start()

        expect:
        assert !hasChanged

        when:
        file.write('Random line')
        notify.join()

        then:
        assert hasChanged
    }

    def cleanupSpec()
    {
        file.delete()
    }
}
