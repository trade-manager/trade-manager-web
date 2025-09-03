package org.trade.core.persistent.role;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<RoleDTO> findAllTopLevelEmployees() {
        // Use a query to get only top-level managers
        List<Role> roles = roleRepository.findByContainedRoleIsNull();
        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        if (role.getContainedRole() != null) {
            dto.setContainedRoleId(role.getContainedRole().getId());
        }

        // Recursively convert subordinates, or fetch lazily if needed.
        if (role.getContainRoles() != null && !role.getContainRoles().isEmpty()) {

            dto.setContainRoles(role.getContainRoles().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}