package org.trade.core.persistent.user;

import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserRecord(UserDTO user) {

    public static UserRecord from(User user) {

        return new UserRecord(
                JSONMapper.convertDTOToEntity(user, UserDTO.class)
        );
    }
}