package com.example.demo.service;

public interface DownloadService {
    public Boolean saveDownload(Long userId,Long fileId);
    public Long getTotalDownloadsForSystem();
}
