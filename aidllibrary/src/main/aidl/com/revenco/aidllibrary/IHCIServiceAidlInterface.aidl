// IHCIServiceAidlInterface.aidl
package com.revenco.aidllibrary;

// Declare any non-default types here with import statements

interface IHCIServiceAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void initBroadCast();
    void destroyBroadCast();

    void resetHCI();

    void testNotify();
    void destory();
}
