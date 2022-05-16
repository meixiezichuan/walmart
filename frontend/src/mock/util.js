const responseBody = {
  status: 0,
  detail: '',
  data: null,
}

export const builder = (data, status = 200, detail, headers = {}) => {
  responseBody.data = data
  if (detail !== undefined && detail !== null) {
    responseBody.detail = detail
  }
  if (status !== undefined && status !== 0) {
    responseBody.status = status
  }
  if (headers !== null && typeof headers === 'object' && Object.keys(headers).length > 0) {
    responseBody._headers = headers
  }
  return responseBody
}

export const getQueryParameters = (options) => {
  const url = options.url
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse('{"' + decodeURIComponent(search)
    .replace(/"/g, '\\"')
    .replace(/&/g, '","')
    .replace(/=/g, '":"') + '"}')
}

export const getBody = (options) => {
  return options.body && JSON.parse(options.body)
}
