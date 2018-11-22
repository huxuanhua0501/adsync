package net.racetime.adsync.dao;

import net.racetime.adsync.pojo.DisplayName;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hu_xuanhua_hua
 * @ClassName: AdsyncDaoMapper
 * @Description: TODO
 * @date 2018-03-26 17:21
 * @versoin 1.0
 */
public interface AdsyncDaoMapper {

    /**
     * 获取物料白名单
     *
     * @return
     */
    List<String> getMaterial();

    /**
     * 广告位关系
     */
    List<Map<String, String>> getADslotConfig();

    /**
     * 设备绑定bd_device_id
     */
    List<Map<String, String>> bindingDevice();

    /**
     * .备用的bd_device_id
     */
    List<Map<String, String>> standbyDevice();

    /**
     * 查询bd_device_id
     */
    String getBdDeviceId(@Param(DisplayName.BD_DEVICE_ID) String bd_deviceId);

    /**
     * 未匹配bd_device_id
     */
    int notmatchDevice(@Param(DisplayName.THIRD_DEVICE_ID) int third_device_id, @Param(DisplayName.BD_DEVICE_ID) int bd_device_id);

    /**
     * 获取周期内要投放额所有的物料
     *
     * @return
     */
   List<Map<String, String>> getAdvertisementMateriel();

    /**
     * 获取所有的有效物料
     */
   List<Map<String, String>> getAllMateriel();
}

