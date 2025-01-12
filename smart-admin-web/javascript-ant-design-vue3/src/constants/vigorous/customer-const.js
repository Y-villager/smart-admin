/**
 * 顾客 枚举
 *
 * @Author:    yxz
 * @Date:      2024-12-12 14:51:07
 * @Copyright  (c)2024 yxz
 */

export const CUSTOMER_GROUP_ENUM= {
    DOMESTIC: {
        value: 1,
        desc: '内贸客户',
    },
    FOREIGN: {
        value: 2,
        desc: '外贸客户',
    },
};

export const TRANSFER_STATUS_ENUM= {
    INDEPENDENTLY: {
        value: 0,
        desc: '自主开发',
    },
    RESIGNATION_TRANSFER: {
        value: 1,
        desc: '离职转交',
    },
    INTERNAL_HANDOVER:{
        value: 2,
        desc: '内勤转交',
    },
    COMPANY_TRANSFER:{
        value: 3,
        desc: '公司转交',
    },
    COMPANY_CUSTOMER:{
        value: 4,
        desc: '公司客户',
    },
    OTHER:{
        value: 5,
        desc: '其他转交',
    },
};

export default {
    CUSTOMER_GROUP_ENUM,
    TRANSFER_STATUS_ENUM
}