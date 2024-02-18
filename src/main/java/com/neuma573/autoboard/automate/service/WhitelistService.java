package com.neuma573.autoboard.automate.service;

import com.neuma573.autoboard.automate.model.entity.AppLog;
import com.neuma573.autoboard.automate.model.entity.Whitelist;
import com.neuma573.autoboard.automate.repository.AppLogRepository;
import com.neuma573.autoboard.automate.repository.WhitelistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    private final AppLogRepository appLogRepository;

    @Transactional
    public boolean isValidIp(String ip, String appName) {
        List<Whitelist> whitelists = whitelistRepository.findByAppName(appName);
        for (Whitelist whitelist : whitelists) {
            if (whitelist.getWhitelist().contains("/")) {
                if (isIpWithinRange(ip, whitelist.getWhitelist())) {
                    appLogRepository.save(
                            AppLog.builder()
                                    .ip(ip)
                                    .appName(appName)
                                    .build()
                    );
                    return true;
                }
            } else if (whitelist.getWhitelist().equals(ip)) {
                appLogRepository.save(
                        AppLog.builder()
                                .ip(ip)
                                .appName(appName)
                                .build()
                );
                return true;
            }
        }
        return false;
    }
    public boolean isIpWithinRange(String ip, String cidr) {
        String[] parts = cidr.split("/");
        String ipSection = parts[0];
        int prefix = (parts.length < 2) ? 0 : Integer.parseInt(parts[1]);

        int mask = -(1 << (32 - prefix));

        int ipAddr = ipToInt(ip);
        int ipSectionAddr = ipToInt(ipSection);

        return (ipAddr & mask) == (ipSectionAddr & mask);
    }

    private int ipToInt(String ipAddress) {
        String[] ipParts = ipAddress.split("\\.");
        int ipNum = 0;
        for (String ipPart : ipParts) {
            ipNum = ipNum << 8;
            ipNum |= Integer.parseInt(ipPart);
        }
        return ipNum;
    }
}
