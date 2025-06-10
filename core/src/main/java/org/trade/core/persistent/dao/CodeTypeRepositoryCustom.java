package org.trade.core.persistent.dao;


public interface CodeTypeRepositoryCustom {

    CodeType findByNameAndType(String codeName, String codeType);

    CodeValue findByAttributeName(String codeTypeName, String codeAttributeName);
}
