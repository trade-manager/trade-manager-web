package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.Aspects;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class AspectTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    private Aspect value;

    public AspectTableModel() {
    }

    public AspectTableModel(String[] columnHeaderToolTip) {
        super(columnHeaderToolTip);
    }

    /**
     * Method getData.
     *
     * @return Aspects
     */
    public abstract Aspects getData();

    /**
     * Method setData.
     *
     * @param data Aspects
     */
    public abstract void setData(Aspects data) throws Exception;

    public void setSelectRowValue(Aspect value) {

        this.value = value;
    }

    public Aspect getSelectRowValue() {

        return this.value;
    }
}
