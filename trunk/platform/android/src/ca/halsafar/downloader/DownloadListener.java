package ca.halsafar.downloader;

public interface DownloadListener
{
     void onDownloadAlreadyUpdated(final String filename);
     void onDownloadSuccess(final String filename, final Long sizeBytes);
     void onDownloadFail(final String filename);
}
