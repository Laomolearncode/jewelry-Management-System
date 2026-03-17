import request from '../utils/request'

export function createCheckApi(data) {
  return request({ url: '/inventory/checks', method: 'post', data })
}

export function listChecksApi() {
  return request({ url: '/inventory/checks', method: 'get' })
}

export function approveCheckApi(id) {
  return request({ url: `/inventory/checks/${id}/approve`, method: 'post' })
}

export function traceApi(params) {
  return request({ url: '/inventory/trace', method: 'get', params })
}
