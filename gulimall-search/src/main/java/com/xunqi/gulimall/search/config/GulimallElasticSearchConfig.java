package com.xunqi.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 高级 REST 客户端配置。
 * 创建单例 {@link RestHighLevelClient} 指向本地 ES（localhost:9200），
 * 并定义公共请求选项 COMMON_OPTIONS 供所有 ES 请求复用。
 */
@Configuration
public class GulimallElasticSearchConfig {

    /** 所有 ES 请求共用的 RequestOptions（此处沿用默认配置） */
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 注册 ES 高级客户端 Bean。
     * @return 连接 localhost:9200 的 RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));
        return  client;
    }

}
