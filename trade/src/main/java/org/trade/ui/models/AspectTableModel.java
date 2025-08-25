package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.dao.Aspects;

import java.io.Serial;

/**
 *
 */
public abstract class AspectTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

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

}
