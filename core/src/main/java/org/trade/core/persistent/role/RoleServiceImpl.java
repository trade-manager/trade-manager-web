package org.trade.core.persistent.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.persistent.domain.DomainNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final static Logger _log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    public RoleServiceImpl(final RoleRepository roleRepository) {

        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {

        return roleRepository.findAllByOrderByName();
    }

    public Role findByName(String name) {

        return roleRepository.findByName(name).orElse(null);
    }

    public Role validateAndGet(String name) {

        return roleRepository.findByName(name).orElseThrow(() -> new DomainNotFoundException(String.format("Role with name %s not found", name)));
    }

    public Role save(Role role) {

        return roleRepository.save(role);
    }

    public void delete(Role role) {

        if (null == role) {

            return;
        }

        roleRepository.delete(role);
    }

    @Transactional
    public RoleRecord findRoleRecordByName(String name) {

        Optional<Role> role = roleRepository.findByName(name);
        return role.map(this::convertToRecord).orElse(null);
    }

    @Transactional
    public List<RoleRecord> findAllTopLevelRoleRecords() {

        // Use a query to get only top-level managers
        List<Role> roles = roleRepository.findByContainedRoleIsNull();
        return roles.stream()
                .map(this::convertToRecord)
                .collect(Collectors.toList());
    }

    private RoleRecord convertToRecord(Role role) {

        role.getContainRoles().size();
        RoleRecord roleRecord = RoleRecord.from(role, true);

        // Recursively convert subordinates, or fetch lazily if needed.
        if (role.getContainRoles() != null && !role.getContainRoles().isEmpty()) {

            for (Role containRole : role.getContainRoles()) {

                convertToRecord(containRole);
            }
        }
        return roleRecord;
    }
}