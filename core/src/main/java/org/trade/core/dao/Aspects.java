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
package org.trade.core.dao;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Aspects implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3388042483785305102L;
    private Long aspectsId;
    private boolean dirty = false;
    private List<Aspect> aspects = new ArrayList<>(0);

    public Aspects() {
    }

    /**
     * Constructor for Aspects.
     *
     * @param aspectsId Long
     */
    public Aspects(Long aspectsId) {

        this.aspectsId = aspectsId;
    }

    /**
     * Constructor for Aspects.
     *
     * @param aspects List<Aspect>
     */
    public Aspects(List<Aspect> aspects) {

        this.aspects = aspects;
    }

    /**
     * Constructor for Aspects.
     *
     * @param aspectsId Long
     * @param aspects    List<Aspect>
     */
    public Aspects(Long aspectsId, List<Aspect> aspects) {

        this.aspectsId = aspectsId;
        this.aspects = aspects;
    }

    /**
     * Method getAspectsId.
     *
     * @return Long
     */
    public Long getAspectsId() {

        return this.aspectsId;
    }

    /**
     * Method setAspectsId.
     *
     * @param aspectsId Long
     */
    public void setAspectsId(Long aspectsId) {

        this.aspectsId = aspectsId;
    }

    /**
     * Method add.
     *
     * @param aspect Aspect
     */
    public void add(Aspect aspect) {

        this.aspects.add(aspect);
    }

    /**
     * Method remove.
     *
     * @param aspect Aspect
     */
    public void remove(Aspect aspect) {

        this.aspects.remove(aspect);
    }

    /**
     * Method getAspects.
     *
     * @return List<Aspect>
     */
    public List<Aspect> getAspects() {

        return this.aspects;
    }

    /**
     * Method setDirty.
     *
     * @param dirty boolean
     */
    public void setDirty(boolean dirty) {

        this.dirty = dirty;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    public boolean isDirty() {

        for (Aspect aspect : this.getAspects()) {

            if (aspect.isDirty()) {

                return true;
            }
        }

        return this.dirty;
    }

    public void clear() {

        getAspects().clear();
    }
}
