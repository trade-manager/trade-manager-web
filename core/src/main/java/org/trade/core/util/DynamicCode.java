package org.trade.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public final class DynamicCode {

    private final static Logger _log = LoggerFactory.getLogger(DynamicCode.class);
    private final String compileClasspath;
    private final ClassLoader parentClassLoader;
    private final List<SourceDir> sourceDirs = new ArrayList<>();

    // class name => LoadedClass
    private final HashMap<String, LoadedClass> loadedClasses = new HashMap<>();

    public DynamicCode() {

        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Constructor for DynamicCode.
     *
     * @param parentClassLoader ClassLoader
     */
    public DynamicCode(ClassLoader parentClassLoader) {

        this(extractClasspath(parentClassLoader), parentClassLoader);
    }

    /**
     * @param compileClasspath  used to compile dynamic classes
     * @param parentClassLoader the parent of the class loader that loads all the dynamic
     *                          classes
     */
    public DynamicCode(String compileClasspath, ClassLoader parentClassLoader) {

        this.compileClasspath = compileClasspath;
        this.parentClassLoader = parentClassLoader;
    }

    /**
     * Add a directory that contains the source of dynamic java code.
     *
     * @return true if the add is successful
     */
    public boolean addSourceDir(File srcDir) {

        try {

            srcDir = srcDir.getCanonicalFile();
        } catch (IOException ex) {

            _log.info("Info: addSourceDir msg: {}", ex.getMessage());
        }

        synchronized (sourceDirs) {

            // check existence
            for (SourceDir src : sourceDirs) {

                if (src.srcDir.equals(srcDir)) {
                    return false;
                }
            }

            // add new
            SourceDir src = new SourceDir(srcDir);
            sourceDirs.add(src);
        }

        return true;
    }

    /**
     * Returns the up-to-date dynamic class by name.
     *
     * @return Class<?>
     */
    public Class<?> loadClass(String className) throws Exception {

        LoadedClass loadedClass;

        synchronized (this.loadedClasses) {

            loadedClass = this.loadedClasses.get(className);
        }

        // first access of a class
        if (loadedClass == null) {

            String resource = className.replace('.', '/') + ".java";
            SourceDir src = locateResource(resource);

            if (src == null) {

                throw new ClassNotFoundException("DynaCode class not found " + className);
            }

            synchronized (this) {

                // compile and load class
                loadedClass = new LoadedClass(className, src);

                synchronized (loadedClasses) {
                    loadedClasses.put(className, loadedClass);
                }
            }

            return loadedClass.clazz;
        }

        // subsequent access
        if (loadedClass.isChanged()) {

            // unload and load again
            unload(loadedClass.srcDir);
            return loadClass(className);
        }

        return loadedClass.clazz;
    }

    /**
     * Method locateResource.
     *
     * @param resource String
     * @return SourceDir
     */
    private SourceDir locateResource(String resource) {

        for (SourceDir src : sourceDirs) {

            if (new File(src.srcDir, resource).exists()) {

                return src;
            }
        }
        return null;
    }

    /**
     * Method unload.
     *
     * @param src SourceDir
     */
    private void unload(SourceDir src) {

        // clear loaded classes
        synchronized (loadedClasses) {

            loadedClasses.values().removeIf(loadedClass -> loadedClass.srcDir == src);
        }

        // create new class loader
        src.recreateClassLoader();
    }

    /**
     * Get a resource from added source directories.
     *
     * @return the resource URL, or null if resource not found
     */
    public URL getResource(String resource) {

        try {

            SourceDir src = locateResource(resource);
            return src == null ? null : new File(src.srcDir, resource).toURI().toURL();

        } catch (MalformedURLException ex) {

            _log.info("Info: getResource msg: {}", ex.getMessage());
            // should not happen
            return null;
        }
    }

    /**
     * Create a proxy instance that implements the specified access interface
     * and delegates incoming invocations to the specified dynamic
     * implementation. The dynamic implementation may change at run-time, and
     * the proxy will always delegates to the up-to-date implementation.
     *
     * @param interfaceClass the access interface
     * @param implClassName  the backend dynamic implementation
     * @return Object
     * @throws Exception if an instance cannot be created, because of class not found
     *                   for example
     */
    public Object newProxyInstance(Class<?> interfaceClass, String implClassName) throws Exception {

        MyInvocationHandler handler = new MyInvocationHandler(implClassName);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, handler);
    }

    /**
     * Method newProxyInstance.
     *
     * @param interfaceClass Class<?>
     * @param implClassName  String
     * @param parm           List<Object>
     * @return Object
     */
    public Object newProxyInstance(Class<?> interfaceClass, String implClassName, List<Object> parm)
            throws Exception {

        MyInvocationHandler handler = new MyInvocationHandler(implClassName, parm);
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, handler);
    }

    /**
     *
     */
    private class SourceDir {

        File srcDir;
        File binDir;
        InlineCompiler inlineCompiler;
        URLClassLoader classLoader;

        /**
         * Constructor for SourceDir.
         *
         * @param srcDir File
         */
        SourceDir(File srcDir) {

            this.srcDir = srcDir;
            String subdir = srcDir.getAbsolutePath().replace(':', '_').replace('/', '_').replace('\\', '_');
            this.binDir = new File(System.getProperty("java.io.tmpdir"), "bin/" + subdir);
            this.binDir.mkdirs();
            this.binDir.deleteOnExit();

            // prepare compiler
            this.inlineCompiler = new InlineCompiler(compileClasspath.replace("%20", " "), binDir.getAbsolutePath());

            // class loader
            recreateClassLoader();
        }

        void recreateClassLoader() {

            try {

                URL[] urls = {
                        binDir.toURI().toURL(),
                        this.srcDir.toURI().toURL(),
                        /*,
                        new URL("file:../core/target/classes/"),
                        new URL("file:../core/target/trade-manager-core-1.0.0-SNAPSHOT.jar")
                        */
                };

                classLoader = new URLClassLoader(urls, parentClassLoader);
            } catch (MalformedURLException ex) {

                _log.error("Error: recreateClassLoader msg: {}", ex.getMessage());
            }
        }
    }

    /**
     *
     */
    private static class LoadedClass {

        String className;
        SourceDir srcDir;
        File srcFile;
        File binFile;
        Class<?> clazz;
        long lastModified;

        /**
         * Constructor for LoadedClass.
         *
         * @param className String
         * @param src       SourceDir
         */
        LoadedClass(String className, SourceDir src) throws Exception {

            this.className = className;
            this.srcDir = src;
            String path = className.replace('.', '/');
            this.srcFile = new File(src.srcDir, path + ".java");
            this.binFile = new File(src.srcDir, path + ".class");

            compileAndLoadClass();
        }

        /**
         * Method isChanged.
         *
         * @return boolean
         */
        boolean isChanged() {
            return srcFile.lastModified() != lastModified;
        }

        void compileAndLoadClass() throws Exception {

            if (clazz != null) {

                return; // class already loaded
            }

            // compile, if required
            String error = null;

            if (binFile.lastModified() < srcFile.lastModified()) {

                error = srcDir.inlineCompiler.compile(List.of(srcFile));
            }

            if (error != null) {

                throw new Exception("Failed to compile " + srcFile.getAbsolutePath() + ". Error: " + error);
            }

            try {
                // load class
                clazz = srcDir.classLoader.loadClass(className);

                // load class success, remember timestamp
                lastModified = srcFile.lastModified();

            } catch (ClassNotFoundException ex) {

                throw new Exception("Failed to load DynaCode class " + srcFile.getAbsolutePath() + " msg: " + ex.getMessage());
            }
        }
    }

    /**
     *
     */
    private class MyInvocationHandler implements InvocationHandler {

        String backendClassName;
        Object backend;
        List<Object> parm;

        /**
         * Constructor for MyInvocationHandler.
         *
         * @param className String
         * @param parm      List<Object>
         */
        MyInvocationHandler(String className, List<Object> parm) throws Exception {

            backendClassName = className;
            this.parm = parm;

            try {

                Class<?> clz = loadClass(backendClassName);
                backend = newDynaCodeInstance(clz);
            } catch (ClassNotFoundException ex) {

                throw new Exception(ex);
            }
        }

        /**
         * Constructor for MyInvocationHandler.
         *
         * @param className String
         */
        MyInvocationHandler(String className) throws Exception {

            backendClassName = className;

            try {

                Class<?> clz = loadClass(backendClassName);
                backend = newDynaCodeInstance(clz);
            } catch (ClassNotFoundException ex) {

                throw new Exception(ex);
            }
        }

        /**
         * Method invoke.
         *
         * @param proxy  Object
         * @param method Method
         * @param args   Object[]
         * @return Object
         * @see InvocationHandler#invoke(Object, Method,
         * Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // check if class has been updated
            Class<?> clz = loadClass(backendClassName);

            if (backend.getClass() != clz) {

                backend = newDynaCodeInstance(clz);
            }

            try {

                // invoke on backend
                return method.invoke(backend, args);
            } catch (InvocationTargetException ex) {

                throw ex.getTargetException();
            }
        }

        /**
         * Method newDynaCodeInstance.
         *
         * @param clz Class<?>
         * @return Object
         */
        private Object newDynaCodeInstance(Class<?> clz) throws Exception {

            try {

                // return clz.newInstance();
                return getCreateClass(clz, this.parm);
            } catch (Exception ex) {

                throw new Exception("Failed to new instance of DynaCode class " + clz.getName(), ex);
            }
        }
    }

    /**
     * Method getCreateClass.
     *
     * @param clz    Class<?>
     * @param params List<Object>
     * @return Object
     */
    private static Object getCreateClass(Class<?> clz, List<Object> params) throws
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        int listSize = params.size();
        Object instance = null;
        Class<?>[] paramClasses = new Class[listSize];
        Object[] object = new Object[listSize];
        StringBuilder classes = new StringBuilder();
        int i = 0;

        for (Object obj : params) {

            if (classes.isEmpty()) {

                classes.append(obj.getClass().getName());
            } else {

                classes.append(",").append(obj.getClass().getName());
            }

            paramClasses[i] = obj.getClass();
            object[i] = obj;
            i++;
        }

        Constructor<?> constructor;

        try {

            constructor = clz.getDeclaredConstructor(paramClasses);
            instance = constructor.newInstance(object);
        } catch (Exception e) {

            _log.debug("Could not find constructor for default parms[{}] will test all constructors.", classes);
            Constructor<?>[] constructors = clz.getConstructors();

            for (Constructor<?> constructor2 : constructors) {

                try {

                    instance = constructor2.newInstance(object);
                    _log.debug("Found constructor: {} for parms[{}]", constructor2.toGenericString(), classes);
                    break;
                } catch (Exception ex) {

                    _log.info("Constructor: {} failed!!", constructor2.toGenericString());
                }
            }
        }

        if (null == instance) {

            instance = clz.getDeclaredConstructor().newInstance();
        }

        return instance;
    }

    /**
     * Extracts a classpath string from a given class loader. Recognizes only
     * URLClassLoader.
     *
     * @param cl ClassLoader
     * @return String
     */
    private static String extractClasspath(ClassLoader cl) {

        StringBuilder buf = new StringBuilder();

        while (cl != null) {

            if (cl instanceof URLClassLoader) {

                URL[] urls = ((URLClassLoader) cl).getURLs();

                for (URL url : urls) {

                    if (!buf.isEmpty()) {

                        buf.append(File.pathSeparatorChar);
                    }
                    buf.append(url.getFile());
                }
            }

            cl = cl.getParent();
        }

        return buf.toString();
    }
}