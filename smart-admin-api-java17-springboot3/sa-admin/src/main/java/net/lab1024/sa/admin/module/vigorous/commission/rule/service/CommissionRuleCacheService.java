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
     * @param commissionType    提成类别（1业务提成 2管理提成
     * @param transferStatus    转交情况（0未转交 1转交
     * @param isDeclared    客户报关（0客户未报关 1客户报关
     * @return
     */
    public CommissionRuleVO getCommissionRuleFromCache(Integer commissionType,Integer transferStatus, Integer isDeclared) {
        if (commissionType == null) {
            throw new RuntimeException("commissionType is null");
        }
        if (transferStatus == null) {
            throw new RuntimeException("transferStatus is null");
        }
        if (isDeclared == null) {
            throw new RuntimeException("isDeclared is null");
        }
        if (transferStatus > 0){ // 0为自主开发，其他都为转交
            transferStatus = 1;
        }
        // 唯一键
        String cacheKey = generateCacheKey(commissionType, transferStatus, isDeclared);
        // 从redis获取数据
        CommissionRuleVO commissionRule = (CommissionRuleVO) redisTemplate.opsForHash().get(COMMISSION_RULE_CACHE_PREFIX, cacheKey);
        if (commissionRule == null){
            CommissionRuleVO commissionRuleVO = new CommissionRuleVO(commissionType, transferStatus, isDeclared);
            commissionRule = commissionRuleDao.queryCommissionRule(commissionRuleVO);
            redisTemplate.opsForHash().put(COMMISSION_RULE_CACHE_PREFIX, cacheKey, commissionRule);
        }
        return commissionRule;
    }

    // 生成缓存的唯一键：转交状态+客户分组+提成类型
    private String generateCacheKey(CommissionRuleVO commissionRule) {
        return generateCacheKey(commissionRule.getCommissionType(), commissionRule.getTransferStatus(), commissionRule.getIsCustomsDeclaration());
    }

    // 根据传入的条件生成唯一键
    private String generateCacheKey(Integer commissionType, Integer transferStatus, Integer isDeclared) {
        return commissionType + "_" + transferStatus + "_" + isDeclared;
    }
}
