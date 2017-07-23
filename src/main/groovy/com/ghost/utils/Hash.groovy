package com.ghost.utils

import java.nio.charset.Charset
import java.security.MessageDigest

class Hash
{
    static String hash(String str)
    {
        MessageDigest.getInstance('MD5').digest(str.getBytes(Charset.defaultCharset())).toString()
    }


}
