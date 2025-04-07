// 扩展后的枚举定义
export const COMMISSION_FLAG_ENUM = {
    NONE: {
        value: 0,
        desc: '未生成',
        color: ''
    },
    CREATED: {
        value: 1,
        desc: '已生成',
        color: 'success'
    },
    OVERRIDABLE: {
        value: 2,
        desc: '修改中',
        color: 'processing'
    }
}

export default {
    COMMISSION_FLAG_ENUM
}