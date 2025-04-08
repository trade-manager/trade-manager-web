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
package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 * This class converts instances of
 * com.cbsinc.esc.devtoold.valuetype.base.CodeDecodeValueType to instances of
 * the object code they where set up with. The conversion will return the code
 * the valuetype is representing.
 *
 * @author Simon Allen
 * @version $Id: DecodeToObject.java,v 1.1 2001/11/06 16:51:55 simon Exp $
 */
public class DecodeToObject implements IJavaDynamicTypeConverter {

    public DecodeToObject() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     * @throws JavaTypeTranslatorException
     * @see IJavaDynamicTypeConverter#convert(Class<?>,
     * Object)
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Object rVal = null;

        if (valueToConvert instanceof Decode) {
            rVal = ((Decode) valueToConvert).getCode();
            rVal = JavaTypeTranslator.convert(targetType, rVal);
        } else {
            throw new JavaTypeTranslatorException(
                    "Value to convert must be a com.aceva.devtools.valuetype.base.Decode");
        }

        return (rVal);
    }

    /**
     * Method supportsConversion.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return boolean
     * @see IJavaDynamicTypeConverter#
     * supportsConversion (Class<?>, Object)
     */
    public boolean supportsConversion(Class<?> targetType, Object valueToConvert) {
        boolean rVal = false;

        if (valueToConvert instanceof Decode) {
            rVal = true;
        }

        return (rVal);
    }
}
