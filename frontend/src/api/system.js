import request from '../utils/request'

export function listUsersApi() {
  return request({ url: '/system/users', method: 'get' })
}

export function createUserApi(data) {
  return request({ url: '/system/users', method: 'post', data })
}

export function updateUserStatusApi(id, data) {
  return request({ url: `/system/users/${id}/status`, method: 'put', data })
}

export function listRolesApi() {
  return request({ url: '/system/roles', method: 'get' })
}

export function listPermissionsApi() {
  return request({ url: '/system/permissions', method: 'get' })
}

export function listLogsApi() {
  return request({ url: '/system/logs', method: 'get' })
}
