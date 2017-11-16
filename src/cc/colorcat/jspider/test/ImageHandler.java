package cc.colorcat.jspider.test;

import cc.colorcat.jspider.Handler;
import cc.colorcat.jspider.Scrap;
import cc.colorcat.jspider.internal.UserAgent;
import cc.colorcat.jspider.test.download.DownloadManager;
import cc.colorcat.jspider.test.download.FileUtils;
import cc.colorcat.jspider.test.download.Task;

import java.io.File;
import java.util.Map;

/**
 * Created by cxx on 2017/11/11.
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
            File savePath = FileUtils.createSavePath(directory, folderName, fileName);
            manager.enqueue(Task.create(url, savePath)
                    .header("Referer", scrap.uri().toString())
                    .header(UserAgent.NAME, UserAgent.Value.CHROME_MAC)
            );
            return true;
        }
        return false;
    }
}
