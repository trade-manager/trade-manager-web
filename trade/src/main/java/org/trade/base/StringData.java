package org.trade.base;

import javax.swing.tree.TreePath;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serial;
import java.io.Serializable;
import java.io.StringReader;

/**
 * @author Simon Allen
 * @version $Id: Aceva-Style.jin 1.3 2001/03/04 21:04:39Z Simon.Allen dev $
 */
public class StringData implements Transferable, ClipboardOwner, Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 553277135583406304L;

    private static final int STRING = 0;
    private static final int PLAIN_TEXT = 1;
    public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

    private static final DataFlavor[] flavors = {DataFlavor.stringFlavor, DataFlavor.getTextPlainUnicodeFlavor(),
            TREEPATH_FLAVOR // deprecated
    };

    private final String data;
    private TreePath path = null;

    /**
     * Creates a Transferable capable of transferring the specified String.
     *
     * @param data String
     */
    public StringData(String data) {
        this.data = data;
    }

    /**
     * Method setPath.
     *
     * @param path TreePath
     */
    public void setPath(TreePath path) {
        this.path = path;
    }

    /**
     * Returns an array of flavors in which this Transferable can provide the
     * data. <code>DataFlavor.stringFlavor</code> is properly supported. Support
     * for <code>DataFlavor.plainTextFlavor</code> is <b>deprecated</b>.
     *
     * @return an array of length two, whose elements are <code>DataFlavor.
     * stringFlavor</code> and <code>DataFlavor.plainTextFlavor</code>.
     * * @see
     * java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {

        // returning flavors itself would allow client code to modify
        // our internal behavior
        return flavors.clone();
    }

    /**
     * Returns whether the requested flavor is supported by this Transferable.
     *
     * @param flavor the requested flavor for the data
     * @return true if flavor is equal to <code>DataFlavor.stringFlavor</code>
     * or <code>DataFlavor.plainTextFlavor</code>, false otherwise.
     * * @see java
     * .awt.datatransfer.Transferable#isDataFlavorSupported(DataFlavor)
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor flavor2 : flavors) {
            if (flavor2.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the Transferable's data in the requested DataFlavor if possible.
     * If the desired flavor is <code>DataFlavor.stringFlavor</code>, or an
     * equivalent flavor, the String representing the selection is returned. If
     * the desired flavor is </code>DataFlavor.plainTextFlavor </code>, or an
     * equivalent flavor, a Reader is returned. <b>Note:<b> The behavior of this
     * method for </code>DataFlavor.plainTextFlavor</code> and equivalent
     * DataFlavors is inconsistent with the definition of
     * <code>DataFlavor.plainTextFlavor</code>.
     *
     * @param flavor the requested flavor for the data
     * @return the data in the requested flavor, as outlined above. * @throws
     * UnsupportedFlavorException if the requested data flavor is not
     * equivalent to either <code>DataFlavor.stringFlavor</code> or
     * <code>DataFlavor.plainTextFlavor</code>. * @throws IOException
     * @see java.io.Reader
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(flavors[STRING])) {
            return new StringData(data);
        } else if (flavor.equals(flavors[PLAIN_TEXT])) {
            return new StringReader(data);
        } else if (flavor.isMimeTypeEqual(TREEPATH_FLAVOR.getMimeType())) // DataFlavor.javaJVMLocalObjectMimeType))
        {
            return path;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * Method lostOwnership.
     *
     * @param clipboard Clipboard
     * @param contents  Transferable
     * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(Clipboard,
     * Transferable)
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return data;
    }
}
