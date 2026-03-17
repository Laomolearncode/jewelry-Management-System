import request from '../utils/request'

export function createPurchaseApi(data) {
  return request({ url: '/purchases', method: 'post', data })
}

export function listPurchasesApi(params) {
  return request({ url: '/purchases', method: 'get', params })
}

export function getPurchaseApi(id) {
  return request({ url: `/purchases/${id}`, method: 'get' })
}

export function approvePurchaseApi(id) {
  return request({ url: `/purchases/${id}/approve`, method: 'post' })
}

export function stockInPurchaseApi(id) {
  return request({ url: `/purchases/${id}/stock-in`, method: 'post' })
}
