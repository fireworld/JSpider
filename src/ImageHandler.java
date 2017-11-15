import cc.colorcat.jspider.Handler;
import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.internal.Utils;
import download.DownloadManager;
import download.Method;
import download.Request;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxx on ${DATA}.
 * xx.ch@outlook.com
 */
class ImageHandler implements Handler {
    private final DownloadManager manager;
    private final File directory;

    ImageHandler(DownloadManager manager, File saveDirectory) {
        this.manager = manager;
        this.directory = saveDirectory;
    }

    @Override
    public boolean handle(Scrap scrap) {
        Map<String, String> data = scrap.data();
        String url = data.get("url");
        if (url != null && url.matches("^(http)(s)?://(.)*\\.(jpg|png|jpeg)$")) {
            String folderName = data.get("dir");
            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Referer", Collections.singletonList(scrap.uri().toString()));
            manager.enqueue(Request.create(url, Method.GET, headers, Utils.createSavePath(directory, folderName, fileName)));
            return true;
        }
        return false;
    }
}
