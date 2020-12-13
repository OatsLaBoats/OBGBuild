package oats;

import java.io.File;

public class Parser
{
    private final String source;
    private final BuildInfo buildInfo;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private boolean error = false;

    public Parser(String source)
    {
        this.source = source;
        this.buildInfo = new BuildInfo();
    }

    public BuildInfo parseFile()
    {
        while(!isAtEnd())
        {
            parseCommand();
        }

        if(error)
        {
            System.exit(1);
        }

        return buildInfo;
    }

    private void parseCommand()
    {
        String command = getCommand();

        switch(command)
        {
            case "COMPILER": parseCompilerCommand(); break;
            case "C_VERSION": parseCVersionCommand(); break;
            case "FILE": parseFileCommand(); break;
            case "DIRECTORY": parseDirectoryCommand(); break;
            case "CONSTANT": parseConstantCommand(); break;
            case "INCLUDE": parseIncludeCommand(); break;
            case "LIBRARY": parseLibraryCommand(); break;
            case "OBJECT": parseObjectCommand(); break;
            case "TYPE": parseTypeCommand(); break;
            case "BUILD": parseBuildCommand(); break;
            case "CUSTOM_BUILD": parseCustomBuildCommand(); break;
            case "COMPILER_FLAGS": parseCompilerFlagsCommand(); break;
            case "LINKER_FLAGS": parseLinkerFlagsCommand(); break;
            case "OUTPUT": parseOutputCommand(); break;

            default:
            {
                error("Unknown command \"" + command + "\"");
                consumeLine();
            }
        }
    }

