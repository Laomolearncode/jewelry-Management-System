import request from '../utils/request'

export function loginApi(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logoutApi() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function profileApi() {
  return request({
    url: '/auth/profile',
    method: 'get'
  })
}
