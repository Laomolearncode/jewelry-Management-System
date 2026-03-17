import request from '../utils/request'

export function dashboardApi(params) {
  return request({ url: '/reports/dashboard', method: 'get', params })
}

export function abcApi() {
  return request({ url: '/reports/abc', method: 'get' })
}

export function turnoverApi(params) {
  return request({ url: '/reports/turnover', method: 'get', params })
}
