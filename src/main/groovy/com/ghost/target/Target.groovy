package com.ghost.target

import groovy.transform.ToString

@ToString
class Target implements Comparable<Target>
{
    int id
    String url

    @Override
    int compareTo(Target o) {
        this.id <=> o.id
    }
}
