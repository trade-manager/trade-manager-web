package org.trade.core.persistent.dao;


import java.util.List;

public interface CodeTypeRepositoryCustom {

    List<CodeType> findByNameAndType(String codeName, String codeType);

    List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName);
}
