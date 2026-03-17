import request from '../utils/request'

export function listMaterialsApi() {
  return request({ url: '/master/materials', method: 'get' })
}

export function createMaterialApi(data) {
  return request({ url: '/master/materials', method: 'post', data })
}

export function listCategoriesApi() {
  return request({ url: '/master/categories', method: 'get' })
}

export function createCategoryApi(data) {
  return request({ url: '/master/categories', method: 'post', data })
}

export function listSuppliersApi() {
  return request({ url: '/master/suppliers', method: 'get' })
}

export function createSupplierApi(data) {
  return request({ url: '/master/suppliers', method: 'post', data })
}

export function listCustomersApi() {
  return request({ url: '/master/customers', method: 'get' })
}

export function createCustomerApi(data) {
  return request({ url: '/master/customers', method: 'post', data })
}

export function listWarehousesApi() {
  return request({ url: '/master/warehouses', method: 'get' })
}

export function createWarehouseApi(data) {
  return request({ url: '/master/warehouses', method: 'post', data })
}

export function createProductApi(data) {
  return request({ url: '/master/products', method: 'post', data })
}

export function listProductsApi(params) {
  return request({ url: '/master/products', method: 'get', params })
}

export function listStocksApi(params) {
  return request({ url: '/master/stocks', method: 'get', params })
}

export function listLowStocksApi(params) {
  return request({ url: '/master/stocks/low', method: 'get', params })
}
