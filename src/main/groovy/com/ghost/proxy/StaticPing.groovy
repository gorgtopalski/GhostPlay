package com.ghost.proxy

class StaticPing
{
    static GhostProxy ping(GhostProxy proxy)
    {
        def exec = "ping -c 4 -q ${proxy.addr}"
        def proc = exec.execute()
        proc.waitFor()

        if (proc.exitValue() == 0 )
        {
            try {
                def output = proc.in.text
                output = output.substring(output.lastIndexOf('rtt'))
                output = output.substring(output.lastIndexOf('='))
                output = output.substring(2,output.lastIndexOf(' ms'))

                StringTokenizer tk = new StringTokenizer(output, '/')

                proxy.min = tk.nextToken() as Double
                proxy.avg = tk.nextToken() as Double
                proxy.max = tk.nextToken() as Double
            }
            catch(Exception e){}
        }
        else {
            proxy.min = -1
            proxy.max = -1
            proxy.avg = -1
        }
        return proxy
    }
}
