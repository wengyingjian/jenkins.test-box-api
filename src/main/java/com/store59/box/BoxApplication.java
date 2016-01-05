package com.store59.box;

import com.caucho.hessian.client.HessianProxyFactory;
import com.store59.base.common.api.*;
import com.store59.box.remoting.*;
import com.store59.coupon.remoting.CouponService;
import com.store59.creditmall.remoting.CreditRecordService;
import com.store59.dorm.common.api.DormitemApi;
import com.store59.kylin.common.cache.ICache;
import com.store59.kylin.common.cache.RedisCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.net.MalformedURLException;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.store59")
@PropertySource("classpath:boxapi.properties")
public class BoxApplication extends SpringBootServletInitializer {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${boxservice.url}")
    private String boxServiceUrl;
    @Value("${baseservice.url}")
    private String baseServiceUrl;
    @Value("${couponservice.url}")
    private String couponServiceUrl;
    @Value("${creditmallservice.url}")
    private String creditmallserviceUrl;
    @Value("${dormservice.url}")
    private String dormserviceUrl;

    public static void main(String[] args) {
        SpringApplication.run(BoxApplication.class, args);
    }

    @Bean
    public BoxService boxServiceApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = boxServiceUrl+"/box";
        return (BoxService) hessianProxyFactoryBean.create(BoxService.class, url);
    }

    @Bean
    public BoxApplyService boxApplyServiceApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = boxServiceUrl+"/boxapply";
        return (BoxApplyService) hessianProxyFactoryBean.create(BoxApplyService.class, url);
    }

    @Bean
    public DislikeRepoService dislikeRepoService()throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = boxServiceUrl+"/dislikerepo";
        return (DislikeRepoService) hessianProxyFactoryBean.create(DislikeRepoService.class, url);
    }

    @Bean
    public DistributionRecordService distributionRecordService()throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = boxServiceUrl+"/distributionrecord";
        return (DistributionRecordService) hessianProxyFactoryBean.create(DistributionRecordService.class, url);
    }

    @Bean
    public OrderService orderServiceApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = boxServiceUrl+"/order";
        return (OrderService) hessianProxyFactoryBean.create(OrderService.class, url);
    }

    @Bean
    public ICache iCacheClient(){
        return new RedisCache(host);
    }

    @Bean
    public DormitemApi dormitemApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = dormserviceUrl+"/dormitem";
        return (DormitemApi) hessianProxyFactoryBean.create(DormitemApi

                .class, url);
    }

    @Bean
    public DormentryApi dormentryApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = baseServiceUrl+"/dormentry";
        return (DormentryApi) hessianProxyFactoryBean.create(DormentryApi.class, url);
    }

    @Bean
    public SiteApi siteApi() throws MalformedURLException{
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = baseServiceUrl+"/site";
        return (SiteApi) hessianProxyFactoryBean.create(SiteApi.class, url);
    }

    @Bean
    public ZoneApi zoneApi() throws MalformedURLException{
        HessianProxyFactory hessianProxyFactoryBean = new HessianProxyFactory();
        hessianProxyFactoryBean.setOverloadEnabled(true);
        String url = baseServiceUrl+"/zone";
        return (ZoneApi) hessianProxyFactoryBean.create(ZoneApi.class, url);
    }

    @Bean
    public RepoApi repoApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();
        hessianProxyFactory.setOverloadEnabled(true);
        if (!baseServiceUrl.endsWith("/")) {
            baseServiceUrl = baseServiceUrl + "/";
        }
        return (RepoApi) hessianProxyFactory.create(RepoApi.class, baseServiceUrl + "repo");
    }

    @Bean
    public CityApi cityApi() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();
        hessianProxyFactory.setOverloadEnabled(true);
        baseServiceUrl = baseServiceUrl + "/city";
        return (CityApi) hessianProxyFactory.create(CityApi.class, baseServiceUrl);
    }

    @Bean
    public CouponService couponService() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();
        hessianProxyFactory.setOverloadEnabled(true);
        if (!couponServiceUrl.endsWith("/")) {
            couponServiceUrl = couponServiceUrl + "/";
        }
        return (CouponService) hessianProxyFactory.create(CouponService.class, couponServiceUrl + "coupon");
    }

    @Bean
    public CreditRecordService creditRecordService() throws MalformedURLException {
        HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();
        hessianProxyFactory.setOverloadEnabled(true);
        if (!creditmallserviceUrl.endsWith("/")) {
            creditmallserviceUrl = creditmallserviceUrl + "/";
        }
        return (CreditRecordService) hessianProxyFactory.create(CreditRecordService.class, creditmallserviceUrl + "creditrecord");
    }

}
