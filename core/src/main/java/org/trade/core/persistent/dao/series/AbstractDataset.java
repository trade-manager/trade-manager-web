package org.trade.core.persistent.dao.series;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;


public abstract class AbstractDataset implements Dataset, Cloneable, Serializable, ObjectInputValidation {
    @Serial
    private static final long serialVersionUID = 1918768939869230744L;
    private DatasetGroup group = new DatasetGroup();
    private transient EventListenerList listenerList = new EventListenerList();
    private boolean notify = true;

    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.group);
        hash = 29 * hash + (this.notify ? 1 : 0);
        return hash;
    }

    public boolean canEqual(Object other) {
        return other instanceof AbstractDataset;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractDataset that)) {
            return false;
        } else {
            if (this.notify != that.notify) {
                return false;
            } else if (!Objects.equals(this.group, that.group)) {
                return false;
            } else {
                return that.canEqual(this);
            }
        }
    }

    protected AbstractDataset() {
    }

    public DatasetGroup getGroup() {
        return this.group;
    }

    public void setGroup(DatasetGroup group) {
        // Args.nullNotPermitted(group, "group");
        this.group = group;
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            this.fireDatasetChanged();
        }

    }

    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
    }

    public boolean hasListener(EventListener listener) {
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    protected void fireDatasetChanged() {
        if (this.notify) {
            this.notifyListeners(new DatasetChangeEvent(this, this));
        }

    }

    protected void notifyListeners(DatasetChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DatasetChangeListener.class) {
                ((DatasetChangeListener) listeners[i + 1]).datasetChanged(event);
            }
        }

    }

    public Object clone() throws CloneNotSupportedException {
        AbstractDataset clone = (AbstractDataset) super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);
    }

    public void validateObject() {
        this.fireDatasetChanged();
    }
}

