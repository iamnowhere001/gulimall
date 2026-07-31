import request from './request'

// 当前登录会员信息（待后端新增 JSON 接口）
export function getCurrentMember() {
  return request.get('/member/currentMember')
}
