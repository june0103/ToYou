// IMyCounterService.aidl
package rhwns0103.com.naver.blog.toyou;

// Declare any non-default types here with import statements

interface IMyCounterService {

    int getCount();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
