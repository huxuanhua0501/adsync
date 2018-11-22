package net.racetime.adsync.pojo;

/**
 * 定义公共的reidskey,或者后续的一些词汇
 */
public class RedisKeyName {

    public final static String HEARTBEAT = "heartbeat";//reidis心跳key

    public final static String MATERIAL = "material";//物料白名单

    public final static String BINDING_DEVICE = "binding_device";//设备绑定bd_device_id

    public final static String STANDBY_DEVICE = "standby_device";//备用的bd_device_id

    public final static String NOTMATCH_DEVICE = "notmatch_device";//未匹配bd_device_id

    public final static String ADSLOT_CONFIG = "adslot_config";//广告位关系
    public final static String ADDDATETIME = "addDatetime";//时间戳
    public final static String MATERIAL_INFO = "material_info";//广告位与今天要投放的物料


}
