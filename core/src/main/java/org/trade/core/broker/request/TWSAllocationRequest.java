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

public class TWSAllocationRequest extends SaxMapper {

    private Aspects fTarget = null;
    private final Stack<Object> fStack = new Stack<>();

    public TWSAllocationRequest() throws XMLModelException {
        super();
    }

    public Object getMappedObject() {
        return fTarget;
    }

    public TagTracker createTagTrackerNetwork() {

        // -- create root: /
        final TagTracker rootTagTracker = new TagTracker() {

            public void onDeactivate() {
                // The root will be deactivated when
                // parsing a new document begins.
                // clear the stack
                fStack.removeAllElements();

                // create the root "dir" object.
                fTarget = new Aspects();
                // push the root dir on the stack...
            }
        };

        final TagTracker allocationProfileTracker = new TagTracker() {

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

        rootTagTracker.track("ListOfAllocationProfiles/AllocationProfile", allocationProfileTracker);
        allocationProfileTracker.track("AllocationProfile", allocationProfileTracker);

        final TagTracker nameTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                super.onStart(namespaceURI, localName, qName, attr);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Portfolio temp = (Portfolio) fStack.peek();
                temp.setName(value);
            }
        };

        allocationProfileTracker.track("AllocationProfile/name", nameTracker);
        nameTracker.track("name", nameTracker);

        final TagTracker typeTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                super.onStart(namespaceURI, localName, qName, attr);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                final Portfolio temp = (Portfolio) fStack.peek();
                temp.setAllocationMethod(value);
            }
        };

        allocationProfileTracker.track("AllocationProfile/type", typeTracker);
        typeTracker.track("type", typeTracker);

        final TagTracker listOfAllocationsTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                final Portfolio temp = (Portfolio) fStack.peek();
                fStack.push(temp);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // Clean up the directory stack...
                fStack.pop();
            }
        };

        allocationProfileTracker.track("AllocationProfile/ListOfAllocations", listOfAllocationsTracker);
        listOfAllocationsTracker.track("ListOfAllocations", listOfAllocationsTracker);

        final TagTracker allocationTracker = new TagTracker() {

            public void onStart(String namespaceURI, String localName, String qName, Attributes attr) {

                final Portfolio portfolio = (Portfolio) fStack.peek();
                PortfolioAccount temp = new PortfolioAccount(portfolio, new Account());
                portfolio.getPortfolioAccounts().add(temp);
                fStack.push(temp);
            }

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // Clean up the directory stack...
                fStack.pop();
            }
        };

        allocationProfileTracker.track("ListOfAllocations/Allocation", allocationTracker);
        allocationTracker.track("Allocation", allocationTracker);

        final TagTracker acctTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                final String value = contents.toString();
                PortfolioAccount temp = (PortfolioAccount) fStack.peek();
                temp.getAccount().setAccountNumber(value);
            }
        };

        allocationTracker.track("Allocation/acct", acctTracker);
        acctTracker.track("acct", acctTracker);

        final TagTracker amountTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // final String value = new String(contents.toString());
                // final PortfolioAccount aspect = (PortfolioAccount) m_stack
                // .peek();
                // aspect.setAmount(new BigDecimal(value));
            }
        };

        allocationTracker.track("Allocation/amount", amountTracker);
        amountTracker.track("amount", amountTracker);

        final TagTracker posEffTracker = new TagTracker() {

            public void onEnd(String namespaceURI, String localName, String qName, CharArrayWriter contents) {

                // final String value = new String(contents.toString());
                // PortfolioAccount temp = (PortfolioAccount) m_stack.peek();
                // .peek();
                // temp.setPosEff(value);
            }
        };
        allocationTracker.track("Allocation/posEff", posEffTracker);
        posEffTracker.track("posEff", posEffTracker);
        return rootTagTracker;
    }
}
