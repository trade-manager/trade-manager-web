package org.trade.core.persistent.dao.series.indicator.pivot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.util.MatrixFunctions;
import org.trade.core.util.Pair;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class PivotCalculator {

    private final static Logger _log = LoggerFactory.getLogger(PivotCalculator.class);

    private static int _polyOrder = 2; // default order
    private static double _minCorrelationCoeff = 0.6;

    public PivotCalculator(int polyOrder, double minCorrelationCoeff) {
        _polyOrder = polyOrder;
        _minCorrelationCoeff = minCorrelationCoeff;
    }

    /**
     * Method calculatePivot.
     *
     * @param pairs Hashtable<Long,Pair>
     * @return boolean
     */
    public boolean calculatePivot(List<Pair> pairs) {

        boolean isPivot = false;

        pairs.sort(Pair.X_VALUE_ASC);

        int size = pairs.size();
        if (size > 1) {
            Pair[] userData = pairs.toArray(new Pair[]{});
            double[] terms = MatrixFunctions.getCalculatedCoeffients(userData, _polyOrder);
            double correlationCoeff = MatrixFunctions.getCorrelationCoefficient(userData, terms);
            double standardError = MatrixFunctions.getStandardError(userData, terms);
            if (correlationCoeff > _minCorrelationCoeff) {
                isPivot = true;
                String output = MatrixFunctions.toPrint(_polyOrder, correlationCoeff, standardError, terms,
                        userData.length);
                _log.debug("Pivot Calc: {}", output);
                for (Pair pair : pairs) {
                    double y = MatrixFunctions.fx(pair.x, terms);
                    pair.y = y;
                }
            }
        }
        return isPivot;
    }
}
