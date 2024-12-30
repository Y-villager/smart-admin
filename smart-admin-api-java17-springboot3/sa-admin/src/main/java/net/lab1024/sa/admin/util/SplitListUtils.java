package net.lab1024.sa.admin.util;

import com.amazonaws.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SplitListUtils {

    public static <T> List<List<T>> splitList(final List<T> list, final int subListSize) {
        if (CollectionUtils.isNullOrEmpty(list)){
            return new ArrayList<>();
        }
        // 返回数据
        List<List<T>> ret = new ArrayList<List<T>>();
        int size = list.size();
        if (size <= subListSize) {
            ret.add(list);
        }else {
            int pre = size / subListSize; // 组数
            int last = size % subListSize;
            // 有pre个集合，每个长度为subListSize
            for (int i = 0; i < pre; i++) {
                List<T> subList = new ArrayList<>(subListSize);
                for (int j = 0; j < subListSize; j++) {
                    subList.add(list.get(i*subListSize + j));
                }
                ret.add(subList);
            }
            if (last > 0){
                List<T> subList = new ArrayList<>(subListSize);
                for (int j = 0; j < last; j++) {
                    subList.add(list.get(pre*subListSize + j));
                }
                ret.add(subList);
            }
        }
        return ret;
    }
}
