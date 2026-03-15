import { ref } from 'vue'

/**
 * 发送验证码 Hook
 * @returns {Object} 包含 disabled, text, sendCode 的响应式对象和方法
 */
export function useSendVerifyCode() {
  const disabled = ref(false)
  const text = ref("获取验证码")
  let timer = null

  /**
   * 发送验证码
   * @param {number} countdown - 倒计时秒数，默认60秒
   */
  const sendCode = (countdown = 60) => {
    if (disabled.value) return
    
    disabled.value = true
    let remainingTime = countdown
    text.value = `剩余 ${remainingTime}s`
    
    timer = setInterval(() => {
      remainingTime--
      
      if (remainingTime <= 0) {
        clearInterval(timer)
        timer = null
        disabled.value = false
        text.value = "重新获取"
      } else {
        text.value = `剩余 ${remainingTime}s`
      }
    }, 1000)
  }

  /**
   * 重置状态
   */
  const reset = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    disabled.value = false
    text.value = "获取验证码"
  }

  return {
    disabled,
    text,
    sendCode,
    reset
  }
}

export default useSendVerifyCode

