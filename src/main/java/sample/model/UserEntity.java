package sample.model;

/**
 * 
 * Interface resolving that an entity belongs to an user (usually current user)
 * It is used mainly for authorization purpose
 *
 */
public interface UserEntity {
	boolean isUserEntity(Long userId);
}
