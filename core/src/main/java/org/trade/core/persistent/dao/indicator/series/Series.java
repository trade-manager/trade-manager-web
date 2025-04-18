package org.trade.core.persistent.dao.indicator.series;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public abstract class Series implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = -6906561437538683581L;
    private Comparable key;
    private String description;
    private transient EventListenerList listeners;
    private transient PropertyChangeSupport propertyChangeSupport;
    private transient VetoableChangeSupport vetoableChangeSupport;
    private boolean notify;

    protected Series(Comparable key) {
        this(key, null);
    }

    protected Series(Comparable key, String description) {
        //  Args.nullNotPermitted(key, "key");
        this.key = key;
        this.description = description;
        this.listeners = new EventListenerList();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.vetoableChangeSupport = new VetoableChangeSupport(this);
        this.notify = true;
    }

    public Comparable getKey() {
        return this.key;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setKey(Comparable key) {
        //  Args.nullNotPermitted(key, "key");
        Comparable old = this.key;

        try {
            this.vetoableChangeSupport.fireVetoableChange("Key", old, key);
            this.key = key;
            this.propertyChangeSupport.firePropertyChange("Key", old, key);
        } catch (PropertyVetoException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        this.propertyChangeSupport.firePropertyChange("Description", old, description);
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        if (this.notify != notify) {
            this.notify = notify;
            this.fireSeriesChanged();
        }

    }

    public boolean isEmpty() {
        return this.getItemCount() == 0;
    }

    public abstract int getItemCount();

    public Object clone() throws CloneNotSupportedException {
        Series clone = (Series) super.clone();
        clone.listeners = new EventListenerList();
        clone.propertyChangeSupport = new PropertyChangeSupport(clone);
        clone.vetoableChangeSupport = new VetoableChangeSupport(clone);
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Series)) {
            return false;
        } else {
            Series that = (Series) obj;
            if (!Objects.equals(this.key, that.key)) {
                return false;
            } else if (!Objects.equals(this.description, that.description)) {
                return false;
            } else {
                return that.canEqual(this);
            }
        }
    }

    public boolean canEqual(Object other) {
        return other instanceof Series;
    }

    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.key);
        hash = 53 * hash + Objects.hashCode(this.description);
        return hash;
    }

    public void addChangeListener(SeriesChangeListener listener) {
        this.listeners.add(SeriesChangeListener.class, listener);
    }

    public void removeChangeListener(SeriesChangeListener listener) {
        this.listeners.remove(SeriesChangeListener.class, listener);
    }

    public void fireSeriesChanged() {
        if (this.notify) {
            this.notifyListeners(new SeriesChangeEvent(this));
        }

    }

    protected void notifyListeners(SeriesChangeEvent event) {
        Object[] listenerList = this.listeners.getListenerList();

        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == SeriesChangeListener.class) {
                ((SeriesChangeListener) listenerList[i + 1]).seriesChanged(event);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        this.vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        this.vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    protected void fireVetoableChange(String property, Object oldValue, Object newValue) throws PropertyVetoException {
        this.vetoableChangeSupport.fireVetoableChange(property, oldValue, newValue);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listeners = new EventListenerList();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.vetoableChangeSupport = new VetoableChangeSupport(this);
    }
}

