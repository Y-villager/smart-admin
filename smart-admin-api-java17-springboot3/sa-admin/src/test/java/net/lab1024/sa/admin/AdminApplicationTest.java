package net.lab1024.sa.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.lab1024.sa.admin.module.vigorous.customer.dao.CustomerDao;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminApplicationTest {

    @Qualifier("customerDao")
    @Autowired
    private CustomerDao customerDao;

    @BeforeEach
    public void before() {
        System.out.println("----------------------- 测试开始 -----------------------");

    }

    @AfterEach
    public void after() {
        System.out.println("----------------------- 测试结束 -----------------------");
    }


    @Test
    public void adjustFirstOrderDate(){
        QueryWrapper<CustomerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("first_order_date");
        List<CustomerEntity> customerList = customerDao.selectList(queryWrapper);

        List<CustomerEntity> adjustList = new ArrayList<>();

        LocalDate rank1ResetDate = LocalDate.of(2025, 1, 1);
        LocalDate rank2ResetDate = LocalDate.of(2023, 1, 1);
        LocalDate rank3ResetDate = LocalDate.of(2021, 1, 1);

        // 遍历客户列表，调整首单时间
        for (CustomerEntity customer : customerList) {
            LocalDate firstOrderDate = customer.getFirstOrderDate();
            if (firstOrderDate == null) {
                continue; // 如果没有首单日期，则跳过
            }

            // 用于存储调整后的日期
            LocalDate adjustedDate = null;

            // 1. 对于 2022.1.1 ~ 2024.12.31 之间的日期
            if (!firstOrderDate.isBefore(LocalDate.of(2022, 1, 1)) && firstOrderDate.isBefore(LocalDate.of(2025, 1, 1))) {
                adjustedDate = rank1ResetDate;
            }
            // 2. 对于 2020.1.1 ~ 2021.12.31 之间的日期
            else if (!firstOrderDate.isBefore(LocalDate.of(2020, 1, 1)) && firstOrderDate.isBefore(LocalDate.of(2022, 1, 1))) {
                adjustedDate = rank2ResetDate;
            }
            // 3. 对于 2020年1月1日之前的日期
            else if (firstOrderDate.isBefore(LocalDate.of(2020, 1, 1))) {
                adjustedDate = rank3ResetDate;
            }

            // 如果日期被调整了，更新客户对象并加入调整列表
            if (adjustedDate != null) {
                customer.setAdjustedFirstOrderDate(adjustedDate);
                adjustList.add(customer);
            }
        }

        customerDao.adjustFirstOrderDate(adjustList);
        System.out.println(adjustList);


    }
}

