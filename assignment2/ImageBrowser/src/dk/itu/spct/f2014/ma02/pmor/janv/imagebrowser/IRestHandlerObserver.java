package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;

public interface IRestHandlerObserver {
	void onDownloadImageDone(File newImage);
}
