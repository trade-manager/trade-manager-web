package org.trade.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class InlineCompiler {

    private final static Logger _log = LoggerFactory.getLogger(InlineCompiler.class);

    public static String doCompile(List<File> files) {

        StringBuffer results = new StringBuffer();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<>();
            optionList.add("-classpath");
            optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(files);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);

            if (!task.call()) {

                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {

                    results.append("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource().toUri());
                    _log.error("Error on line {} in {}", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
                    System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
                }
                return results.toString();
            }

        } catch (IOException exp) {

            _log.error("Error: doCompile msg: {}", exp.getMessage());
        }
        return null;
    }

    public static Object loadClass(String className) {

        // Create a new custom class loader, pointing to the directory that contains the compiled
        // classes, this should point to the top of the package structure!
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()})) {

            // Load the class from the classloader by name....
            _log.info("Info: className: {}", className);
            Class<?> loadedClass = classLoader.loadClass(className);

            // Create a new instance...
            return loadedClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {

            _log.error("Error: doRun msg: {}", ex.getMessage());
        }
        return null;
    }

    public interface DoStuff {

        void doStuff();
    }
}
