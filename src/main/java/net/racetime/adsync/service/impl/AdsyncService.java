package net.racetime.adsync.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.racetime.adsync.dao.AdsyncDaoMapper;
import net.racetime.adsync.pojo.DisplayName;
import net.racetime.adsync.pojo.RedisKeyName;
import net.racetime.adsync.service.IAdsyncService;
import net.racetime.util.PubMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hu_xuanhua_hua
 * @ClassName: AdsyncService
 * @Description: TODO
 * @date 2018-03-26 17:21
 * @versoin 1.0
 */
@Service
public class AdsyncService implements IAdsyncService {
    private Logger logger = LoggerFactory.getLogger(AdsyncService.class);
    @Autowired
    AdsyncDaoMapper adsyncDaoMapper;
    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Override
    public void getMaterial() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            ShardedJedisPipeline sp = shardedJedis.pipelined();
            Map<String, Object> redisMap = new HashMap<>();//存储cpt计数
            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
            shardedJedis.del(RedisKeyName.MATERIAL);
            List<String> listMaterial = adsyncDaoMapper.getMaterial();
            if (listMaterial != null && listMaterial.size() > 0) {//放进redis
                int materialcount = listMaterial.size();
                for (int i = 0; i < materialcount; i++) {
                    sp.sadd(RedisKeyName.MATERIAL, listMaterial.get(i));
                }
                sp.sync();
                shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.MATERIAL, "存放成物料白名单MATERIAL" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } finally {
            shardedJedis.close();
        }
    }

    @Override
    public void getADslotConfig() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            ShardedJedisPipeline sp = shardedJedis.pipelined();
            Map<String, Object> redisMap = new HashMap<>();//存储cpt计数
            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
            shardedJedis.del(RedisKeyName.ADSLOT_CONFIG);
            List<Map<String, String>> listADslotConfig = adsyncDaoMapper.getADslotConfig();
            Map<String, List<Map<String, String>>> adslotConfigMap = new HashMap<>();
            if (listADslotConfig != null && listADslotConfig.size() > 0) {
                int adslotConfigcount = listADslotConfig.size();
                for (int i = 0; i < adslotConfigcount; i++) {
                    if (adslotConfigMap != null && adslotConfigMap.size() > 0) {
                        String ssp_id = listADslotConfig.get(i).remove(DisplayName.SSP_ID);
                        if (adslotConfigMap.get(ssp_id) != null) {
                            adslotConfigMap.get(ssp_id).add(listADslotConfig.get(i));
                            List<Map<String, String>> list = new ArrayList<>();
                            list.add(listADslotConfig.get(i));
                        } else {
                            List<Map<String, String>> list = new ArrayList();
                            list.add(listADslotConfig.get(i));
                            adslotConfigMap.put(ssp_id, list);
                            adslotConfigMap.put(ssp_id, list);
                        }

                    } else {
                        List<Map<String, String>> list = new ArrayList<>();
                        String ssp_id = listADslotConfig.get(i).remove(DisplayName.SSP_ID);
                        list.add(listADslotConfig.get(i));
                        adslotConfigMap.put(ssp_id, list);
                    }
                }
            }
            if (adslotConfigMap != null && adslotConfigMap.size() > 0) {//放进redis
                for (Map.Entry<String, List<Map<String, String>>> entry : adslotConfigMap.entrySet()) {
                    String key = entry.getKey();
                    List<Map<String, String>> value = entry.getValue();
                    sp.hset(RedisKeyName.ADSLOT_CONFIG, key, JSON.toJSONString(value));
                }
                sp.sync();
                shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.ADSLOT_CONFIG, "存放广告位关系ADSLOT_CONFIG" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } finally {
            shardedJedis.close();
        }

    }

    @Override
    public void bindingDevice() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            ShardedJedisPipeline sp = shardedJedis.pipelined();
            Map<String, Object> redisMap = new HashMap<>();//存储cpt计数
            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
            shardedJedis.del(RedisKeyName.BINDING_DEVICE);
            List<Map<String, String>> listbindingDevice = adsyncDaoMapper.bindingDevice();
            if (listbindingDevice != null && listbindingDevice.size() > 0) {//放进redis
                int bindingDevicecount = listbindingDevice.size();
                for (int i = 0; i < bindingDevicecount; i++) {
                    sp.hset(RedisKeyName.BINDING_DEVICE, listbindingDevice.get(i).get(DisplayName.THIRD_DEVICE_ID), listbindingDevice.get(i).get(DisplayName.BD_DEVICE_ID));
                }
                sp.sync();
                shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.BINDING_DEVICE, "存放设备绑定BINDING_DEVICE" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } finally {
            shardedJedis.close();
        }
    }

    @Override
    public void standbyDevice() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            ShardedJedisPipeline sp = shardedJedis.pipelined();
            Map<String, Object> redisMap = new HashMap<>();//存储cpt计数
            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
            shardedJedis.del(RedisKeyName.STANDBY_DEVICE);
            List<Map<String, String>> liststandbyDevice = adsyncDaoMapper.standbyDevice();
            if (liststandbyDevice != null && liststandbyDevice.size() > 0) {//放进redis
                int standbyDevicecount = liststandbyDevice.size();
                for (int i = 0; i < standbyDevicecount; i++) {
                    sp.lpush(RedisKeyName.STANDBY_DEVICE, liststandbyDevice.get(i).get(DisplayName.BD_DEVICE_ID));
                }
                sp.sync();
                shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.STANDBY_DEVICE, "存放备用的STANDBY_DEVICE" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } finally {
            shardedJedis.close();
        }

    }

    @Override
    public void notmatchDevice() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        try {
            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
//            JSONObject json = new JSONObject();
//            json.put("third_device_id", "33333");
//            json.put("bd_device_id", "41242874BKCXFUF");
//            shardedJedis.lpush("notmatch_device", json.toJSONString());
            shardedJedis.del(RedisKeyName.NOTMATCH_DEVICE);
            long size = shardedJedis.llen(RedisKeyName.NOTMATCH_DEVICE);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    String str = shardedJedis.lpop(RedisKeyName.NOTMATCH_DEVICE);
                    logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "将未匹配到未匹配bd_device_id到数据库==" + str);
                    JSONObject obj = JSONObject.parseObject(str);
                    System.err.println(obj.getString(DisplayName.BD_DEVICE_ID));
                    String bd_device_id = adsyncDaoMapper.getBdDeviceId(obj.getString(DisplayName.BD_DEVICE_ID));
                    String third_device_id = obj.getString(DisplayName.THIRD_DEVICE_ID);
                    if (bd_device_id != null) {
                        adsyncDaoMapper.notmatchDevice(Integer.parseInt(third_device_id), Integer.parseInt(bd_device_id));
                        logger.info("THIRD_DEVICE_ID=" + obj.getString(DisplayName.THIRD_DEVICE_ID) + ",BD_DEVICE_ID=" + obj.getString(DisplayName.BD_DEVICE_ID) + new SimpleDateFormat(",更新时间=yyyy-MM-dd HH:mm:ss").format(new Date()));
                    }
                }
            }
            shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.NOTMATCH_DEVICE, "将未匹配third_device_id更新到数据库" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            shardedJedis.close();
        } catch (Exception e) {
            shardedJedis.close();
            e.printStackTrace();
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "(updateLineBydeviceid)将未匹配third_device_id的信息更新到数据库==" + e);
        }

    }

    @Override
    public void addDatetime() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        Collection<Jedis> collection = shardedJedis.getAllShards();
        Iterator<Jedis> jedis = collection.iterator();
        while (jedis.hasNext()) {
            jedis.next().select(0);
        }
        try {
            shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.ADDDATETIME, String.valueOf(System.currentTimeMillis() / 1000));
            shardedJedis.close();
        } catch (Exception e) {
            shardedJedis.close();
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "(addDatetime)添加时间戳==" + e);
        }
    }

    @Override
    public void materialInfo() {
//        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "(materialInfo)进入==" );
        ShardedJedis shardedJedis = shardedJedisPool.getResource();

            Collection<Jedis> collection = shardedJedis.getAllShards();
            Iterator<Jedis> jedis = collection.iterator();
            while (jedis.hasNext()) {
                jedis.next().select(0);
            }
        try {
            shardedJedis.del(RedisKeyName.MATERIAL_INFO);
            //获取所有服务周期内投放的物料
            List<Map<String, String>> advertisementMaterielList = adsyncDaoMapper.getAdvertisementMateriel();
            Map<String, Set> materialInfo = new HashMap<>();//过滤重复的物料和广告位
            if (advertisementMaterielList != null && advertisementMaterielList.size() > 0) {
                int advertisementMaterielListSize = advertisementMaterielList.size();
                for (int i = 0; i < advertisementMaterielListSize; i++) {
                    JSONObject week_and_hour = JSONObject.parseObject(advertisementMaterielList.get(i).get(DisplayName.WEEK_AND_HOUR).toString());
                    //获取当天的播放策略
                    String today = (String) week_and_hour.get(getEnglishWeek());
                    if (!PubMethod.isEmpty(today)) {
                    String[] hours = today.split(",");
                    Set<String> hourset = new HashSet<>(Arrays.asList(hours));
                    //如果当前小时下有播放策略,将策略过滤重复的广告
                    if (hourset.contains(hour())) {
                        String key = advertisementMaterielList.get(i).get(DisplayName.ADID);
                        String materialInfoId = advertisementMaterielList.get(i).get(DisplayName.MATERIALID);
                        String[] materialInfoIds = materialInfoId.split(",");
                        if (materialInfo != null && materialInfo.size() > 0 && materialInfo.get(key) != null) {
                            for (String id : materialInfoIds) {
                                materialInfo.get(key).add(id);
                            }
                        } else {
                            Set<String> set = new HashSet<>();
                            for (String id : materialInfoIds) {
                                set.add(id);
                                materialInfo.put(key, set);
                            }
                        }
                    }
                    }
                }
            }

            //获取所有广告
            List<Map<String, String>> allMaterielList = adsyncDaoMapper.getAllMateriel();
            Map<String, Map> allMaterielMap = new HashMap<>();
            if (allMaterielList != null && allMaterielList.size() > 0) {
                int allMaterielListSize = allMaterielList.size();
                for (int i = 0; i < allMaterielListSize; i++) {
                    String materialId = allMaterielList.get(i).remove(DisplayName.MATERIALID);
                    allMaterielMap.put(materialId, allMaterielList.get(i));
                }
            }

            //要存放redis的投放策略
            Map<String, List<Map<String, String>>> redisHashMap = new HashMap<>();

            if (materialInfo != null && materialInfo.size() > 0) {
                for (Map.Entry<String, Set> entry : materialInfo.entrySet()) {
                    List<Map<String, String>> list = new ArrayList<>();
                    String key = entry.getKey();
                    Set value = entry.getValue();
                    for (Object id : value) {
                        if (allMaterielMap.get(id) != null) {
                            list.add(allMaterielMap.get(id));
                        }
                    }
                    //存放进redis
                    redisHashMap.put(key, list);
                }

            }
            if (redisHashMap != null && redisHashMap.size() > 0) {
                for (Map.Entry<String, List<Map<String,String>>> entry : redisHashMap.entrySet()) {
                    shardedJedis.hset(RedisKeyName.MATERIAL_INFO, entry.getKey(), JSON.toJSONString(entry.getValue()));
//                    logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "(materialInfo)进入=="+entry.getKey());
                    shardedJedis.hset(RedisKeyName.HEARTBEAT, RedisKeyName.MATERIAL_INFO, "存放成策略" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                }
            }
        } catch (Exception e) {
            logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "(广告投放优先级" + e);
            e.printStackTrace();
        } finally {
            shardedJedis.close();

        }

    }

    /**
     * 获取数字星期
     *
     * @return
     */
    public String getEnglishWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w + "";
    }

    /**
     * 获取当前小时
     * @return
     */
    private String hour() {
        String hour = new SimpleDateFormat("HH").format(new Date());
        if (hour.startsWith("0")){
            hour = hour.substring(1);
        }
        return hour;
    }

    public static void main(String[] args) {
       String str2="0,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,19,20,21,22,23";
       String[] ss = str2.split(",");
        Set<String> set = new HashSet<>(Arrays.asList(ss));
        String hour = new SimpleDateFormat("HH").format(new Date());
        if (hour.startsWith("0")){
            System.err.println(hour.substring(1));
        }
        System.err.println(hour);
        System.err.println(set.contains(hour));
    }

}
