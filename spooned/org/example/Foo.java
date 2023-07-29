package org.example;
public class Foo implements org.example.IFoo {
    public boolean equal(int i, int i1) {
        while (i == 0) {
            i = i + i1;
        } 
        if (i != i1) {
            return true;
        } else {
            return false;
        }
    }
}