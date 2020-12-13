package oats;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

//TODO: ADD SUPPORT FOR STATIC LIBRARIES.
//TODO: SUPPORT FOR MULTIPLE DIFFERENT BUILDING OF EXECUTABLES

public class OBGBuild
{
    private static String readFile(String fileName)
    {
        try
        {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            return new String(bytes, Charset.defaultCharset());
        }
        catch(IOException e)
        {
            System.err.println("Could not open " + fileName + ".");
            System.exit(1);
        }

        return null;
    }

    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.out.println("obgbuild [buildfile]");
            System.out.println("COMPILER:gcc/clang/cl/clang-cl");
            System.out.println("C_VERSION:c89/c99/c11/c17");
            System.out.println("FILE:example.c");
            System.out.println("DIRECTORY:example_dir");
            System.out.println("CONSTANT:EXAMPLE=1");
            System.out.println("INCLUDE:example_dir");
            System.out.println("LIBRARY:example_library");
            System.out.println("OBJECT:example_object_file");
            System.out.println("TYPE:shared/object/executable");
            System.out.println("BUILD:release/debug/custom");
            System.out.println("CUSTOM_BUILD:flags");
            System.out.println("COMPILER_FLAGS:flags");
            System.out.println("LINKER_FLAGS:flags");
            System.out.println("OUTPUT:output_name");
            System.exit(1);
        }

        String buildFile = readFile(args[0]);

        Parser parser = new Parser(buildFile);
        BuildInfo buildInfo = parser.parseFile();

        Builder builder = new Builder(buildInfo);
        builder.buildProject();
    }
}
