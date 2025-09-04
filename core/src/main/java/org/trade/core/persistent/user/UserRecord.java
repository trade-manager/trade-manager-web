package org.trade.core.persistent.user;

import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserRecord(Long id, String username,  String name,  String email, String role) {

    public static UserRecord from(User user) {

        UserDTO userDTO = JSONMapper.convertDTOToEntity(user, UserDTO.class);

        return new UserRecord(
                userDTO.getId(),
                userDTO.getUsername(),
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getRoleDTOs().getFirst().getName()
        );
    }
}