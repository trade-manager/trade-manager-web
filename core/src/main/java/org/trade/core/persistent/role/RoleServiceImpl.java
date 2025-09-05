package org.trade.core.persistent.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.persistent.domain.DomainNotFoundException;
import org.trade.core.util.JSONMapper;

import java.util.List;
import java.util.Optional;
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

        return roleRepository.findByName(name).orElse(null);
    }

    public Role validateAndGetDomain(String name) {

        return roleRepository.findByName(name).orElseThrow(() -> new DomainNotFoundException(String.format("Role with name %s not found", name)));
    }

    public Role saveRole(Role role) {

        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {

        if (null == role) {

            return;
        }

        roleRepository.delete(role);
    }

    @Transactional
    public RoleDTO findRoleDTOByName(String name) {

        Optional<Role> role = roleRepository.findByName(name);
        return role.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public List<RoleDTO> findAllTopLevelRoleDTOs() {

        // Use a query to get only top-level managers
        List<Role> roles = roleRepository.findByContainedRoleIsNull();
        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToDTO(Role role) {

        role.getContainRoles().size();
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        if (role.getContainedRole() != null) {
            dto.setContainedRole(JSONMapper.convertEntityToDTO(role.getContainedRole(), RoleDTO.class));
        }

        // Recursively convert subordinates, or fetch lazily if needed.
        if (role.getContainRoles() != null && !role.getContainRoles().isEmpty()) {

            dto.setContainRoleDTOs(role.getContainRoles().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}