package org.trade.core.persistent.dao;

import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.trade.core.dao.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface EmployeeRepository extends AspectRepository<Employee, Long>, EmployeeRepositoryCustom {


    @PreAuthorize("#employee?.user == null or #employee?.user?.name == authentication?.name")
    Employee save(@Param("employee") Employee employee);


    @PreAuthorize("@employeeRepository.findById(#id)?.user?.name == authentication?.name")
    void deleteById(@Param("id") Long id);


    @PreAuthorize("#employee?.user?.name == authentication?.name")
    void delete(@Param("employee") Employee employee);

}
// end::code[]
