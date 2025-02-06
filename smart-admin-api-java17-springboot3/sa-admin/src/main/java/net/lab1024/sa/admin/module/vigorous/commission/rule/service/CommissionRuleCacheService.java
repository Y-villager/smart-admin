package net.lab1024.sa.admin.module.vigorous.commission.rule.service;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.commission.rule.dao.CommissionRuleDao;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommissionRuleCacheService {
    @Resource
    private CommissionRuleDao commissionRuleDao;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    // 缓存
    private static final String COMMISSION_RULE_CACHE_PREFIX = "commission_rule:";

    // 查询并缓存提成规则
    public void cacheCommissionRules() {
        List<CommissionRuleVO> allCommissionRules = commissionRuleDao.findAll();

        // 将提成规则缓存到 Map 中，key 为一个组合的唯一标识符（转交状态+客户分组+提成类型）
        for (CommissionRuleVO commissionRule : allCommissionRules) {
            String key = generateCacheKey(commissionRule);
            redisTemplate.opsForHash().put(COMMISSION_RULE_CACHE_PREFIX, key, commissionRule);
        }
    }

    /**
     * 从 Redis 获取提成规则
     * @param transferStatus
     * @param customerGroup
     * @param commissionType
     * @return
     */
    public CommissionRuleVO getCommissionRuleFromCache(Integer transferStatus, Integer customerGroup, Integer commissionType) {
        // 唯一键
        String cacheKey = generateCacheKey(transferStatus, customerGroup, commissionType);
        // 从redis获取数据
        CommissionRuleVO commissionRule = (CommissionRuleVO) redisTemplate.opsForHash().get(COMMISSION_RULE_CACHE_PREFIX, cacheKey);
        if (commissionRule == null){
            CommissionRuleVO commissionRuleVO = new CommissionRuleVO();
            commissionRuleVO.setTransferStatus(transferStatus);
            commissionRuleVO.setCustomerGroup(customerGroup);
            commissionRuleVO.setCommissionType(commissionType);
            commissionRule = commissionRuleDao.queryCommissionRule(commissionRuleVO);
            redisTemplate.opsForHash().put(COMMISSION_RULE_CACHE_PREFIX, cacheKey, commissionRule);
        }
        return commissionRule;
    }

    // 生成缓存的唯一键：转交状态+客户分组+提成类型
    private String generateCacheKey(CommissionRuleVO commissionRule) {
        return commissionRule.getTransferStatus() + "_" + commissionRule.getCustomerGroup() + "_" + commissionRule.getCommissionType();
    }

    // 根据传入的条件生成唯一键
    private String generateCacheKey(Integer transferStatus, Integer customerGroup, Integer commissionType) {
        return transferStatus + "_" + customerGroup + "_" + commissionType;
    }
}
