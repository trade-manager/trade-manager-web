package org.trade.core.dao;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
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
     * @param aspects   List<Aspect>
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
