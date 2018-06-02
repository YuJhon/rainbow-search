package com.rainbow.house.search.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * <p>功能描述</br>ES的配置信息</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 13:56
 */
@Configuration
public class ElasticsearchConfig {

  @Value("${elasticsearch.host}")
  private String esHost;

  @Value("${elasticsearch.port}")
  private int esPort;

  @Value("${elasticsearch.cluster.name}")
  private String esName;

  @Bean
  public TransportClient esClient() throws UnknownHostException {
    Settings settings = Settings.builder()
            /** ES集群的名称 **/
            .put("cluster.name", this.esName)
            /** ES自动发现节点 **/
            .put("client.transport.sniff", true)
            .build();

    InetSocketTransportAddress master = new InetSocketTransportAddress(
            InetAddress.getByName(this.esHost), this.esPort
    );

    TransportClient client = new PreBuiltTransportClient(settings)
            .addTransportAddress(master);
    return client;
  }
}
