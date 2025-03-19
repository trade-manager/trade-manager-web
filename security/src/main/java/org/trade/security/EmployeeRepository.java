package org.trade.security;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@PreAuthorize("hasRole('ROLE_MANAGER')")
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    @Override
    @PreAuthorize("#employee?.manager == null or #employee?.manager?.name == authentication?.name")
    Employee save(@Param("employee") Employee employee);

    @Override
    @PreAuthorize("@employeeRepository.findById(#id)?.manager?.name == authentication?.name")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("#employee?.manager?.name == authentication?.name")
    void delete(@Param("employee") Employee employee);

}
// end::code[]
