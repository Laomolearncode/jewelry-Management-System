import request from '../utils/request'

export function createSalesApi(data) {
  return request({ url: '/sales', method: 'post', data })
}

export function listSalesApi(params) {
  return request({ url: '/sales', method: 'get', params })
}

export function getSalesApi(id) {
  return request({ url: `/sales/${id}`, method: 'get' })
}

export function approveSalesApi(id) {
  return request({ url: `/sales/${id}/approve`, method: 'post' })
}
