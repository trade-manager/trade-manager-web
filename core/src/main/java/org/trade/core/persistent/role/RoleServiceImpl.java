package org.trade.core.persistent.role;

import org.springframework.stereotype.Service;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(final RoleRepository roleRepository) {

        this.roleRepository = roleRepository;
    }

    public Role findRoleByName(String name) {

        return roleRepository.findByName(name);
    }

    public Role saveRole(Role role) {

        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {

        roleRepository.delete(role);
    }
}