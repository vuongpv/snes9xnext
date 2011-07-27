/**
 * HALSAFAR DownloaderHelpers
 * 
 * SEE LICENSE FILE
 * 	Free to use, non-commercial license.
 * 
 * Copyright 2011 - Stephen Damm (shinhalsafar@gmail.com)
 */

package ca.halsafar.downloader;

public interface DecompressListener
{
     void onDecompressSuccess(final String filename, final Integer numFiles);
     void onDecompressFail(final String filename);
}
