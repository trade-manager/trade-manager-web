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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public final class InlineCompiler {

    private final static Logger _log = LoggerFactory.getLogger(InlineCompiler.class);

    private String classpath;
    private String outputdir;
    private String sourcepath;
    private String bootclasspath;
    private String extdirs;
    private String encoding;
    private String target;

    /**
     * Constructor for Javac.
     *
     * @param classpath String
     * @param outputdir String
     */
    public InlineCompiler(String classpath, String outputdir) {

        this.classpath = classpath;
        this.outputdir = outputdir;
    }


    /**
     * Method compile.
     *
     * @param srcFiles File[]
     * @return String
     */
    public String compile(List<File> srcFiles) {

        StringBuffer results = new StringBuffer();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<>();
            optionList.add("-classpath");
            optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(srcFiles);
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

    /**
     * Method buildJavacArgs.
     *
     * @param srcFiles String[]
     * @return String[]
     */
    private String[] buildJavacArgs(String[] srcFiles) {
        List<String> args = new ArrayList<>();

        if (classpath != null) {
            args.add("-classpath");
            args.add(classpath);
        }
        if (outputdir != null) {
            args.add("-d");
            args.add(outputdir);
        }
        if (sourcepath != null) {
            args.add("-sourcepath");
            args.add(sourcepath);
        }
        if (bootclasspath != null) {
            args.add("-bootclasspath");
            args.add(bootclasspath);
        }
        if (extdirs != null) {
            args.add("-extdirs");
            args.add(extdirs);
        }
        if (encoding != null) {
            args.add("-encoding");
            args.add(encoding);
        }
        if (target != null) {
            args.add("-target");
            args.add(target);
        }

        Collections.addAll(args, srcFiles);

        return args.toArray(new String[0]);
    }

    /**
     * Method getBootclasspath.
     *
     * @return String
     */
    public String getBootclasspath() {
        return bootclasspath;
    }

    /**
     * Method setBootclasspath.
     *
     * @param bootclasspath String
     */
    public void setBootclasspath(String bootclasspath) {
        this.bootclasspath = bootclasspath;
    }

    /**
     * Method getClasspath.
     *
     * @return String
     */
    public String getClasspath() {
        return classpath;
    }

    /**
     * Method setClasspath.
     *
     * @param classpath String
     */
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Method getEncoding.
     *
     * @return String
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Method setEncoding.
     *
     * @param encoding String
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Method getExtdirs.
     *
     * @return String
     */
    public String getExtdirs() {
        return extdirs;
    }

    /**
     * Method setExtdirs.
     *
     * @param extdirs String
     */
    public void setExtdirs(String extdirs) {
        this.extdirs = extdirs;
    }

    /**
     * Method getOutputdir.
     *
     * @return String
     */
    public String getOutputdir() {
        return outputdir;
    }

    /**
     * Method setOutputdir.
     *
     * @param outputdir String
     */
    public void setOutputdir(String outputdir) {
        this.outputdir = outputdir;
    }

    /**
     * Method getSourcepath.
     *
     * @return String
     */
    public String getSourcepath() {
        return sourcepath;
    }

    /**
     * Method setSourcepath.
     *
     * @param sourcepath String
     */
    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    /**
     * Method getTarget.
     *
     * @return String
     */
    public String getTarget() {
        return target;
    }

    /**
     * Method setTarget.
     *
     * @param target String
     */
    public void setTarget(String target) {
        this.target = target;
    }

}
