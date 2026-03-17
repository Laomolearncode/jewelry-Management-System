export function getPermissions(user) {
  return Array.isArray(user?.permissions) ? user.permissions : []
}

export function hasPermission(user, permission) {
  if (!permission) {
    return true
  }
  return getPermissions(user).includes(permission)
}

export function hasAnyPermission(user, permissions = []) {
  if (!permissions.length) {
    return true
  }
  return permissions.some((permission) => hasPermission(user, permission))
}

export function hasAllPermissions(user, permissions = []) {
  if (!permissions.length) {
    return true
  }
  return permissions.every((permission) => hasPermission(user, permission))
}
