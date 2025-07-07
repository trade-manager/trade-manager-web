/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */

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
import java.util.Vector;

/**
 *
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
        } catch (IOException e) {
            // ignore
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
        synchronized (loadedClasses) {
            loadedClass = loadedClasses.get(className);
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

        } catch (MalformedURLException e) {
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
     * @param parm           Vector<Object>
     * @return Object
     */
    public Object newProxyInstance(Class<?> interfaceClass, String implClassName, Vector<Object> parm)
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

        Javac javac;

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

            // prepare compiler
            this.javac = new Javac(compileClasspath.replace("%20", " "), binDir.getAbsolutePath());

            // class loader
            recreateClassLoader();
        }

        void recreateClassLoader() {
            try {
                classLoader = new URLClassLoader(new URL[]{binDir.toURI().toURL()}, parentClassLoader);
            } catch (MalformedURLException e) {
                // should not happen
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
            this.binFile = new File(src.binDir, path + ".class");

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

                error = srcDir.javac.compile(new File[]{srcFile});
            }

            if (error != null) {
                throw new Exception("Failed to compile " + srcFile.getAbsolutePath() + ". Error: " + error);
            }

            try {
                // load class
                clazz = srcDir.classLoader.loadClass(className);

                // load class success, remember timestamp
                lastModified = srcFile.lastModified();

            } catch (ClassNotFoundException e) {
                throw new Exception("Failed to load DynaCode class " + srcFile.getAbsolutePath());
            }
        }
    }

    /**
     *
     */
    private class MyInvocationHandler implements InvocationHandler {

        String backendClassName;

        Object backend;
        Vector<Object> parm;

        /**
         * Constructor for MyInvocationHandler.
         *
         * @param className String
         * @param parm      Vector<Object>
         */
        MyInvocationHandler(String className, Vector<Object> parm) throws Exception {
            backendClassName = className;
            this.parm = parm;
            try {
                Class<?> clz = loadClass(backendClassName);
                backend = newDynaCodeInstance(clz);

            } catch (ClassNotFoundException e) {
                throw new Exception(e);
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

            } catch (ClassNotFoundException e) {
                throw new Exception(e);
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

            } catch (InvocationTargetException e) {
                throw e.getTargetException();
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
            } catch (Exception e) {
                throw new Exception("Failed to new instance of DynaCode class " + clz.getName(), e);
            }
        }

    }

    /**
     * Method getCreateClass.
     *
     * @param clz  Class<?>
     * @param parm Vector<Object>
     * @return Object
     */
    private static Object getCreateClass(Class<?> clz, Vector<Object> parm) throws
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int vectorSize;
        vectorSize = parm.size();
        Object instance = null;

        Class<?>[] parms = new Class[vectorSize];
        Object[] object = new Object[vectorSize];
        StringBuilder classes = new StringBuilder();
        int i = 0;
        for (Object obj : parm) {
            if (classes.isEmpty()) {
                classes.append(obj.getClass().getName());
            } else {
                classes.append(",").append(obj.getClass().getName());
            }
            parms[i] = obj.getClass();
            object[i] = obj;
            i++;
        }

        Constructor<?> constructor;

        try {
            constructor = clz.getDeclaredConstructor(parms);
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