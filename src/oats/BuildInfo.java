package oats;

import java.util.LinkedList;
import java.util.List;

public class BuildInfo
{
    public CompilerType compiler;
    public String compilerCommand;

    public String langStd;

    public String customBuildFlags;
    public String compilerFlags;

    public String linkedFlags;

    public String outputName;

    public BinaryType binaryType;

    public BuildType buildType;

    public List<String> sourceFiles;
    public List<String> directories;
    public List<String> constants;
    public List<String> includePaths;
    public List<String> libraries;

    public BuildInfo()
    {
        compiler = CompilerType.GCC;
        compilerCommand = "clang";
        langStd = "c17";
        customBuildFlags = "";
        compilerFlags = "";
        linkedFlags = "";
        binaryType = BinaryType.EXECUTABLE;
        buildType = BuildType.RELEASE;
        outputName = "out";

        sourceFiles = new LinkedList<>();
        directories = new LinkedList<>();
        constants = new LinkedList<>();
        includePaths = new LinkedList<>();
        libraries = new LinkedList<>();
    }

    public void addFile(String file)
    {
        sourceFiles.add(file);
    }

    public void addDirectory(String directory)
    {
        directories.add(directory);
    }

    public void addConstant(String constant)
    {
        constants.add(constant);
    }

    public void addIncludePath(String path)
    {
        includePaths.add(path);
    }

    public void addLibrary(String library)
    {
        libraries.add(library);
    }
}
