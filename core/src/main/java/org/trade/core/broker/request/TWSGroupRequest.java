package org.trade.core.broker.request;

import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.PortfolioAccount;
import org.trade.core.xml.SaxMapper;
import org.trade.core.xml.TagTracker;
import org.trade.core.xml.XMLModelException;
import org.xml.sax.Attributes;

import java.io.CharArrayWriter;
import java.util.Stack;

public class TWSGroupRequest extends SaxMapper {

    private Aspects fTarget = null;
    private final Stack<Object> fStack = new Stack<>();

    public TWSGroupRequest() throws XMLModelException {
        super();
    }

    public Object getMappedObject() {
        return fTarget;
    }

    public TagTracker createTagTrackerNetwork() {

        // -- create root: /
        final TagTracker rootTagTracker = new TagTracker() {

            public void onDeactivate() {
                /*
                 * The root will be deactivated when parsing a new document
                 * begins. clear the stack
                 */
                fStack.removeAllElements();

                // create the root "dir" object.
                fTarget = new Aspects();
            }
        };

        final TagTracker groupsTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                Portfolio aspect = new Portfolio();
                fTarget.add(aspect);
                fStack.push(aspect);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // Clean up the directory stack...
                fStack.pop();
            }
        };

        rootTagTracker.track("ListOfGroups/Group", groupsTracker);
        groupsTracker.track("Group", groupsTracker);

        final TagTracker nameTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Portfolio temp = (Portfolio) fStack.peek();
                temp.setName(value);
            }
        };

        groupsTracker.track("Group/name", nameTracker);
        nameTracker.track("name", nameTracker);

        final TagTracker methodTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Portfolio temp = (Portfolio) fStack.peek();
                temp.setAllocationMethod(value);
            }
        };

        groupsTracker.track("Group/defaultMethod", methodTracker);
        methodTracker.track("defaultMethod", methodTracker);

        final TagTracker listOfAcctsTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                final Portfolio temp = (Portfolio) fStack.peek();
                fStack.push(temp);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // Clean up the directory stack...
                fStack.pop();
            }
        };

        groupsTracker.track("Group/ListOfAccts", listOfAcctsTracker);
        listOfAcctsTracker.track("ListOfAccts", listOfAcctsTracker);

        final TagTracker accountTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Portfolio portfolio = (Portfolio) fStack.peek();
                PortfolioAccount temp = new PortfolioAccount(portfolio, new Account());
                portfolio.getPortfolioAccounts().add(temp);
                temp.getAccount().setAccountNumber(value);
            }
        };

        listOfAcctsTracker.track("ListOfAccts/String", accountTracker);
        accountTracker.track("String", accountTracker);
        return rootTagTracker;
    }
}
