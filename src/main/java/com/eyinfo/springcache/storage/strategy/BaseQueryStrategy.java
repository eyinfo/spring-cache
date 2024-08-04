package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.entity.SearchCondition;

import java.util.Map;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/12
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class BaseQueryStrategy {

    //md5(cacheSubKey+conditions)作为缓存key,缓存60秒
    public String getQueryKey(String cacheSubKey, SearchCondition conditions) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("pageNumber=%s&", conditions.getPageNumber()));
        builder.append(String.format("pageSize=%s&", conditions.getPageSize()));
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
        if (queryWrapper == null) {
            builder.append(conditions.getConditionSql());
        } else {
            builder.append(combQueryWrapper(queryWrapper));
        }
        return getQueryKey(cacheSubKey, builder.toString());
    }

    //md5(cacheSubKey+primary)作为缓存key,永久缓存
    public String getQueryKey(String cacheSubKey, String where) {
        if (TextUtils.isEmpty(cacheSubKey)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(cacheSubKey).append("&");
        builder.append(where);
        String key = MD5Encrypt.md5(builder.toString());
        return String.format("%s_%s", cacheSubKey, key);
    }

    private String combQueryWrapper(QueryWrapper queryWrapper) {
        StringBuilder builder = new StringBuilder();
        builder.append(queryWrapper.getSqlSegment());
        builder.append(queryWrapper.getCustomSqlSegment());
        Map<String, Object> paramNameValuePairs = queryWrapper.getParamNameValuePairs();
        if (!ObjectJudge.isNullOrEmpty(paramNameValuePairs)) {
            for (Map.Entry<String, Object> entry : paramNameValuePairs.entrySet()) {
                builder.append(entry.getKey()).append(entry.getValue());
            }
        }
        return builder.toString();
    }

    public <T> String getQueryKeyPlus(String cacheSubKey, QueryWrapper<T> queryWrapper) {
        if (TextUtils.isEmpty(cacheSubKey)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(cacheSubKey).append("&");
        builder.append(combQueryWrapper(queryWrapper));
        String key = MD5Encrypt.md5(builder.toString());
        return String.format("%s_%s", cacheSubKey, key);
    }
}
