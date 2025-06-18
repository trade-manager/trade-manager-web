package org.trade.core.broker.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Account;
import org.trade.core.xml.SaxMapper;
import org.trade.core.xml.TagTracker;
import org.trade.core.xml.XMLModelException;
import org.xml.sax.Attributes;

import java.io.CharArrayWriter;
import java.util.Stack;

public class TWSAccountAliasRequest extends SaxMapper {

    private final static Logger _log = LoggerFactory.getLogger(TWSAccountAliasRequest.class);

    private Aspects fTarget = null;
    private final Stack<Object> fStack = new Stack<>();

    public TWSAccountAliasRequest() throws XMLModelException {
        super();
    }

    public Object getMappedObject() {
        return fTarget;
    }

    public TagTracker createTagTrackerNetwork() {

        _log.trace("creating tag track network");

        // -- create root: /
        final TagTracker rootTagTracker = new TagTracker() {

            public void onDeactivate() {
                // The root will be deactivated when
                // parsing a new document begins.
                // clear the stack
                fStack.removeAllElements();

                // create the root "dir" object.
                fTarget = new Aspects();

                _log.trace("rootTagTracker onDeactivate");
                // push the root dir on the stack...
            }
        };

        final TagTracker accountAliasTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                _log.trace("accountAliasTracker onStart()");
                Account aspect = new Account();
                fTarget.add(aspect);
                fStack.push(aspect);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {
                // Clean up the directory stack...
                fStack.pop();
                _log.trace("accountAliasTracker onEnd() {}", contents.toString());
            }
        };

        rootTagTracker.track("ListOfAccountAliases/AccountAlias", accountAliasTracker);
        accountAliasTracker.track("AccountAlias", accountAliasTracker);

        final TagTracker accountTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Account temp = (Account) fStack.peek();
                temp.setAccountNumber(value);
                _log.trace("accountTracker: {}", value);
            }
        };

        accountAliasTracker.track("AccountAlias/account", accountTracker);
        accountTracker.track("account", accountTracker);

        final TagTracker aliasTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Account temp = (Account) fStack.peek();
                temp.setAlias(value);
                _log.trace("aliasTracker: {}", value);
            }
        };

        accountAliasTracker.track("AccountAlias/alias", aliasTracker);
        aliasTracker.track("alias", aliasTracker);
        return rootTagTracker;
    }
}
