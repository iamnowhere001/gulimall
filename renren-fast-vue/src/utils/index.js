import Vue from 'vue'
import store from '@/store'

/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : (Math.random() * 4 | 0x8)).toString(16)
  })
}

/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]]['children']) {
        temp[data[k][pid]]['children'] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]]['children'].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete('token')
  store.commit('resetStore')
  const routerModule = require('@/router')
  const router = routerModule.default || routerModule
  router.options.isAddDynamicMenuRoutes = false
}

/**
 * 日期格式化
 * 将 2026-07-29T01:00:05.000+0000 / 2026-07-29 01:00:05 统一展示为 2026-07-29 01:00
 * 说明：直接按字符串截取年月日时分，避免 UTC(+0000) 被转换为本地时区导致时间偏移
 * @param {*} value 时间值（ISO 字符串、时间戳、Date 对象）
 */
export function dateFormat (value) {
  if (value === null || value === undefined || value === '') {
    return ''
  }
  // ISO 字符串：2026-07-29T01:00:05.000+0000 或 2026-07-29 01:00:05
  if (typeof value === 'string') {
    // 纯日期 yyyy-MM-dd（如生日），原样返回，避免被当作 UTC 偏移
    const d = value.match(/^(\d{4})-(\d{2})-(\d{2})$/)
    if (d) {
      return `${d[1]}-${d[2]}-${d[3]}`
    }
    const m = value.match(/^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})/)
    if (m) {
      return `${m[1]}-${m[2]}-${m[3]} ${m[4]}:${m[5]}`
    }
  }
  // 时间戳（数字或纯数字字符串）
  const num = typeof value === 'number' ? value : (/^\d+$/.test(value) ? Number(value) : NaN)
  const date = new Date(isNaN(num) ? value : num)
  if (!isNaN(date.getTime())) {
    const pad = n => (n < 10 ? '0' + n : '' + n)
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
  }
  return value
}
