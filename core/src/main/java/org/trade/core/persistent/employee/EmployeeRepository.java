package org.trade.core.persistent.employee;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface EmployeeRepository extends AspectRepository<Employee, Long> {

    /**
     * @param id Long
     * @return Optional<Employee>
     */
    Optional<Employee> findById(Long id);

    /**
     * @param email String
     * @return Optional<Employee>
     */
    Optional<Employee> findByEmail(String email);

    /**
     * @return List<Employee>
     */
    List<Employee> findAllByOrderByName();

    /**
     * @param name String
     * @return List<Employee>
     */
    List<Employee> findByNameContainingIgnoreCaseOrderByName(String name);

    /**
     * @param name String
     * @return Employee
     */
    Optional<Employee> findByName(String name);
}

