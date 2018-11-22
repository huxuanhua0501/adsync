package net.racetime.timer;

import net.racetime.adsync.service.IAdsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hu_xuanhua_hua
 * @ClassName: Timer
 * @Description: TODO
 * @date 2018-03-26 17:21
 * @versoin 1.0
 */
@Component
public class Timer {
    @Autowired
    private IAdsyncService adsyncService;

    @Scheduled(cron = "${getMaterial}")
    public void getMaterial() {
        adsyncService.getMaterial();
    }

    @Scheduled(cron = "${getADslotConfig}")
    public void getADslotConfig() {
        adsyncService.getADslotConfig();
    }

//    @Scheduled(cron = "${bindingDevice}")
    public void bindingDevice() {
        adsyncService.bindingDevice();
    }

//    @Scheduled(cron = "${standbyDevice}")
    public void standbyDevice() {
        adsyncService.standbyDevice();
    }

//    @Scheduled(cron = "${notmatchDevice}")
    public void notmatchDevice() {
        adsyncService.notmatchDevice();
    }

    @Scheduled(cron = "${addDatetime}")
    public void addDatetime() {
        adsyncService.addDatetime();
    }

    @Scheduled(cron = "${materialInfo}")
    void materialInfo() {
        adsyncService.materialInfo();
    }
}
