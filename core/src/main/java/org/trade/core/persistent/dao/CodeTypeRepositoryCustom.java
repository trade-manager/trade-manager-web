package org.trade.core.persistent.dao;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CodeTypeRepositoryCustom {

    List<CodeType> findByNameAndType(String codeName, String codeType);

    List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName);
}
