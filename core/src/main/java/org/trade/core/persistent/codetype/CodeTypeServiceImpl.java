package org.trade.core.persistent.codetype;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private final CodeValueRepository codeValueRepository;

    public CodeTypeServiceImpl(final CodeTypeRepository codeTypeRepository, final CodeValueRepository codeValueRepository) {

        this.codeTypeRepository = codeTypeRepository;
        this.codeValueRepository = codeValueRepository;
    }


    public CodeType findByName(String name) {

        return codeTypeRepository.findByName(name).orElse(null);
    }

    @Transactional
    public List<CodeType> findAll() {

        List<CodeType> codeTypes = codeTypeRepository.findAll();

        for (CodeType codeType : codeTypes) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttribute.getCodeValues().size();
            }
        }

        return codeTypes;
    }

    @Transactional
    public List<CodeType> findByCategory(String category) {

        List<CodeType> codeTypes = codeTypeRepository.findByCategory(category);

        for (CodeType codeType : codeTypes) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttribute.getCodeValues().size();
            }
        }

        return codeTypes;
    }

    @Cacheable(value = "codeTypes", key = "#type")
    @Transactional
    public List<CodeType> findByType(String type) {

        List<CodeType> codeTypes = codeTypeRepository.findByType(type);

        for (CodeType codeType : codeTypes) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttribute.getCodeValues().size();
            }
        }

        return codeTypes;
    }

    public CodeType validateAndGet(String name) {

        return codeTypeRepository.findByName(name).orElseThrow(() -> new CodeTypeNotFoundException(String.format("CodeType with name %s not found", name)));
    }

    public CodeType findByNameAndType(String name, String type) {

        List<CodeType> codeTypes = codeTypeRepository.findByNameAndType(name, type);
        return codeTypes.isEmpty() ? null : codeTypes.getFirst();
    }

    @Transactional
    public CodeType findByNameAndTypeAndCategory(String name, String type, String category) {

        List<CodeType> codeTypes = codeTypeRepository.findByNameAndTypeAndCategory(name, type, category);

        for (CodeType codeType : codeTypes) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttribute.getCodeValues().size();
            }
        }

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

    @CacheEvict(value = "codeTypes", allEntries = true)
    public CodeType save(CodeType codeType) {

        return codeTypeRepository.save(codeType);
    }

    @CacheEvict(value = "codeTypes", allEntries = true)
    public void delete(CodeType codeType) {

        if (null == codeType) {

            return;
        }

        codeTypeRepository.delete(codeType);
    }

    @Cacheable(value = "CodeValue", key = "#type")
    @Transactional
    public List<CodeValue> findByTypeSortedByCodeTypeAndCodeValue(String type) {

        List<CodeValue> codeTypes = codeValueRepository.findByTypeSortedByCodeTypeAndCodeValue(type);

        return codeTypes;
    }
}