package org.trade.web;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;


@PreAuthorize("hasRole('ROLE_MANAGER')")
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {


    @PreAuthorize("#employee?.user == null or #employee?.user?.name == authentication?.name")
    Employee save(@Param("employee") Employee employee);


    @PreAuthorize("@employeeRepository.findById(#id)?.user?.name == authentication?.name")
    void deleteById(@Param("id") Long id);


    @PreAuthorize("#employee?.user?.name == authentication?.name")
    void delete(@Param("employee") Employee employee);

}
// end::code[]
