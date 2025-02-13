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

        // 目标日期
        LocalDate after2021_12_31 = LocalDate.of(2021, 12, 31);  // 2021年12月31日
        LocalDate before2025 = LocalDate.of(2025, 1, 1);          // 2025年1月1日
        LocalDate start2021 = LocalDate.of(2021, 1, 1);            // 2021年1月1日
        LocalDate newDate2025 = LocalDate.of(2025, 1, 1);           // 2025年1月1日
        LocalDate newDate2023 = LocalDate.of(2023, 1, 1);           // 2023年1月1日
        LocalDate newDate2021 = LocalDate.of(2021, 1, 1);           // 2021年1月1日

        for (CustomerEntity customer : customerList) {
            LocalDate date = customer.getFirstOrderDate();
            if (date == null){
                continue;
            }

            // 如果日期符合条件，先进行调整后加入
            LocalDate adjustedDate = null;

            // 对于 > 2021.12.31 且 < 2025.01.01 之间的日期
            if (date.isBefore(before2025) && date.isAfter(after2021_12_31)) {
                adjustedDate = newDate2025;
            }
            // 对于 2021.01.01 到 2021.12.31 之间的日期
            else if (!date.isBefore(start2021) && date.isBefore(after2021_12_31)) {
                adjustedDate = newDate2023;
            }
            // 对于 2020年之前的日期
            else if (date.isBefore(start2021)) {
                adjustedDate = newDate2021;
            }

            // 如果日期被调整了，更新对象并加入调整列表
            if (adjustedDate != null) {
                customer.setAdjustedFirstOrderDate(adjustedDate);
                adjustList.add(customer);
            }
        }
        customerDao.adjustFirstOrderDate(adjustList);
        System.out.println(adjustList);


    }
}

