# Dockerfile for Kibana
FROM docker.elastic.co/kibana/kibana:8.13.4

# Thiết lập biến môi trường kết nối Elasticsearch
ENV ELASTICSEARCH_HOSTS=http://host.docker.internal:9200

# Cổng mặc định
EXPOSE 5601
