package net.racetime.adsync.service;
/**
 * @author hu_xuanhua_hua
 * @ClassName: IAdsyncService
 * @Description: TODO
 * @date 2018-03-26 17:21
 * @versoin 1.0
 */
public interface IAdsyncService {
    /**
     * 获取物料白名单
     *
     * @return
     */
    void getMaterial();

    /**
     * 广告位关系
     */
    void getADslotConfig();

    /**
     * 设备绑定bd_device_id
     */
    void bindingDevice();

    /**
     * .备用的bd_device_id
     */
    void standbyDevice();

    /**
     * 未匹配bd_device_id
     */
    void notmatchDevice();

    /**
     * 添加时间戳
     */
    void addDatetime();

    /**
     * 投放策略
     */
    void materialInfo();
}
