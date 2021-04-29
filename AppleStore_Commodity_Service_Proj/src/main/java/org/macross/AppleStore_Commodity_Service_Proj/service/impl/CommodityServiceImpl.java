package org.macross.AppleStore_Commodity_Service_Proj.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Commodity_Service_Proj.config.CacheKeyManager;
import org.macross.AppleStore_Commodity_Service_Proj.mapper.CommodityMapper;
import org.macross.AppleStore_Commodity_Service_Proj.service.CommodityService;
import org.macross.AppleStore_Commodity_Service_Proj.utils.BaseCache;
import org.macross.AppleStore_Common_Config.model.entity.Commodity;
import org.macross.AppleStore_Common_Config.model.entity.CommodityCategory;
import org.macross.AppleStore_Common_Config.model.entity.HomeBanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class CommodityServiceImpl implements CommodityService {

    @Autowired
    BaseCache baseCache;

    @Autowired
    CommodityMapper commodityMapper;

    /**
     * AliyunOSS config
     */
    @Value("${aliyun.oss.endpoint}")
    private String ENDPOINT;

    @Value("${aliyun.oss.accesskeyid}")
    private String ACCESSKEYID;

    @Value("${aliyun.oss.accesskeysecret}")
    private String ACCESSKEYSECRET ;

    @Value("${aliyun.oss.bucketname}")
    private String BUCKETNAME;

    @Value("${aliyun.oss.path}")
    private String PATH;

    @Value("${aliyun.oss.region}")
    private String REGION;

    @Override
    public List<HomeBanner> homeBanner() {

        try {
            Object Cache = baseCache.getTenMinuteCache().get(CacheKeyManager.INDEX_BANNER_LIST_KEY, () ->
            {
                return commodityMapper.homeBanner();
            });
            if (Cache instanceof List){
                return (List<HomeBanner>)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<CommodityCategory> homeCommodity() {

        try {
            Object Cache = baseCache.getOneHourCache().get(CacheKeyManager.INDEX_COMMODITY_LIST_KEY, () ->
            {
                return commodityMapper.homeCommodity();
            });
            if (Cache instanceof List){
                return (List<CommodityCategory>)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Commodity findCommodityDetail(Integer commodityId) {

        String INDEX_COMMODITY_DETAIL_KEY = String.format(CacheKeyManager.INDEX_COMMODITY_DETAIL_KEY, commodityId);

        try {

            Object Cache =  baseCache.getOneHourCache().get(INDEX_COMMODITY_DETAIL_KEY,() ->
            {
                return commodityMapper.findCommodityDetail(commodityId);
            });
            if (Cache instanceof Commodity){
                return (Commodity)Cache;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<Commodity> seckillCommodityList() {
        List<Commodity> commodityList =  commodityMapper.findAllSeckillCommodity();
        commodityList.forEach(e->e.setDescribe("[秒杀价]"+e.getDescribe()));
        return commodityList;
    }

    @Override
    public Commodity findSeckillCommodityDetail(Integer commodityId) {
        Commodity seckillCommodityDetail = commodityMapper.findSeckillCommodityDetail(commodityId);
        seckillCommodityDetail.setDescribe("[秒杀价]"+seckillCommodityDetail.getDescribe());
        return seckillCommodityDetail;
    }

    @Override
    public List<String> uploadImage(HttpServletRequest request) throws IOException {
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
        Iterator<String> iterator = req.getFileNames();
        List<String> list = new ArrayList<String>();
        while (iterator.hasNext()){
            MultipartFile file = req.getFile(iterator.next());
            if (file == null)continue;
            String filename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);

            if (!ossClient.doesBucketExist(BUCKETNAME)) {
                /*
                 * Create a new OSS bucket
                 */
                System.out.println("Creating bucket " + BUCKETNAME + "\n");
                ossClient.createBucket(BUCKETNAME);
                CreateBucketRequest createBucketRequest= new CreateBucketRequest(BUCKETNAME);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            /*
             * Upload an object to your bucket
             */
            System.out.println("Uploading a new object to OSS from a file\n");
            ossClient.putObject(new PutObjectRequest(BUCKETNAME,PATH+filename,inputStream));
            ossClient.shutdown();
            String path = BUCKETNAME+"."+ REGION + "/" + PATH + filename;
            list.add(path);
            log.info("UploadImage To Aliyun OSS Success,name=" +  filename +",url = "+path);
        }
        return list;
    }

}
