package ca.halsafar.downloader;

public interface DecompressListener
{
     void onDecompressSuccess(final String filename, final Integer numFiles);
     void onDecompressFail(final String filename);
}
