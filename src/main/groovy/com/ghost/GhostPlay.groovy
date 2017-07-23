package com.ghost

import com.ghost.runner.BrowserPool

class GhostPlay
{
    static void main(String[] args)
    {
        new BrowserPool().start().join()
    }
}

