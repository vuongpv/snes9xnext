/**
 * HALSAFAR DownloaderHelpers
 * 
 * SEE LICENSE FILE
 * 	Free to use, non-commercial license.
 * 
 * Copyright 2011 - Stephen Damm (shinhalsafar@gmail.com)
 */

package ca.halsafar.downloader;

public interface DownloadListener
{
     void onDownloadAlreadyUpdated(final String filename);
     void onDownloadSuccess(final String filename, final Long sizeBytes);
     void onDownloadFail(final String filename);
}
