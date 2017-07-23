package com.ghost.runner

import com.ghost.proxy.GhostProxy
import com.ghost.target.Target
import com.ghost.utils.Config
import com.ghost.utils.Hash
import geb.Browser
import groovy.transform.ToString
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.LoggerFactory

import java.util.logging.Level

@ToString
class ChromeRunnerActor  extends DefaultActor implements Comparable<ChromeRunnerActor>
{
    private Browser browser
    private WebDriver driver
    private DesiredCapabilities capabilities
    GhostProxy proxy

    private String pHash
    def log
    String soundCloudPlayButton = '//*[@id=\"content\"]/div/div[3]/div/div[2]/div[2]/div/div/div[1]/button'
    String soundCloudTimer = '//*[@id="app"]/div[4]/div/div/div[3]/div[3]/div/div[2]'

    Double play = 5000d
    Integer timeout = 20

    private Actor agregator

    ChromeRunnerActor(GhostProxy proxy, Actor targetAgregator)
    {
        agregator = targetAgregator
        play = Config.getPlayThroughTime()
        timeout = Config.getBrowserTimeout()
        this.proxy = proxy
        pHash = Hash.hash(proxy.addr)
        log = LoggerFactory.getLogger("$proxy.addr:$proxy.port")

        capabilities = DesiredCapabilities.chrome()
        capabilities.setCapability("proxy", proxy.toSeleniumProxy())

        def logging = new LoggingPreferences()
        logging.enable('browser', Level.OFF)
        //logging.enable('client', Level.OFF)
        logging.enable('driver', Level.OFF)
        logging.enable('performance', Level.OFF)
        //logging.enable('server', Level.OFF)


        capabilities.setCapability('loggingPrefs', logging)
        ChromeOptions options = new ChromeOptions()
        options.addArguments('headless')
        capabilities.setCapability(ChromeOptions.CAPABILITY, options)

        driver = new ChromeDriver(capabilities)
        browser = new Browser(driver: driver)
        log.info("Set http proxy to: ${proxy.addr}:${proxy.port}")
    }

    void act()
    {
        agregator.send('next target')
        loop {
            react { Target target ->
                agregator.send(runTarget(target))
            }
        }
    }

    GhostProxy getProxy()
    {
        proxy
    }

    private Target runTarget(Target target)
    {
        def tStop = true
        try {
            browser.go(target.url)
            //sleep(1500)
            //browser.driver.findElement(By.xpath(soundCloudPlayButton)).click()
            def value = browser.driver.findElement(By.xpath(soundCloudTimer)).getAttribute('aria-valuenow') as Double
            def task = new Timer().runAfter(timeout*1000) {
                tStop = false
                value = 2*play
                log.error("Timeout")
            }
            while( value < play )
            {
                if (tStop)
                    value = browser.driver.findElement(By.xpath(soundCloudTimer)).getAttribute('aria-valuenow') as Double
            }
            browser.driver.findElement(By.xpath(soundCloudPlayButton)).click()
            task.cancel()
        }catch(Exception e) { log.error(e.message) }
        return target
    }

    @Override
    int compareTo(ChromeRunnerActor o)
    {
        (proxy.min + proxy.max + proxy.avg) <=> (o.proxy.min + o.proxy.max + o.proxy.avg)
    }

    @Override
    String toString()
    {
        proxy.addr
    }
}

