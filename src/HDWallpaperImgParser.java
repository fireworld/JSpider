import cc.colorcat.spider.Parser;
import cc.colorcat.spider.Scrap;
import cc.colorcat.spider.WebSnapshot;

import java.util.Collections;
import java.util.List;

/**
 * Created by cxx on 2017/11/10.
 * xx.ch@outlook.com
 */
public class HDWallpaperImgParser implements Parser {
    @Override
    public List<Scrap> parse(Scrap seed, WebSnapshot snapshot) {
        if (!HDWallpaperLiParser.TAG.equals(seed.tag()) || !seed.uri().toString().contains(HDWallpaperLiParser.HOST)) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