    private void parseCompilerCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("COMPILER", "requires parameter", "gcc/clang/cl/clang-cl");
            return;
        }

        buildInfo.compilerCommand = param;

        switch(param)
        {
            case "gcc": buildInfo.compiler = CompilerType.GCC; break;
            case "clang": buildInfo.compiler = CompilerType.CLANG; break;
            case "clang-cl": buildInfo.compiler = CompilerType.CLANG_CL; break;
            case "cl": buildInfo.compiler = CompilerType.CL; break;
            default: buildInfo.compiler = CompilerType.OTHER;
        }
    }

    private void parseCVersionCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("C_VERSION", "requires parameter", "c89/c99/c11/c17");
            return;
        }

        switch(param)
        {
            case "c89":
            case "c99":
            case "c11":
            case "c17": buildInfo.langStd = param; break;
            default: commandError("C_VERSION", "invalid parameter \"" + param + "\"", "c89/c99/c11/c17");
        }
    }

    private void parseFileCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("FILE", "requires parameter", "file_name.c");
            return;
        }

        if(!param.endsWith(".c"))
        {
            commandError("FILE", "only \".c\" files can be added", "file_name.c");
            return;
        }

        if(!fileExists(param))
        {
            commandError("FILE", param + " is not a file or it doesn't exist");
            return;
        }

        buildInfo.addFile(param);
    }

    private void parseDirectoryCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("DIRECTORY", "requires parameter", "directory_name");
            return;
        }

        if(!directoryExists(param))
        {
            commandError("DIRECTORY", param + " is not a directory or it doesn't exist");
            return;
        }

        buildInfo.addDirectory(param);
    }

    private void parseConstantCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("CONSTANT", "requires parameter", "name=value");
            return;
        }

        buildInfo.addConstant(param);
    }

    private void parseIncludeCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("INCLUDE", "requires parameter", "include_path");
            return;
        }

        if(!directoryExists(param))
        {
            commandError("INCLUDE", param + " is not a directory or it doesn't exits");
            return;
        }

        buildInfo.addIncludePath(param);
    }

    private void parseLibraryCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("LIBRARY", "requires parameter", "library_name");
            return;
        }

        if(param.endsWith(OSInfo.getStaticLibraryExtension()))
        {
            commandError("LIBRARY", "libraries should not have a file extension", "library_name");
            return;
        }

        buildInfo.addLibrary(param);
    }

    private void parseObjectCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("OBJECT", "requires parameter", "object_file_name");
            return;
        }

        if(param.endsWith(OSInfo.getObjectFileExtension()))
        {
            commandError("OBJECT", "object files should not have a file extension", "object_file_name");
            return;
        }

        buildInfo.addLibrary(param + OSInfo.getObjectFileExtension());
    }

    private void parseTypeCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("TYPE", "requires parameter", "object/shared/executable");
            return;
        }

        switch(param)
        {
            case "object": buildInfo.binaryType = BinaryType.OBJECT_LIB; break;
            case "shared": buildInfo.binaryType = BinaryType.SHARED_LIB; break;
            case "executable": buildInfo.binaryType = BinaryType.EXECUTABLE; break;
            default: commandError("TYPE", param + " is not a known binary type", "object/shared/executable");
        }
    }

    private void parseBuildCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("BUILD", "requires parameter", "release/debug/custom");
            return;
        }

        switch(param)
        {
            case "release": buildInfo.buildType = BuildType.RELEASE; break;
            case  "debug": buildInfo.buildType = BuildType.DEBUG; break;
            default: buildInfo.buildType = BuildType.CUSTOM;
        }
    }

    private void parseCustomBuildCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("CUSTOM_BUILD", "requires parameter", "compiler_flag1 compiler_flag2...");
            return;
        }

        buildInfo.customBuildFlags = param;
    }

    private void parseCompilerFlagsCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("COMPILER_FLAGS", "requires parameter", "-compiler_flag1 -compiler_flag2...");
            return;
        }

        buildInfo.compilerFlags = param;
    }

    private void parseLinkerFlagsCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("LINKER_FLAGS", "requires parameter", "-linker_flag1 -linker_flag2...");
            return;
        }

        buildInfo.linkedFlags = param;
    }

    private void parseOutputCommand()
    {
        String param = getParameter();
        if(param == null)
        {
            commandError("OUTPUT", "requires parameter", "output_name");
            return;
        }

        if(param.contains("."))
        {
            commandError("OUTPUT", "output name should not have a file extension", "output_name");
            return;
        }

        buildInfo.outputName = param;
    }

    private boolean directoryExists(String name)
    {
        File file = new File(name);
        return file.isDirectory();
    }

    private boolean fileExists(String name)
    {
        File file = new File(name);
        return file.isFile();
    }

    private void error(String message)
    {
        error = true;
        System.err.println("[" + getLineNumber() + "] " + message + ".");
    }

    private void commandError(String command, String message, String exampleUsage)
    {
        error(command + ": " + message + " " + "( " + exampleUsage + " )");
    }

    private void commandError(String command, String message)
    {
        error(command + ": " + message);
    }

    private void consumeLine()
    {
        while(peek() != '\n' && !isAtEnd())
        {
            advance();
        }

        advance();
        syncIndexes();
    }

    private char peek()
    {
        if(isAtEnd())
        {
            return '\0';
        }

        return source.charAt(current);
    }

    private char advance()
    {
        if(isAtEnd())
        {
            return '\0';
        }

        ++current;
        char result = source.charAt(current - 1);
        if(result == '\n')
        {
            ++line;
        }

        return result;
    }

    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    private int getLineNumber()
    {
        if(isAtEnd())
        {
            return line;
        }

        return line - 1;
    }

    private String getCommand()
    {
        syncIndexes();
        while(peek() != ':')
        {
            if(peek() == '\n')
            {
                advance();
                syncIndexes();
                continue;
            }

            advance();
        }

        advance();

        return source.substring(start, current - 1);
    }

    private String getParameter()
    {
        syncIndexes();
        while(peek() != '\n' && peek() != '\0')
        {
            advance();
        }

        advance();

        if(start == current || (start + 2 == current && current != source.length()))
        {
            return null;
        }

        if(isAtEnd())
        {
            return source.substring(start, current);
        }

        return source.substring(start, current - OSInfo.lineBreakCount());
    }

    private void syncIndexes()
    {
        start = current;
    }
}
