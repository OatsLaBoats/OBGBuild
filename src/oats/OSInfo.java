package oats;

public class OSInfo
{
    public static final int WINDOWS = 1;
    public static final int UNIX = 2;

    public static final int OS;

    static
    {
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("win"))
        {
            OS = WINDOWS;
        }
        else
        {
            OS = UNIX;
        }
    }

    public static String getStaticLibraryExtension()
    {
        if(OS == WINDOWS)
        {
            return ".lib";
        }

        return ".a";
    }

    public static String getSharedLibraryExtension()
    {
        if(OS == WINDOWS)
        {
            return ".dll";
        }

        return ".so";
    }

    public static String getExecutableExtension()
    {
        if(OS == WINDOWS)
        {
            return ".exe";
        }

        return "";
    }

    public static String getObjectFileExtension()
    {
        if(OS == WINDOWS)
        {
            return  ".obj";
        }

        return ".o";
    }

    public static int lineBreakCount()
    {
        if(OS == WINDOWS)
        {
            return 2; // \r\n
        }

        return 1; // \n
    }
}
