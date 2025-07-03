/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
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
package org.trade.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassName {

    private static final Logger _log = LoggerFactory.getLogger(ClassName.class);

    private String entireClassName = null;
    private String className = null;
    private String packageName = null;
    private String entirePackageName = null;

    /**
     * This method exists strictly to test this class
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        if (args.length != 1) {

            _log.error("Usage = java <package>.ClassName <full class name>");
            System.exit(1);
        } else {

            try {

                Class<?> aClass = Class.forName(args[0]);
                ClassName className = new ClassName(aClass);
                _log.info("Entire Package Name = {}", className.getEntirePackageName());
                _log.info("PackageName = {}", className.getPackageName());
                _log.info("Entire Class Name = {}", className.getEntireClassName());
                _log.info("Class Name = {}", className.getClassName());
            } catch (Exception ex) {

                _log.error("Error creating className: {}, msg: {}", args[0], ex.getMessage());
            }
        }
    }

    /**
     * Constructor for ClassName.
     *
     * @param aClass Class<?>
     */
    public ClassName(Class<?> aClass) {

        super();
        _parseClassName(aClass.getName());
    }

    /**
     * Method _extractClassName.
     *
     * @param className String
     * @return String
     */
    private String _extractClassName(String className) {

        int index = className.lastIndexOf('.');

        if (index != -1) {

            return className.substring(index + 1);
        } else {

            return className;
        }
    }

    /**
     * Method _extractEntireClassName.
     *
     * @param className String
     * @return String
     */
    private String _extractEntireClassName(String className) {

        return className;
    }

    /**
     * Method _extractEntirePackageName.
     *
     * @param className String
     * @return String
     */
    private String _extractEntirePackageName(String className) {

        int index = className.lastIndexOf('.');

        if (index != -1) {

            return className.substring(0, index);
        }
        return null;
    }

    /**
     * Method _extractPackageName.
     *
     * @param className String
     * @return String
     */
    private String _extractPackageName(String className) {


        int index = className.lastIndexOf('.');

        if (index != -1) {

            index = className.substring(0, index).lastIndexOf('.');
            return className.substring(++index);
        }

        return className;
    }

    /**
     * Method _parseClassName.
     *
     * @param className String
     */
    private void _parseClassName(String className) {

        this.entirePackageName = _extractEntirePackageName(className);
        this.packageName = _extractPackageName(className);
        this.entireClassName = _extractEntireClassName(className);
        this.className = _extractClassName(className);
    }

    /**
     * Returns the short class name
     *
     * @return String
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Returns the entire class name
     *
     * @return String
     */

    public String getEntireClassName() {
        return this.entireClassName;
    }

    /**
     * Returns the entire package name
     *
     * @return String
     */
    public String getEntirePackageName() {
        return this.entirePackageName;
    }

    /**
     * Returns the short package name
     *
     * @return String
     */
    public String getPackageName() {
        return this.packageName;
    }

}
