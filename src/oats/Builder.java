package oats;

import java.io.IOException;

public class Builder
{
    private final BuildInfo buildInfo;

    public Builder(BuildInfo buildInfo)
    {
        this.buildInfo = buildInfo;
    }

    public void buildProject()
    {
        //Creating it here because I can't in the parser since it doesn't know the binary type.
        buildInfo.outputName = createOutputName(buildInfo.outputName);
        printBuildInfo();

        String params = constructCompilerParams();
        System.out.println("params: " + params + "\n");

        compilationMessage(compile(params));
    }

    private void compilationMessage(int result)
    {
        if(result == 0)
        {
            System.out.println("Compilation succeeded.");
        }
        else
        {
            System.out.println("Compilation failed.");
            System.exit(1);
        }
    }

    private String constructCompilerParams()
    {
        switch(buildInfo.compiler)
        {
            case GCC: return constructParamsGcc();
            case CLANG: return constructParamsClang();
            case CLANG_CL: return constructParamsClangCl();
            case CL: return constructParamsCl();
            case OTHER: return constructParamsOther();
        }

        return null;
    }

    private String constructParamsGcc()
    {
        String compilerFlags = " -Wall -std=" + buildInfo.langStd;

        if(buildInfo.buildType == BuildType.RELEASE)
        {
            compilerFlags = compilerFlags + " -O2";
        }
        else if(buildInfo.buildType == BuildType.DEBUG)
        {
            compilerFlags = compilerFlags + " -O0 -g";
        }
        else
        {
            compilerFlags = buildInfo.customBuildFlags;
        }

        if(buildInfo.binaryType == BinaryType.EXECUTABLE)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -o " + buildInfo.outputName +
                    " " + buildInfo.linkedFlags +
                    getGccLibraries();
        }
        else if(buildInfo.binaryType == BinaryType.SHARED_LIB)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -shared" +
                    " -o " + buildInfo.outputName +
                    " " + buildInfo.linkedFlags +
                    getGccLibraries();
        }
        else
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -c";
        }

        return compilerFlags;
    }

    private String constructParamsClang()
    {
        String compilerFlags = " -mno-incremental-linker-compatible -Wall -std=" + buildInfo.langStd;

        if(buildInfo.buildType == BuildType.RELEASE)
        {
            compilerFlags = compilerFlags + " -O2";
        }
        else if(buildInfo.buildType == BuildType.DEBUG)
        {
            compilerFlags = compilerFlags + " -O0 -g";
        }
        else
        {
            compilerFlags = buildInfo.customBuildFlags;
        }

        if(buildInfo.binaryType == BinaryType.EXECUTABLE)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -o " + buildInfo.outputName +
                    " " + buildInfo.linkedFlags +
                    getGccLibraries();
        }
        else if(buildInfo.binaryType == BinaryType.SHARED_LIB)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -shared" +
                    " -o " + buildInfo.outputName +
                    " " + buildInfo.linkedFlags +
                    getGccLibraries();
        }
        else
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " -c";
        }

        return compilerFlags;
    }

    private String constructParamsClangCl()
    {
        String compilerFlags = " /FC /W4 -Xclang -std=" + buildInfo.langStd;

        if(buildInfo.buildType == BuildType.RELEASE)
        {
            compilerFlags = compilerFlags + " /O2 /Oi /fp:fast /MT";
        }
        else if(buildInfo.buildType == BuildType.DEBUG)
        {
            compilerFlags = compilerFlags + " /Od /Zi";

            if(!buildInfo.compilerFlags.contains("fsanitize"))
            {
                compilerFlags = compilerFlags + " /MTd";
            }
        }
        else
        {
            compilerFlags = compilerFlags + buildInfo.customBuildFlags;
        }

        if(buildInfo.binaryType == BinaryType.EXECUTABLE)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " /o " + buildInfo.outputName +
                    " /link /INCREMENTAL:NO /OPT:REF" +
                    " " + buildInfo.linkedFlags +
                    getLibraries();
        }
        else if(buildInfo.binaryType == BinaryType.SHARED_LIB)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " /o " + buildInfo.outputName +
                    " /link /INCREMENTAL:NO /OPT:REF" +
                    " " + buildInfo.linkedFlags +
                    getLibraries() +
                    " /DLL";
        }
        else
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    " /c /Fo\"" + buildInfo.outputName + "\"\\" +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories();
        }

        return compilerFlags;
    }

    private String constructParamsCl()
    {
        String compilerFlags = " /FC /W4 /std:" + buildInfo.langStd;

        if(buildInfo.buildType == BuildType.RELEASE)
        {
            compilerFlags = compilerFlags + " /O2 /Oi /fp:fast /MT";
        }
        else if(buildInfo.buildType == BuildType.DEBUG)
        {
            compilerFlags = compilerFlags + " /Od /Zi /MTd";
        }
        else
        {
            compilerFlags = compilerFlags + buildInfo.customBuildFlags;
        }

        if(buildInfo.binaryType == BinaryType.EXECUTABLE)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " /o " + buildInfo.outputName +
                    " /link /INCREMENTAL:NO /OPT:REF" +
                    " " + buildInfo.linkedFlags +
                    getLibraries();
        }
        else if(buildInfo.binaryType == BinaryType.SHARED_LIB)
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories() +
                    " /o " + buildInfo.outputName +
                    " /link /INCREMENTAL:NO /OPT:REF" +
                    " " + buildInfo.linkedFlags +
                    getLibraries() +
                    " /DLL";
        }
        else
        {
            compilerFlags = compilerFlags +
                    " " + buildInfo.compilerFlags +
                    " /c /Fo\"" + buildInfo.outputName + "\"\\" +
                    getConstants() +
                    getIncludePaths() +
                    getSourceFiles() +
                    getSourceDirectories();
        }

        return compilerFlags;
    }

    private String constructParamsOther()
    {
        return buildInfo.customBuildFlags;
    }

    private String getIncludePaths()
    {
        String result = "";

        for(String s : buildInfo.includePaths)
        {
            result = result + " -I " + s;
        }

        return result;
    }

    private String getSourceFiles()
    {
        String result = "";

        for(String s : buildInfo.sourceFiles)
        {
            result = result + " " + s;
        }

        return result;
    }

    private String getSourceDirectories()
    {
        String result = "";

        for(String s : buildInfo.directories)
        {
            result = result + " " + s + "\\*.c";
        }

        return result;
    }

    private String getConstants()
    {
        String result = "";

        for(String s : buildInfo.constants)
        {
            result = result + " -D " + s;
        }

        return result;
    }

    private String getGccLibraries()
    {
        String result = "";

        for(String s : buildInfo.libraries)
        {
            if(s.endsWith(OSInfo.getObjectFileExtension()))
            {
                result = result + " " + s;
            }
            else
            {
                result = result + " -l" + s;
            }
        }

        return result;
    }

    private String getLibraries()
    {
        String result = "";

        for(String s : buildInfo.libraries)
        {
            result = result + " " + s + OSInfo.getStaticLibraryExtension();
        }

        return result;
    }

    private void printBuildInfo()
    {
        System.out.println("compiler: " + buildInfo.compiler);
        System.out.println("compiler command: " + buildInfo.compilerCommand);
        System.out.println("language standard: " + buildInfo.langStd);
        System.out.println("custom build flags: " + buildInfo.customBuildFlags);
        System.out.println("compiler flags: " + buildInfo.compilerFlags);
        System.out.println("linker flags: " + buildInfo.linkedFlags);
        System.out.println("binary type: " + buildInfo.binaryType);
        System.out.println("build type: " + buildInfo.buildType);
        System.out.println("output name: " + buildInfo.outputName);

        for(String s : buildInfo.sourceFiles)
        {
            System.out.println("file: " + s);
        }

        for(String s : buildInfo.directories)
        {
            System.out.println("directory: " + s);
        }

        for(String s : buildInfo.constants)
        {
            System.out.println("constant: " + s);
        }

        for(String s : buildInfo.includePaths)
        {
            System.out.println("include path: " + s);
        }

        for(String s : buildInfo.libraries)
        {
            System.out.println("library: " + s);
        }
    }

    private String createOutputName(String name)
    {
        switch(buildInfo.binaryType)
        {
            case EXECUTABLE: return name + OSInfo.getExecutableExtension();
            case OBJECT_LIB: return name;
            case SHARED_LIB: return name + OSInfo.getSharedLibraryExtension();
        }

        return null;
    }

    private int compile(String params)
    {
        String command = buildInfo.compilerCommand + " " + params;
        return launchCommand(command);
    }

    private int launchCommand(String command)
    {
        if(OSInfo.OS == OSInfo.WINDOWS)
        {
            try
            {
                return new ProcessBuilder("cmd", "/c", command).inheritIO().start().waitFor();
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
            catch(InterruptedException e)
            {
                System.err.println(e.getMessage());
            }
        }
        else
        {
            try
            {
                return new ProcessBuilder("bash", command).inheritIO().start().waitFor();
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
            catch(InterruptedException e)
            {
                System.err.println(e.getMessage());
            }
        }

        return -1;
    }
}
