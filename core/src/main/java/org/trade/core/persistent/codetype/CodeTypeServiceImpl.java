package org.trade.core.persistent.codetype;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class CodeTypeServiceImpl implements CodeTypeService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CodeTypeRepository codeTypeRepository;

    public CodeTypeServiceImpl(final CodeTypeRepository codeTypeRepository) {

        this.codeTypeRepository = codeTypeRepository;
    }

    public CodeType findByName(String name) {

        return codeTypeRepository.findByName(name).orElse(null);
    }

    public CodeType validateAndGet(String name) {

        return codeTypeRepository.findByName(name).orElseThrow(() -> new CodeTypeNotFoundException(String.format("CodeType with name %s not found", name)));
    }

    public CodeType findByNameAndType(String name, String type) {

        List<CodeType> codeTypes = codeTypeRepository.findByNameAndType(name, type);
        return codeTypes.isEmpty() ? null : codeTypes.getFirst();

    }

    /**
     * Method findByAttributeName.
     *
     * @param codeTypeName      String
     * @param codeAttributeName String
     * @return List<CodeValue>
     */
    public List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CodeValue> query = builder.createQuery(CodeValue.class);
        Root<CodeValue> from = query.from(CodeValue.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != codeAttributeName) {

            Join<CodeValue, CodeAttribute> codeAttribute = from.join("codeAttribute");
            Predicate predicate = builder.equal(codeAttribute.get("name"), codeAttributeName);
            predicates.add(predicate);
            Join<CodeAttribute, CodeType> codeType = codeAttribute.join("codeType");
            Predicate predicate1 = builder.equal(codeType.get("name"), codeTypeName);
            predicates.add(predicate1);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<CodeValue> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public CodeType save(CodeType codeType) {

        return codeTypeRepository.save(codeType);
    }

    public void delete(CodeType codeType) {

        if (null == codeType) {

            return;
        }

        codeTypeRepository.delete(codeType);
    }
}