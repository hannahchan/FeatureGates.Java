package featuregates.helpers;

import java.util.Random;

public class TestDelegates
{
    public static boolean booleanFunction()
    {
        return new Random().nextDouble() >= 0.5;
    }

    public static void action()
    {
        // Do nothing
    }

    public static <TResult> TResult function()
    {
        return null;
    }
}
