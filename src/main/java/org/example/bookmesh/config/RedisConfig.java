package org.example.bookmesh.config;

import org.example.bookmesh.dto.book.BookResponse;
import org.example.bookmesh.dto.book.ListingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        JsonMapper mapper = JsonMapper.builder().build();

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        JavaType bookType = mapper.getTypeFactory().constructType(BookResponse.class);

        JavaType bookListType = mapper.getTypeFactory()
                .constructCollectionType(List.class, BookResponse.class);

        JavaType listingType = mapper.getTypeFactory().constructType(ListingResponse.class);

        JavaType listingListType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ListingResponse.class);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withCacheConfiguration("books", withType(base, mapper, bookType))
                .withCacheConfiguration("bookList", withType(base, mapper, bookListType))
                .withCacheConfiguration("listings", withType(base, mapper, listingType))
                .withCacheConfiguration("listingList", withType(base, mapper, listingListType))
                .withCacheConfiguration("bookListByAuthor", withType(base, mapper, bookListType))
                .withCacheConfiguration("listingListBySupplier", withType(base, mapper, listingListType))
                .build();
    }

    private RedisCacheConfiguration withType(RedisCacheConfiguration base,
                                             ObjectMapper mapper, JavaType type) {
        return base.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(mapper, type)));
    }
}
